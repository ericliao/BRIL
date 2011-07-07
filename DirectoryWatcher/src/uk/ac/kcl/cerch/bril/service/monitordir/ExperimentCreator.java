package uk.ac.kcl.cerch.bril.service.monitordir;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.Vector;

import javax.jms.JMSException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.configuration.ConfigurationException;

import uk.ac.kcl.cerch.bril.common.util.DateTime;
import uk.ac.kcl.cerch.bril.service.queue.ADQueueWriter;
import uk.ac.kcl.cerch.bril.service.queue.ADQueueWriterImpl;
import uk.ac.kcl.cerch.bril.service.uuid.IDGenerator;

public class ExperimentCreator {

		private String experimentMetadataXML;
		private String experimentId;
		private String projectName;
		private String experimentType;
		private String directoryPath;
		private String dateTime;
		private String status;
		private boolean activeExpt = false;
		private ExperimentConfig ec;

	
		public ExperimentCreator(){
			try {
				ec = new ExperimentConfig();
			} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public boolean hasAnyExperiment(){
			return ec.hasSavedExperiments();
		}
		
		public boolean hasOneExperiment(){
			return ec.hasOneExperiment();
		}
		
		public boolean hasExperimentCollection(){
			return ec.hasCollectionOfExperiments();
		}
		
		/**
		 * Experiment Id whose status is active
		 * 
		 * @return
		 */
		public String getLastActiveExperiment(){
			return ec.getActiveExperimentId();
			
		}
		
		/**
		 * Change the given experiment status to inactive
		 * @param experimentId
		 */
		public void setExperimentStatusToInactive(String experimentId){
			try {
				ec.setExperimentStatus(experimentId, "inactive");
			} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/**
		 * Change the given experiment status to active
		 * @param experimentId
		 */
		public void setExperimentStatusToActive(String experimentId){
			try {
				ec.setExperimentStatus(experimentId, "active");
			} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		public Vector<String> getAllExperiment(){
			Vector<String> experimentIds = new Vector<String>();
			return experimentIds;
		}
		
		
		
		/**
		 * Creates experiment XML to send in the message and saves the experiment to
		 * the config file and send this experiment xml to the queue
		 * 
		 * @see createExperimentXML()
		 * @see saveExperiment()
		 * @see registerExperimentToQueue()
		 * 
		 * @param projectName
		 * @param type
		 * @param dirPath
		 * @throws XMLStreamException
		 * @throws FactoryConfigurationError
		 */
		public void createNewExperiment(String projectName, String experimentType,
				String dirPath) throws XMLStreamException,
				FactoryConfigurationError {
			// replace with id generator
			this.experimentId = "exp-" + IDGenerator.generateUUID();
			this.projectName = projectName;
			this.experimentType = experimentType;
			this.directoryPath = dirPath;
			this.dateTime = DateTime.getCurrentSysDateTime();
		

			saveNewExperiment();
			createExperimentXML();
			// set other experiments to inactive
			try {
				registerExperimentToQueue();
			} catch (JMSException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		public String getExperimentXML() {
			return experimentMetadataXML;
		}

		public String getExperimentId() {
			return experimentId;
		}

		public String getProjectName() {
			return projectName;
		}

		public String getExperimentType() {
			return experimentType;
		}

		public String getDirectoryPath() {
			return directoryPath;
		}

		public String getDateTime() {
			return dateTime;
		}

		public String getStatus() {
			return status;
		}
		

		public void saveNewExperiment() {
			try {
				ExperimentConfig ec = new ExperimentConfig();

				// other experiments present
				if (ec.hasCollectionOfExperiments() == true) {
					ec.addExperiment(
							"experiments.message_parameter(-1).experimentId",
							experimentId);
					ec.addExperiment("experiments.message_parameter.projectName",
							projectName);
					ec.addExperiment(
							"experiments.message_parameter.experimentType",
							experimentType);
					ec.addExperiment("experiments.message_parameter.directoryPath",
							directoryPath);
					ec.addExperiment("experiments.message_parameter.dataTime",
							dateTime);
					ec.addExperiment("experiments.message_parameter.status",
							"active");
					System.out
							.println("New experiment is saved to config file status=active: "
									+ "other experiment present made status=inactive");

				} else if(ec.hasOneExperiment()==true){
					ec.addExperiment(
							"experiments.message_parameter(-1).experimentId",
							experimentId);
					ec.addExperiment("experiments.message_parameter.projectName",
							projectName);
					ec.addExperiment(
							"experiments.message_parameter.experimentType",
							experimentType);
					ec.addExperiment("experiments.message_parameter.directoryPath",
							directoryPath);
					ec.addExperiment("experiments.message_parameter.dataTime",
							dateTime);
					ec.addExperiment("experiments.message_parameter.status",
							"active");
				}else {
				
					// new experiment
					ec.addExperiment("experiments.message_parameter.experimentId",
							experimentId);
					ec.addExperiment("experiments.message_parameter.projectName",
							projectName);
					ec.addExperiment(
							"experiments.message_parameter.experimentType",
							experimentType);
					ec.addExperiment("experiments.message_parameter.directoryPath",
							directoryPath);
					ec.addExperiment("experiments.message_parameter.dataTime",
							dateTime);
					ec.addExperiment("experiments.message_parameter.status",
							"active");
					System.out
							.println("New experiment is saved to config file status=active");

				}
			} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	 public	void setStatusToInactive() {
			ExperimentConfig ec;
			try {
				
				ec = new ExperimentConfig();
				String thisid= ec.getExperimentId();
				ec.setExperimentStatus(thisid,"inactive");
			} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		public String getActiveExperiment() {
			ExperimentConfig ec=null;
			try {
				ec = new ExperimentConfig();
			} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return ec.getActiveExperimentId();
		}
		public void loadActiveExperiment(String experimentId){
			try {
				ExperimentConfig ec = new ExperimentConfig();
				this.experimentId = ec.getExperimentParameter("experimentId");
				if(ec.hasCollectionOfExperiments()==true){
					int countId=0;
					for(Iterator iter = ec.experimentIdCollection.iterator(); iter.hasNext();){
						String id = (String) iter.next();
						if (id.equals(experimentId)){
							this.experimentId = experimentId;
							this.projectName=ec.getElementValue( "projectName", countId);					
							this.experimentType=ec.getElementValue( "experimentType", countId);
							this.directoryPath=ec.getElementValue( "directoryPath", countId);
							this.dateTime=ec.getElementValue( "dateTime", countId);
							
						}
					
					}
				}
			} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public boolean hasAnActiveExperiment() {
			boolean activeExpt = false;
		//	ExperimentConfig ec;
			//	ec = new ExperimentConfig();
				// returns false if only one experiment is present
				if (ec.hasCollectionOfExperiments() == false) {
					if (ec.hasOneExperiment() == true) {
						activeExpt = true;
					}
				}

			return activeExpt;
		}

		public void loadActiveExperiment() {
			try {
				ExperimentConfig ec = new ExperimentConfig();
				
				if (ec.hasCollectionOfExperiments() == false) {
					if (ec.hasOneExperiment() == true) {
						this.experimentId = ec
								.getExperimentParameter("experimentId");
						this.projectName = ec.getExperimentParameter("projectName");
						this.experimentType = ec
								.getExperimentParameter("experimentType");
						this.directoryPath = ec
								.getExperimentParameter("directoryPath");
						this.dateTime = ec.getExperimentParameter("dateTime");
						// this value shows that this experiment was the last active
						// one
						this.status = ec.getExperimentParameter("status");

						System.out.println("One active experiment present");
					}
				} else {
					// activeExpt=true;
					// loads an experiment with status= active get you the last
					// active one
					System.out.println("active exp Id= "+ ec.getActiveExperimentId());
				}
				System.out
						.println("Loading the existing last active experiment from config file");

			} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void createExperimentXML() throws XMLStreamException,
				FactoryConfigurationError {
			// experimentMetadataXML = null;
			StringWriter res = new StringWriter();
			XMLStreamWriter writer = XMLOutputFactory.newInstance()
					.createXMLStreamWriter(res);
			writer.writeStartDocument();

			writer.writeStartElement("message_parameter");

			writer.writeStartElement("experimentId");
			writer.writeCharacters(String.valueOf(experimentId));
			writer.writeEndElement();

			writer.writeStartElement("dateTime");
			writer.writeCharacters(String.valueOf(dateTime));
			writer.writeEndElement();

			writer.writeStartElement("experimentType");
			writer.writeCharacters(String.valueOf(experimentType));
			writer.writeEndElement();

			writer.writeStartElement("projectName");
			writer.writeCharacters(String.valueOf(projectName));
			writer.writeEndElement();

			writer.writeStartElement("directoryPath");
			writer.writeCharacters(String.valueOf(directoryPath));
			writer.writeEndElement();

			writer.writeEndElement();
			writer.writeEndDocument();

			experimentMetadataXML = res.toString();

			System.out
					.println("Created XML string to be sent a new experiment message to the queue...");
			System.out.println(experimentMetadataXML);

			// return experimentMetadataXML;
		}

		public void registerExperimentToQueue() throws JMSException,
				XMLStreamException, FactoryConfigurationError {
			ADQueueWriter adQueueWriter = new ADQueueWriterImpl();
			adQueueWriter.sendNewExperimentMessage(experimentMetadataXML);

		}
	}
