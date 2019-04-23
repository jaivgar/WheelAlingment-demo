/*
 *  Copyright (c) 2018 Jens Eliasson, Luleå University of Technology
 *
 *  This work is part of the Productive 4.0 innovation project, which receives grants from the
 *  European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 *  (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 *  national funding authorities from involved countries.
 */

package eu.arrowhead.core.datamanager;

import eu.arrowhead.common.Utility;
import eu.arrowhead.common.exception.BadPayloadException;
import eu.arrowhead.common.messages.SenMLMessage;
//import eu.arrowhead.common.messages.SigMLMessage;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.Vector;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.log4j.Logger;

/*
 * Modifications Use Case Far Edge
 */
import java.io.File;
import java.io.InputStream;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import javax.ws.rs.core.Response.ResponseBuilder;
import eu.arrowhead.common.misc.TypeSafeProperties;

/**
 * This is the REST resource for the DataManager Core System.
 */
@Path("datamanager")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DataManagerResource {

  private static final Logger log = Logger.getLogger(DataManagerResource.class.getName());
  TypeSafeProperties props = Utility.getProp("app.properties");
  
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String getIt() {
    log.info("datamanager");
    return "This is the DataManager Arrowhead Core System.";
  }


  /* Historian Service */
  @POST
  @Path("historian")
  public Response storeData(@Valid SenMLMessage sml, @Context ContainerRequestContext requestContext) {
    int statusCode = 0;
    log.info("storage returned with status code: " + 0);
    return Response.status(Status.OK).build();
  }

  @GET
  @Path("historian")
  public String getInfo(@Context ContainerRequestContext requestContext) {
    return "Datamanager::Historian";
  }


  @GET
  @Path("historian/{consumerName}")
  @Produces("application/json")
  public Response getData(@PathParam("consumerName") String consumerName, @QueryParam("count") @DefaultValue("1") String count_s, @Context UriInfo uriInfo) {
    int statusCode = 0;
    int count = Integer.parseInt(count_s);
      
    System.out.println("getData requested with count: " + count);
    MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters(); 
    int i=0;
    String sig;
    Vector<String> signals = new Vector<String>();
    do {
      sig = queryParams.getFirst("sig"+i);
      if (sig != null) {
        System.out.println("sig["+i+"]: "+sig+"");
	signals.add(sig);
      }
      i++;
    } while (sig != null);
    if (signals.size() == 0)
      signals = null;
    


//    System.out.println("getData returned with count: " + );
    //return Response.status(Status.OK).build();
    /*SigMLMessage ret = null;
    if(signals == null)
      ret = DataManagerService.fetchEndpoint(consumerName, count);
    else
      ret = DataManagerService.fetchEndpoint(consumerName, count, signals);*/
    Vector<SenMLMessage> ret = null;
    if(signals == null)
      ret = DataManagerService.fetchEndpoint(consumerName, count);
    else
      ret = DataManagerService.fetchEndpoint(consumerName, count, signals);

    return Response.status(Status.OK).entity(ret).build();
  }

  @PUT
  @Path("historian/{consumerName}")
  @Consumes("application/senml+json")
  public Response PutData(@PathParam("consumerName") String consumerName, @Valid Vector<SenMLMessage> sml) {
    boolean statusCode = DataManagerService.createEndpoint(consumerName);
    System.out.println("Got SenML message");

    SenMLMessage head = sml.firstElement();
    if(head.getBt() == null)
      head.setBt((double)System.currentTimeMillis() / 1000.0);

    for(SenMLMessage s: sml) {
      System.out.println("object" + s.toString());
      if(s.getT() == null && s.getBt() != null)
	s.setT(0.0);
    } 
    statusCode = DataManagerService.updateEndpoint(consumerName, sml);
    System.out.println("putData returned with status code: " + statusCode);

    String jsonret = "{\"p\": "+ 0 +",\"x\": 0}";
    return Response.ok(jsonret, MediaType.APPLICATION_JSON).build();
  }

/*  @PUT
  @Path("historian/{consumerName}")
  @Consumes("application/sigml+json")
  public Response PutData(@PathParam("consumerName") String consumerName, @Valid SigMLMessage sml) {
    boolean statusCode = DataManagerService.createEndpoint(consumerName);

    System.out.println("PutData() from " + consumerName);

    if (sml.getBn() == null)
      sml.setBn(consumerName);
    if (sml.getBt() == null)
      sml.setBt((double)System.currentTimeMillis() / 1000.0);
    for (SenMLMessage m : sml.e) {
      if(m.getT() == null)
	m.setT(0.0);
    }

    statusCode = DataManagerService.updateEndpoint(consumerName, sml);
    System.out.println("putData returned with status code: " + statusCode + " from: "); // + sml.getBn() + " at: " + sml.getBt());

    String jsonret = "{\"p\": "+sml.getP()+",\"x\": 0}";
    return Response.ok(jsonret, MediaType.APPLICATION_JSON).build();
  }
*/
 
  /*@PUT
  @Path("historian/{consumerName}")
  @Consumes("application/senml+json")
  public Response PutData(@PathParam("consumerName") String consumerName, @Valid Vector<SenMLMessage> sml) {
    boolean statusCode = DataManagerService.createEndpoint(consumerName);
    statusCode = DataManagerService.updateEndpoint(consumerName, sml);
    System.out.println("putData returned with status code: " + statusCode + " from: "); // + sml.getBn() + " at: " + sml.getBt());

    //return Response.status(Status.OK).build();
    String jsonret = "{\"rc\": 0}";
    return Response.ok(jsonret, MediaType.APPLICATION_JSON).build();
  }*/


  /* Modifications Use Case Far Edge
   * 
   * Jaime Garcia, Luleå University of Technology
   */
  
  /*Using this resource, when sending the request from a client I get a error: 
   * Jackson library threw IOException during JSON serialization! Wrapping it in RuntimeException. 
   * Exception message: Unexpected character ('-' (code 45)) in numeric value: expected digit (0-9) 
   * to follow minus sign, for valid numeric value\n at [Source: (String)\"--------
   */
  /*
  @POST
  @Path("historian_file/{consumerName}")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response PutData(@PathParam("consumerName") String consumerName, @FormDataParam("file") InputStream uploadedFile,
			@FormDataParam("file") FormDataContentDisposition fileDetail){
	  
	  // Create entry in iot_device table
	  boolean statusCode = DataManagerService.createEndpoint(consumerName);
	  System.out.println("CreatedEndpoint returned with status code: " + statusCode);
	  
	  //Decided for this use case (USB directory)
	  String path = "/home/jaime/Documents/testFileTran/";
	  
	  // Create entry in iot_files table
	  statusCode = DataManagerService.updatePathFile(consumerName, path );
	  System.out.println("updatePathFile returned with status code: " + statusCode);
	  
	  // Save file in file system
	  if (DataManagerService.saveToDisk(uploadedFile, fileDetail, path)) {
		  System.out.println("File received");
		  return Response.status(Status.OK).build();
	  }
	  else {
		  System.out.println("Error in file recpetion");
		  return Response.serverError().entity("File couldn be uploaded").build();
	  }
	  
  }
  */
  
  @POST
  @Path("historian/file/{consumerName}")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.TEXT_PLAIN)
  public String PutData(@PathParam("consumerName") String consumerName, @FormDataParam("file") InputStream uploadedFile,
			@FormDataParam("file") FormDataContentDisposition fileDetail){
			
	  
	  // Create entry in iot_device table
	  boolean statusCode = DataManagerService.createEndpoint(consumerName);
	  System.out.println("CreatedEndpoint returned with status code: " + statusCode);
	  
	  String path_storage = props.getProperty("path_storage", "/home/jaime/Documents/testFileTran/");
	  
	  // Create entry in iot_files table
	  statusCode = DataManagerService.updatePathFile(consumerName, fileDetail.getFileName(), path_storage );
	  System.out.println("updatePathFile returned with status code: " + statusCode);
	  
	  // Save file in file system
	  if (DataManagerService.saveToDisk(uploadedFile, fileDetail, path_storage)) {
		  System.out.println("File received");
		  return "File saved in DataManager at " + path_storage;
		  //return Response.status(Status.OK).build();
	  }
	  else {
		  System.out.println("Error in file reception");
		  return "Error, file could not be saved in DataManager";
		  //return Response.serverError().entity("File couldn be uploaded").build();
	  }
  }
  
  @GET
  @Path("historian/file/{fileName}")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response getFile(@PathParam("fileName") String fileName) {
	  
	  String path = DataManagerService.lookFileName(fileName);
	  if (path == null) {
		  return Response.serverError().entity("File could not be found").build();
	  }
	  
	  File file = new File(path + fileName);
	  ResponseBuilder response = Response.ok((Object) file, MediaType.APPLICATION_OCTET_STREAM);
	  response.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
	  return response.build();
	  
	}
  
  /* END Modifications
   * 
   * 2019
   */
  
  
  /* Proxy Service */
  @GET
  @Path("proxy")
  public String getInfo() {
    return "DataManager::Proxy";
  }

  @POST
  @Path("proxy")
  public Response proxy(@Context ContainerRequestContext requestContext) {
    int statusCode = 0;
    log.info("Proxy returned with status code: " + 0);
    return Response.status(Status.OK).build();
  }


  @GET
  @Path("proxy/{consumerName}")
  public Response proxyGet(@PathParam("consumerName") String consumerName) {
    int statusCode = 0;
    ProxyElement pe = ProxyService.getEndpoint(consumerName);
    if (pe == null) {
      System.out.println("proxy GET to consumerName: " + consumerName + " not found");
      //System.out.println("proxyGet returned with NULL data");
      return Response.status(Status.NOT_FOUND).build();
    }

    return Response.status(Status.OK).entity(pe.msg).build();
  }


  /*@PUT
  @Path("proxy/{consumerName}")
  @Consumes("application/sigml+json")
  public Response proxyPut(@PathParam("consumerName") String consumerName, @Valid SigMLMessage sml) {
    ProxyElement pe = ProxyService.getEndpoint(consumerName);
    if (pe == null) {
      System.out.println("consumerName: " + consumerName + " not found, creating");
      pe = new ProxyElement(consumerName);
      ProxyService.addEndpoint(pe);
    }

    boolean statusCode = ProxyService.updateEndpoint(consumerName, sml);
    System.out.println("putData/SigML returned with status code: " + statusCode + " from: "); // + sigml.get(0).getBn() + " at: " + sml.get(0).getBt());

    String jsonret = "{\"rc\": 0}";
    return Response.ok(jsonret, MediaType.APPLICATION_JSON).build();
  }*/

  @PUT
  @Path("proxy/{consumerName}")
  @Consumes("application/senml+json")
  public Response proxyPut(@PathParam("consumerName") String consumerName, @Valid Vector<SenMLMessage> sml) {
    ProxyElement pe = ProxyService.getEndpoint(consumerName);
    if (pe == null) {
      System.out.println("consumerName: " + consumerName + " not found, creating");
      pe = new ProxyElement(consumerName);
      ProxyService.addEndpoint(pe);
    }

    //System.out.println("sml: "+ sml + "\t"+sml.toString());
    boolean statusCode = ProxyService.updateEndpoint(consumerName, sml);
    System.out.println("putData/SenML returned with status code: " + statusCode + " from: " + sml.get(0).getBn() + " at: " + sml.get(0).getBt());

    String jsonret = "{\"rc\": 0}";
    return Response.ok(jsonret, MediaType.APPLICATION_JSON).build();
  }

}
