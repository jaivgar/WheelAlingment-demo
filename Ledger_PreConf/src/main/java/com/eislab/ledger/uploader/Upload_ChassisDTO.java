package com.eislab.ledger.uploader;

import java.io.BufferedWriter;
import java.io.FileWriter;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import it.eng.productunithubledgerclient.base.BlockchainFactory;
import it.eng.productunithubledgerclient.base.LedgerClient;
import it.eng.productunithubledgerclient.exception.ProductUnitHubException;
import it.eng.productunithubledgerclient.model.ChassisDTO;
import it.eng.productunithubledgerclient.model.EquipmentRequirement;
import it.eng.productunithubledgerclient.model.EquipmentSpecification;
import it.eng.productunithubledgerclient.model.InstructionText;
import it.eng.productunithubledgerclient.model.Operation;
import it.eng.productunithubledgerclient.model.OperationStep;
import it.eng.productunithubledgerclient.model.ProcessStep;
import it.eng.productunithubledgerclient.model.WorkcellResource;

public class Upload_ChassisDTO {

	static LedgerClient ledgerClient;
	
	public static void main(String[] args) {
		try {
			  BlockchainFactory factory = new BlockchainFactory();
		      ledgerClient = factory.getType();
		  }catch (ProductUnitHubException e) {
			  e.printStackTrace();
	      }
		  
		  Collection<ChassisDTO> chassisDTOList = new ArrayList<>();
	      ChassisDTO chassisDTO = buildChassisDTO();

	      chassisDTOList.add(chassisDTO);
		  /*
		  try {
			  ledgerClient.storeProcessStepRouting(chassisDTOList);
		  }catch (ProductUnitHubException e) {
			  e.printStackTrace();
	      }
	      */
		  System.out.println("--- Information uploaded to Ledger ---");
	}
	
	private static ChassisDTO buildChassisDTO() {
		
		ChassisDTO chassisDTO = new ChassisDTO();
		chassisDTO.setChassisId("Test");
		chassisDTO.setComponent("Integration");
		chassisDTO.setSubComponent("Use Case");

		ProcessStep processStep = new ProcessStep();
		//processStep.setId("001_8415E5D-47A2-4E79-BF1C-1F56B105AC6-" + getNextInt());
		processStep.setId("001_8415E5D-47A2-4E79-BF1C-1F56B105AC6-Iterator");
		processStep.setName("WheelAlignment");
		processStep.setSequenceNo(0);

		WorkcellResource workcellResource = new WorkcellResource();
		workcellResource.setId("074544");
		processStep.setWorkcellResource(workcellResource);

		Operation operation = new Operation();
		operation.setId("015111336");
		
		List <InstructionText> texts = new ArrayList <InstructionText> ();
		InstructionText text = new InstructionText();
		text.setSequenceNo(0);
		
		// Inside the Instructions Text will be included the information to retrieve the XML file to perform the operation
		String path = "/home/jaime/Documents/testFileTran/Start/Request_A847050_20190410_133149545.xml";
		String name = "Request_A847050_20190410_133149545.xml";
		operation.setDescription(name);
		String filecontent = null;
		
		/*Improved way to copy file to String with java.nio
		 * No need to check end of line characters
		 */
		
		try {
		filecontent = new String(Files.readAllBytes(Paths.get(path)));
		}catch (IOException e) {
			  e.printStackTrace();
		}
		
		/*	Check the line separator string of the xml file
		 * (According to original document it is CRLF line terminator \r\n DOS type)
		 * 
		try (InputStream in = new FileInputStream(path);
			BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {     
			StringBuilder sb = new StringBuilder();
			while ((filecontent = r.readLine()) != null) {
				//sb.append(str + System.lineSeparator());
				//r.read()
				sb.append(filecontent + "\r\n");
			}
			filecontent = sb.toString();		
			//System.out.println("data from InputStream as String : " + str);			
		} catch (IOException e) {
			  e.printStackTrace();
		}
		*/
		
		/* Code to retrieve file from String
		 * Will be included in ERP system
		 *
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("Tested XML.xml"));
			writer.write(filecontent);
			writer.close();
			
		}catch (IOException e) {
			  e.printStackTrace();
		}
		*/
		
		text.setText(filecontent);
		texts.add(text);
		operation.setInstructionTexts(texts);
		
		//Continue building the test ChassisDTO for the ledger
		
		List <EquipmentSpecification> equipmentSpecifications = new ArrayList <EquipmentSpecification> ();
		EquipmentSpecification equipmentSpecification = new EquipmentSpecification();
		equipmentSpecification.setSequenceNo(0);
		equipmentSpecification.setQuantity(3);
		equipmentSpecifications.add(equipmentSpecification);
		
		List <EquipmentRequirement> equipmentRequirements = new ArrayList <EquipmentRequirement> ();
		EquipmentRequirement equipmentRequirement = new EquipmentRequirement();
		equipmentRequirement.setSequenceNo(0);
		equipmentRequirement.setSpecifications(equipmentSpecifications);
		equipmentRequirements.add(equipmentRequirement);
		
		operation.setEquipmentRequirements(equipmentRequirements);
		
		List <OperationStep> operationSteps = new ArrayList<OperationStep>();
		OperationStep operationStep = new OperationStep() ;
		operationStep.setDescription("OperationStep: Wheel Alingment test");
		operationStep.setSequenceNo(0);
		
		operationSteps.add(operationStep);
		operation.setOperationSteps(operationSteps);
		
		
		List <Operation> billofOperations = new ArrayList<Operation>();
		billofOperations.add(operation);
		processStep.setBillOfOperation(billofOperations);
		
		ArrayList<ProcessStep> processStepCollection = new ArrayList<>();
		processStepCollection.add(processStep);
		chassisDTO.getBillOfProcessSteps().add(processStep);

		return chassisDTO;
  }
}
