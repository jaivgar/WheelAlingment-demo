/*
 *  Copyright (c) 2018 AITIA International Inc.
 *
 *  This work is part of the Productive 4.0 innovation project, which receives grants from the
 *  European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 *  (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 *  national funding authorities from involved countries.
 */

package eu.arrowhead.client.consumer;

import eu.arrowhead.client.common.CertificateBootstrapper;
import eu.arrowhead.client.common.Utility;
import eu.arrowhead.client.common.exception.ArrowheadException;
import eu.arrowhead.client.common.exception.UnavailableServerException;
import eu.arrowhead.client.common.misc.ClientType;
import eu.arrowhead.client.common.misc.TypeSafeProperties;
import eu.arrowhead.client.common.model.ArrowheadService;
import eu.arrowhead.client.common.model.ArrowheadSystem;
import eu.arrowhead.client.common.model.OrchestrationResponse;
import eu.arrowhead.client.common.model.ServiceRequestForm;
import java.awt.Font;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.SSLContext;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response.Status;

import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLContextConfigurator.GenericStoreException;

/* Modifications Use Case Far Edge
 * 
 * Jaime Garcia, Luleå University of Technology
 */
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.ClientBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Oper_station_truck {

  private static boolean isSecure;
  private static String orchestratorUrl;
  private static TypeSafeProperties props = Utility.getProp();
  private static final String consumerSystemName = props.getProperty("consumer_system_name");
  private static final String consumerSystemAddress = props.getProperty("consumer_system_address", "192.168.0.115");
  private static final String consumerSystemPort = props.getProperty("consumer_system_port", "8100");
  
  //New core system url
  //private static final String dataManagerUrl = props.getProperty("dataman_url","http://localhost:8456/datamanager/");

  private Oper_station_truck(String[] args) {
    //Prints the working directory for extra information. Working directory should always contain a config folder with the app.conf file!
    System.out.println("Working directory: " + System.getProperty("user.dir"));
    
    //Compile the URL for the orchestration request.
    getOrchestratorUrl(args);

    //Start a timer, to measure the speed of the Core Systems and the provider application system.
    long startTime = System.currentTimeMillis();

    //Compile the payload, that needs to be sent to the Orchestrator - THIS METHOD SHOULD BE MODIFIED ACCORDING TO YOUR NEEDS
    ServiceRequestForm srf = compileSRF();

    //Sending the orchestration request and parsing the response
    String dataManagerUrl = sendOrchestrationRequest(srf);
    
    //Connect to the provider, consuming its service - THIS METHOD SHOULD BE MODIFIED ACCORDING TO YOUR USE CASE
    //double temperature = consumeService(providerUrl);
    
    //---TODO: Change path when using different USB in app.conf
    String path_upload = props.getProperty("path_upload", "/media/pi/TOSHIBA/");
    String filename = props.getProperty("filename","test.xml");
    System.out.println("The path where the file comes from is: "+ path_upload + filename);
    
    String status = consumeService_upload(dataManagerUrl,path_upload + filename);
    
    System.out.println(status);
    
    InputStream getStream = consumeService_download(dataManagerUrl, filename);
    
    //---TODO: Change path when using different files in app.conf
    String path_download = props.getProperty("path_download", "/media/pi/ESD-USB/");
    System.out.println("The path where the file will be downloaded is: "+ path_download);
    
    boolean result = saveToDisk(getStream, path_download, filename);
    
    long endTime = System.currentTimeMillis();
    System.out.println("DataManager and File sent and saved response time: " + Long.toString(endTime - startTime));
    
  //Show a message dialog with the response from the service provider
    JLabel label;
    if (result) {
    	label = new JLabel("The file was dowloaded and saved correctly in: " + path_download );
    }
    else {
    	label = new JLabel("There was an error and the file could not be downloaded and saved");
    }
    label.setFont(new Font("Arial", Font.BOLD, 18));
    JOptionPane.showMessageDialog(null, label, "Provider Response", JOptionPane.INFORMATION_MESSAGE);
    
    /*
    //Show a message dialog with the response from the service provider
    JLabel label = new JLabel("The response to the file upload is " + status );
    label.setFont(new Font("Arial", Font.BOLD, 18));
    JOptionPane.showMessageDialog(null, label, "Provider Response", JOptionPane.INFORMATION_MESSAGE);
    */
  }

  public static void main(String[] args) {
    new Oper_station_truck(args);
  }

  //Compiles the payload for the orchestration request
  private ServiceRequestForm compileSRF() {
    /*
      ArrowheadSystem: systemName, (address, port, authenticationInfo)
      Since this Consumer skeleton will not receive HTTP requests (does not provide any services on its own),
      the address, port and authenticationInfo fields can be set to anything.
      SystemName can be an arbitrarily chosen name, which makes sense for the use case.
     */
	ArrowheadSystem consumer = new ArrowheadSystem(consumerSystemName, consumerSystemAddress, Integer.parseUnsignedInt(consumerSystemPort), "null");

    //You can put any additional metadata you look for in a Service here (key-value pairs)
    /* No medatada as we are calling a core system
     * 
    Map<String, String> metadata = new HashMap<>();
    metadata.put("Product", "Production_order");
    if (isSecure) {
    	//This is a mandatory metadata when using TLS, do not delete it
    	metadata.put("security", "token");
    }
    */
    
    /*
      ArrowheadService: serviceDefinition (name), interfaces, metadata
      Interfaces: supported message formats (e.g. JSON, XML, JSON-SenML), a potential provider has to have at least 1 match,
      so the communication between consumer and provider can be facilitated.
     */
    ArrowheadService service = new ArrowheadService("InsecureHistorian", Collections.singleton("JSON"), null);

    //Some of the orchestrationFlags the consumer can use, to influence the orchestration process
    Map<String, Boolean> orchestrationFlags = new HashMap<>();
    //When true, the orchestration store will not be queried for "hard coded" consumer-provider connections
    orchestrationFlags.put("overrideStore", true);
    //When true, the Service Registry will ping every potential provider, to see if they are alive/available on the network
    orchestrationFlags.put("pingProviders", false);
    //When true, the Service Registry will only providers with the same exact metadata map as the consumer
    orchestrationFlags.put("metadataSearch", false);
    //When true, the Orchestrator can turn to the Gatekeeper to initiate interCloud orchestration, if the Local Cloud had no adequate provider
    orchestrationFlags.put("enableInterCloud", false);
  //When true, the Orchestrator skips intra cloud orchestration and turns to the Gatekeeper to initiate interCloud orchestration.
    orchestrationFlags.put("triggerInterCloud", false);
    
    //Add preferred local clouds in case there is more than one neighbour cloud with a data manager
    
    //Build the complete service request form from the pieces, and return it
    ServiceRequestForm srf = new ServiceRequestForm.Builder(consumer).requestedService(service).orchestrationFlags(orchestrationFlags).build();
    System.out.println("Service Request payload: " + Utility.toPrettyJson(null, srf));
    return srf;
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
	  
	/*
	 * TODO: Create my own client as in Utility in client-commons
	 * //Response getResponse = Utility.sendRequest(providerUrl, "GET", null);
	 */
	  
	  Client client_own = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
	  String url_upload_file = providerUrl+"/file/"+consumerSystemName;
	  Builder request = client_own.target(UriBuilder.fromUri(url_upload_file).build()).request().header("Content-type", MediaType.MULTIPART_FORM_DATA);
	  
	  //Received error org.glassfish.jersey.message.internal.MessageBodyProviderNotFoundException ...
	  
	  Response response;
	  try {
		  response = request.post(Entity.entity(multipart, multipart.getMediaType()));
	  }catch(ProcessingException e){
		  throw new UnavailableServerException("Could not get any response from: " + providerUrl, Status.SERVICE_UNAVAILABLE.getStatusCode(), e);
	  }
	  return response.readEntity(String.class);
  }

  private InputStream consumeService_download(String providerUrl, String filename) {
    /*
      Sending request to the provider, to the acquired URL. The method type and payload should be known beforehand.
      If needed, compile the request payload here, before sending the request.
      Supported method types at the moment: GET, POST, PUT, DELETE
     */
	  
	  Response getResponse = Utility.sendRequest(providerUrl+"/file/"+filename, "GET", null);
	  
	  InputStream incomingFile = getResponse.readEntity(InputStream.class);
	  
	  return incomingFile;
    /*
      Parsing the response from the provider here. This code prints an error message, if the answer is not in the expected JSON format, but custom
      error handling can also be implemented here. For example the Orchestrator will send back a JSON with the structure of the eu.arrowhead.client
      .common.exception.ErrorMessage class, and the errors from the Orchestrator are parsed this way.
     */
	  /*
    TemperatureReadout readout = new TemperatureReadout();
    try {
      readout = getResponse.readEntity(TemperatureReadout.class);
      System.out.println("Provider Response payload: " + Utility.toPrettyJson(null, readout));
    } catch (RuntimeException e) {
      e.printStackTrace();
      System.out.println("Provider did not send the temperature readout in SenML format.");
    }
    if (readout.getE().get(0) == null) {
      System.out.println("Provider did not send any MeasurementEntry.");
      return -1;
    } else {
      System.out.println("The indoor temperature is " + readout.getE().get(0).getV() + " degrees celsius.");
      return readout.getE().get(0).getV();
    }
    */
  }

  static boolean saveToDisk(InputStream uploadedFile, String path, String filename) {
	  
	try {
		File target = new File(path+filename);
		OutputStream out = new FileOutputStream(target);
		
		int read = 0;
        byte[] bytes = new byte[1024];
        
        while ((read = uploadedFile.read(bytes)) != -1) {
            out.write(bytes, 0, read);
        }
        
        uploadedFile.close();
        
        out.flush();
        out.close();
        
        return true;
		
	}catch (FileNotFoundException exception){
        exception.printStackTrace();
        
    }catch (IOException ioException){
        ioException.printStackTrace();
        
    }catch(Exception e) {
        e.printStackTrace();
        
    }
  	
  	return false;
  }
  
  
  
   /*
      Methods that should be modified to your use case ↑
   ----------------------------------------------------------------------------------------------------------------------------------
      Methods that do not need to be modified ↓
   */

  //DO NOT MODIFY - Gets the correct URL where the orchestration requests needs to be sent (from app.conf config file + command line argument)
  private void getOrchestratorUrl(String[] args) {
    String orchAddress = props.getProperty("orch_address", "0.0.0.0");
    int orchInsecurePort = props.getIntProperty("orch_insecure_port", 8440);
    int orchSecurePort = props.getIntProperty("orch_secure_port", 8441);

    for (String arg : args) {
      if (arg.equals("-tls")) {
        isSecure = true;
        SSLContextConfigurator sslCon = new SSLContextConfigurator();
        sslCon.setKeyStoreFile(props.getProperty("keystore"));
        sslCon.setKeyStorePass(props.getProperty("keystorepass"));
        sslCon.setKeyPass(props.getProperty("keypass"));
        sslCon.setTrustStoreFile(props.getProperty("truststore"));
        sslCon.setTrustStorePass(props.getProperty("truststorepass"));

        try {
          SSLContext sslContext = sslCon.createSSLContext(true);
          Utility.setSSLContext(sslContext);
        } catch (GenericStoreException e) {
          System.out.println("Provided SSLContext is not valid, moving to certificate bootstrapping.");
          e.printStackTrace();
          sslCon = CertificateBootstrapper.bootstrap(ClientType.CONSUMER, consumerSystemName);
          props = Utility.getProp();
          Utility.setSSLContext(sslCon.createSSLContext(true));
        }
        break;
      }
    }

    if (isSecure) {
      Utility.checkProperties(props.stringPropertyNames(), ClientType.CONSUMER.getSecureMandatoryFields());
      orchestratorUrl = Utility.getUri(orchAddress, orchSecurePort, "orchestrator/orchestration", true, false);
    } else {
      orchestratorUrl = Utility.getUri(orchAddress, orchInsecurePort, "orchestrator/orchestration", false, false);
    }
  }

  /* NO NEED TO MODIFY (for basic functionality)
     Sends the orchestration request to the Orchestrator, and compiles the URL for the first provider received from the OrchestrationResponse */
  private String sendOrchestrationRequest(ServiceRequestForm srf) {
    //Sending a POST request to the orchestrator (URL, method, payload)
    Response postResponse = Utility.sendRequest(orchestratorUrl, "POST", srf);
    //Parsing the orchestrator response
    OrchestrationResponse orchResponse = postResponse.readEntity(OrchestrationResponse.class);
    System.out.println("Orchestration Response payload: " + Utility.toPrettyJson(null, orchResponse));
    if (orchResponse.getResponse().isEmpty()) {
      throw new ArrowheadException("Orchestrator returned with 0 Orchestration Forms!");
    }

    //Getting the first provider from the response
    ArrowheadSystem provider = orchResponse.getResponse().get(0).getProvider();
    String serviceURI = orchResponse.getResponse().get(0).getServiceURI();
    //Compiling the URL for the provider
    UriBuilder ub = UriBuilder.fromPath("").host(provider.getAddress()).scheme("http");
    if (serviceURI != null) {
      ub.path(serviceURI);
    }
    if (provider.getPort() != null && provider.getPort() > 0) {
      ub.port(provider.getPort());
    }
    if (orchResponse.getResponse().get(0).getService().getServiceMetadata().containsKey("security")) {
      ub.scheme("https");
      ub.queryParam("token", orchResponse.getResponse().get(0).getAuthorizationToken());
      ub.queryParam("signature", orchResponse.getResponse().get(0).getSignature());
    }
    System.out.println("Received provider system URL: " + ub.toString());
    return ub.toString();
  }

}
