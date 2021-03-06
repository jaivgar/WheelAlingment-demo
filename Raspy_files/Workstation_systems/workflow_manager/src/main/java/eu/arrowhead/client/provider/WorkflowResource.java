package eu.arrowhead.client.provider;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import eu.arrowhead.client.common.Utility;
import eu.arrowhead.client.common.misc.TypeSafeProperties;
import eu.arrowhead.client.common.model.Car;
import eu.arrowhead.client.common.model.StateMachine;



@Path("/") //base path after the port
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class WorkflowResource {
	
	static final String SERVICE_URI = "workflow";
	protected TypeSafeProperties props = Utility.getProp();
	
	@GET
	@Path(SERVICE_URI)
	@Produces(MediaType.TEXT_PLAIN)
	public String getIt() {
		return "Workflow Manager got it!";
	}
	
	@GET
	@Path(SERVICE_URI + "/StateMachine")
	public Response getStateMachine() {
		
		if (WorkflowManager.productArrived) {
			// Before state machine is sent we will try just by transferring the 
			// name of the file necessary to execute the operation and the name
			// of the machine( service machine) that has to execute it
			String filename = props.getProperty("filename","test.xml");
			
			// This data would come from the production order but now is hard coded
			String machine = "Calibration";
			StateMachine test_machine = new StateMachine(filename, machine);
			return Response.status(Status.OK).entity(test_machine).build();
		}
		else {
			System.out.println("The product did not arrived yet to the workstation so there is not State Machine");
			return Response.status(Status.NO_CONTENT).build();
		}
		
	}
	
	//For future demos as we do not have the equipment to POST
	@POST
	@Path(SERVICE_URI + "/Product")
	public Response postProductId() {
		return Response.status(Status.OK).build();
	}
	
	@PUT
	@Path(SERVICE_URI + "/OperationResult")
	public Response storeOperationResult(StateMachine result_machine) {
		
		// Problem get datamanager url
		//String status = consumeService_upload(WorkflowManager.dataManagerUrl,path_upload + filename);
		
	    System.out.println(" The result from " + result_machine.getmachine() + " is :" + result_machine.getfilename());
		return Response.status(Status.OK).build();
	}
	
	
}
