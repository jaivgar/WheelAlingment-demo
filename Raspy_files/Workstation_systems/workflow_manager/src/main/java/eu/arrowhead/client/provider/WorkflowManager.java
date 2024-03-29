/*
 *  Copyright (c) 2018 AITIA International Inc.
 *
 *  This work is part of the Productive 4.0 innovation project, which receives grants from the
 *  European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 *  (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 *  national funding authorities from involved countries.
 */

package eu.arrowhead.client.provider;

import eu.arrowhead.client.common.ArrowheadClientMain;
import eu.arrowhead.client.common.Utility;
import eu.arrowhead.client.common.exception.ArrowheadException;
import eu.arrowhead.client.common.exception.ExceptionType;
import eu.arrowhead.client.common.exception.UnavailableServerException;
import eu.arrowhead.client.common.misc.ClientType;
import eu.arrowhead.client.common.misc.SecurityUtils;
import eu.arrowhead.client.common.model.ArrowheadService;
import eu.arrowhead.client.common.model.ArrowheadSystem;
import eu.arrowhead.client.common.model.IntraCloudAuthEntry;
import eu.arrowhead.client.common.model.OrchestrationStore;
import eu.arrowhead.client.common.model.ServiceRegistryEntry;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.Set;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

/* This version of the ProviderMain class has some extra functionalities, that are not mandatory to have:
    1) Secure (HTTPS) mode
    2) Authorization registration
    3) Orchestration Store registration
    4) Get payloads from JSON files
 */
public class WorkflowManager extends ArrowheadClientMain {

  static String customResponsePayload;
  static PublicKey authorizationKey;
  static PrivateKey privateKey;
  
  public static boolean productArrived;

  private static boolean NEED_AUTH;
  private static boolean NEED_ORCH;
  private static boolean FROM_FILE;
  private static String SR_BASE_URI;

  //JSON payloads
  private static ServiceRegistryEntry srEntry;
  private static IntraCloudAuthEntry authEntry;
  private static List<OrchestrationStore> storeEntry = new ArrayList<>();
  
  

  public static void main(String[] args) {
    new WorkflowManager(args);
  }

  private WorkflowManager(String[] args) {
    Set<Class<?>> classes = new HashSet<>(Arrays.asList(WorkflowResource.class));
    String[] packages = {"eu.arrowhead.client.common"};
    
    productArrived = false;
    
    init(ClientType.PROVIDER, args, classes, packages);
    
    for (String arg : args) {
      switch (arg) {
        case "-ff":
          FROM_FILE = true;
          break;
        case "-auth":
          NEED_AUTH = true;
          break;
        case "-orch":
          NEED_ORCH = true;
          break;
      }
    }
    if (isSecure && NEED_ORCH) {
      throw new ServiceConfigurationError("The Store registration feature can only be used in insecure mode!");
    }

    String srAddress = props.getProperty("sr_address", "0.0.0.0");
    int srPort = isSecure ? props.getIntProperty("sr_secure_port", 9443) : props.getIntProperty("sr_insecure_port", 9442);
    SR_BASE_URI = Utility.getUri(srAddress, srPort, "serviceregistry", isSecure, false);

    loadAndCompilePayloads(FROM_FILE);
    registerToServiceRegistry();
    if (NEED_AUTH) {
      registerToAuthorization();
    }
    if (NEED_ORCH) {
      registerToStore();
    }

    if (props.getBooleanProperty("payload_from_file", false)) {
      customResponsePayload = props.getProperty("custom_payload");
    }

    //TODO: Get the production Order from USB for demo
    
    // Change such that it waits for a production order
    listenForProduct();
    productArrived = true;
    
    //New core system url
    String dataManagerUrl = props.getProperty("dataman_url","http://localhost:9456/datamanager/historian");
    
    String path_upload = props.getProperty("path_upload", "/media/pi/0013-A053/");
    String filename = props.getProperty("filename","prod_order.xml");
    System.out.println("The path where the file comes from is: "+ path_upload + filename);
    
    String status = consumeService_upload(dataManagerUrl,path_upload + filename);
    System.out.println(status);
    
    listenForInput();
  }

  @Override
  protected void startSecureServer(Set<Class<?>> classes, String[] packages) {
    super.startSecureServer(classes, packages);

    //Load the Provider private key
    String keystorePath = props.getProperty("keystore");
    String keystorePass = props.getProperty("keystorepass");
    KeyStore keyStore = SecurityUtils.loadKeyStore(keystorePath, keystorePass);
    privateKey = SecurityUtils.getPrivateKey(keyStore, keystorePass);

    //Load the Authorization Core System public key
    String authPublicKeyPath = props.getProperty("authorization_public_key");
    //Supporting the old format used previously: crt file containing the full certificate
    if (authPublicKeyPath.endsWith("crt")) {
      KeyStore authKeyStore = SecurityUtils.createKeyStoreFromCert(authPublicKeyPath);
      X509Certificate authCert = SecurityUtils.getFirstCertFromKeyStore(authKeyStore);
      authorizationKey = authCert.getPublicKey();
    } else { //This is just a PEM encoded public key
      authorizationKey = SecurityUtils.getPublicKey(authPublicKeyPath, true);
    }

    System.out.println("Authorization System PublicKey Base64: " + Base64.getEncoder().encodeToString(authorizationKey.getEncoded()));
  }

  @Override
  protected void shutdown() {
    unregisterFromServiceRegistry();
    if (server != null) {
      server.shutdownNow();
    }
    System.out.println("Provider Server stopped");
    System.exit(0);
  }

  private void loadAndCompilePayloads(boolean fromFile) {
    if (fromFile) {
      String srPath = props.getProperty("sr_entry");
      srEntry = Utility.fromJson(Utility.loadJsonFromFile(srPath), ServiceRegistryEntry.class);
      if (NEED_AUTH) {
        String authPath = props.getProperty("auth_entry");
        authEntry = Utility.fromJson(Utility.loadJsonFromFile(authPath), IntraCloudAuthEntry.class);
      }
      if (NEED_ORCH) {
        String storePath = props.getProperty("store_entry");
        storeEntry = Arrays.asList(Utility.fromJson(Utility.loadJsonFromFile(storePath), OrchestrationStore[].class));
      }
    } else {
      String serviceDef = props.getProperty("service_name");
      String serviceUri = props.getProperty("service_uri");
      if (!serviceUri.equals(WorkflowResource.SERVICE_URI)) {
        System.out.println("WARNING: Service URI in config file does not match REST sub-path.");
      }
      String interfaceList = props.getProperty("interfaces");
      Set<String> interfaces = new HashSet<>();
      if (interfaceList != null && !interfaceList.isEmpty()) {
        interfaces.addAll(Arrays.asList(interfaceList.replaceAll("\\s+", "").split(",")));
      }
      Map<String, String> metadata = new HashMap<>();
      String metadataString = props.getProperty("metadata");
      if (metadataString != null && !metadataString.isEmpty()) {
        String[] parts = metadataString.split(",");
        for (String part : parts) {
          String[] pair = part.split("-");
          metadata.put(pair[0], pair[1]);
        }
      }
      ArrowheadService service = new ArrowheadService(serviceDef, interfaces, metadata);

      URI uri;
      try {
        uri = new URI(baseUri);
      } catch (URISyntaxException e) {
        throw new AssertionError("Parsing the BASE_URI resulted in an error.", e);
      }
      ArrowheadSystem provider;
      if (isSecure) {
        if (!metadata.containsKey("security")) {
          metadata.put("security", "token");
        }
        String secProviderName = props.getProperty("secure_system_name");
        provider = new ArrowheadSystem(secProviderName, uri.getHost(), uri.getPort(), base64PublicKey);
      } else {
        String insecProviderName = props.getProperty("insecure_system_name");
        provider = new ArrowheadSystem(insecProviderName, uri.getHost(), uri.getPort(), null);
      }

      ArrowheadSystem consumer = null;
      if (NEED_AUTH || NEED_ORCH) {
        String consumerName = props.getProperty("consumer_name");
        String consumerAddress = props.getProperty("consumer_address");
        String consumerPK = props.getProperty("consumer_public_key", null);
        int consumerPort = props.getIntProperty("consumer_port", 9502);
        consumer = new ArrowheadSystem(consumerName, consumerAddress, consumerPort, consumerPK);
      }

      srEntry = new ServiceRegistryEntry(service, provider, serviceUri);
      if (NEED_AUTH) {
        authEntry = new IntraCloudAuthEntry(consumer, Collections.singletonList(provider), Collections.singletonList(service));
      }
      if (NEED_ORCH) {
        storeEntry = Collections.singletonList(new OrchestrationStore(service, consumer, provider, 0, false));
      }
    }
    System.out.println("Service Registry Entry: " + Utility.toPrettyJson(null, srEntry));
    System.out.println("IntraCloud Auth Entry: " + Utility.toPrettyJson(null, authEntry));
    System.out.println("Orchestration Store Entry: " + Utility.toPrettyJson(null, storeEntry));
  }

  private static void registerToServiceRegistry() {
    // create the URI for the request
    String registerUri = UriBuilder.fromPath(SR_BASE_URI).path("register").toString();
    try {
      Utility.sendRequest(registerUri, "POST", srEntry);
    } catch (ArrowheadException e) {
      if (e.getExceptionType() == ExceptionType.DUPLICATE_ENTRY) {
        System.out.println("Received DuplicateEntryException from SR, sending delete request and then registering again.");
        unregisterFromServiceRegistry();
        Utility.sendRequest(registerUri, "POST", srEntry);
      } else {
        throw e;
      }
    }
    System.out.println("Registering service is successful!");
  }

  private static void unregisterFromServiceRegistry() {
    String removeUri = UriBuilder.fromPath(SR_BASE_URI).path("remove").toString();
    Utility.sendRequest(removeUri, "PUT", srEntry);
    System.out.println("Removing service is successful!");
  }

  private void registerToAuthorization() {
    String authAddress = props.getProperty("auth_address", "0.0.0.0");
    int authPort = isSecure ? props.getIntProperty("auth_secure_port", 8445) : props.getIntProperty("auth_insecure_port", 8444);
    String authUri = Utility.getUri(authAddress, authPort, "authorization/mgmt/intracloud", isSecure, false);
    try {
      Utility.sendRequest(authUri, "POST", authEntry);
      System.out.println("Authorization registration is successful!");
    } catch (ArrowheadException e) {
      e.printStackTrace();
      System.out.println("Authorization registration failed!");
    }

  }

  private void registerToStore() {
    String orchAddress = props.getProperty("orch_address", "0.0.0.0");
    int orchPort = props.getIntProperty("orch_port", 8440);
    String orchUri = Utility.getUri(orchAddress, orchPort, "orchestrator/mgmt/store", false, false);
    try {
      Utility.sendRequest(orchUri, "POST", storeEntry);
      System.out.println("Store registration is successful!");
    } catch (ArrowheadException e) {
      e.printStackTrace();
      System.out.println("Store registration failed!");
    }
  }
  
  private String consumeService_upload(String providerUrl, String path_file) {
	    /*
	      Sending request to the provider, to the acquired URL. The method type and payload should be known beforehand.
	      If needed, compile the request payload here, before sending the request.
	      Supported method types at the moment: GET, POST, PUT, DELETE
	     */
		  
		//Need to compile the MULTIPART_FORM_DATA request with the file and make my own SendRequest to not send a JSON
		//So it is not possible to use: Response getResponse = Utility.sendRequest(providerUrl, "GET", null);
		  
		  
		  FileDataBodyPart filePart;
		  try {
			  filePart = new FileDataBodyPart("file", new File(path_file));
		  }catch(NullPointerException e) {
			  e.printStackTrace();
			  return "Path of file is null";
		  }
		  
		  FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
		  FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.bodyPart(filePart);
		  
		  Client client_own = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
		  String url_upload_file = providerUrl+"/file/"+props.getProperty("insecure_system_name");
		  Builder request = client_own.target(UriBuilder.fromUri(url_upload_file).build()).request().header("Content-type", MediaType.MULTIPART_FORM_DATA);
		  
		  Response response;
		  try {
			  response = request.post(Entity.entity(multipart, multipart.getMediaType()));
		  }catch(ProcessingException e){
			  throw new UnavailableServerException("Could not get any response from: " + providerUrl, Status.SERVICE_UNAVAILABLE.getStatusCode(), e);
		  }
		  return response.readEntity(String.class);
	  }
  
  private void listenForProduct() {
	  System.out.println("Type \"run\" to start the workflow manager operation or \"stop\"");
	  BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      String input = "";
      try{
    	  boolean stop = false;
    	  boolean run = false;
    	  do {
    		  System.out.print("> ");
    		  input = br.readLine();
    		  switch (input) {
    		  	case "stop":
		  		stop = true;
		  		break;
    		  	case "run":
		  		run = true;
		  		break;
    		  }
	      }while (stop != true && run != true);
    	  //If system.in is close it can not be reopen !!!
    	  //br.close();
          
          if (stop == true) {
        	  br.close();
        	  shutdown();
          }
      }catch (IOException e) {
    	  e.printStackTrace();
	  }
      
      
  }

}
