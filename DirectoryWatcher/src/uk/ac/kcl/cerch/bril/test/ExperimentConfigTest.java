package uk.ac.kcl.cerch.bril.test;

import org.apache.commons.configuration.ConfigurationException;

import uk.ac.kcl.cerch.bril.service.monitordir.ExperimentConfig;
import junit.framework.TestCase;

public class ExperimentConfigTest extends TestCase {
	
	public void test(){
		ExperimentConfig ec;
		try {
			ec = new ExperimentConfig();
		  
		//trying to start the monitor with the exising experiment info
		//collection of experiment present
		//if(ec.hasCollectionOfExperiments()==true){
		//	ec.getNumberOfExperiment();
			//System.out.println("Active Expt id: "+ec.getActiveExperimentId());
		//	if(ec.getActiveExperimentId()==null){
				
				//use the xml to create a selection list in the userinterface
				//selected experiment id is returned 				
				//the config file will be updated with active as:
				//setExperimentStatus(expid,"active");
				//directory monitor is started with all these detail
				//
		//	}
		//	ec.setExperimentStatus("idchangeme1","active");
		//	System.out.println(ec.getAllStatus().size());
		//	System.out.println("Expt id: "+ec.getExperimentId());	
		//	System.out.println("Expt status: " +ec.getStatus());
		//}else{
			//one experiment present
		//	System.out.println("Expt id: "+ec.getExperimentId());	
		//	System.out.println("Expt status: " +ec.getStatus());
		//}
		//create a new experiment if already the node message_parameter is present
		/*ec.addExperiment("experiments.message_parameter(-1).experimentId", "bril:testid");
		ec.addExperiment("experiments.message_parameter.projectName", "baa");
		ec.addExperiment("experiments.message_parameter.experimentType", "cryst");
		ec.addExperiment("experiments.message_parameter.directoryPath", "/user/stella/baa5d5");
		ec.addExperiment("experiments.message_parameter.status", "active");
	*/
		// create new experiment the node message_parameter is absent 
		System.out.println("Has one or more: "+	ec.hasSavedExperiments()); //true one or more
		System.out.println("Has Collection: "+ ec.hasCollectionOfExperiments()); //false if only one/ true if more then one
		System.out.println("Has one expt: "+ ec.hasOneExperiment()); //one is present it gives true / false if more thne one is present
		ec.setExperimentStatus("expe10d07ab-263a-4964-ba51-90ec1bca72d0", "active");
		System.out.println(ec.getActiveExperimentId());
		System.out.println(ec.getExperimentParameter("experimentId"));
		System.out.println(ec.getExperimentParameter("projectName"));
		System.out.println(ec.getExperimentParameter("experimentType"));
		System.out.println(ec.getExperimentParameter("directoryPath"));
		System.out.println(ec.getExperimentParameter("dateTime"));
		//this value shows that this experiment was the last active one
		//System.out.println(ec.getExperimentParameter("status"));
		
	//	System.out.println(ec.getActiveExperimentId());
	//	ec.setExperimentStatus("expaba412d7-6e25-4ffe-9744-6a1b7e40131e", "inactive");
		//ec.addExperiment("experiments.message_parameter.experimentId", "bril:testid");
		//ec.addExperiment("experiments.message_parameter.projectName", "baa");
		//ec.addExperiment("experiments.message_parameter.experimentType", "cryst");
		//ec.addExperiment("experiments.message_parameter.directoryPath", "/user/stella/baa5d5");
		//ec.addExperiment("experiments.message_parameter.status", "active");
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
