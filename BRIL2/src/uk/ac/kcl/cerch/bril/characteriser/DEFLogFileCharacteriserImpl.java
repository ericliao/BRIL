package uk.ac.kcl.cerch.bril.characteriser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.Map;
import java.util.Vector;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import uk.ac.kcl.cerch.bril.ccp4.TaskObject;
import uk.ac.kcl.cerch.bril.ccp4.TaskObjectVector;
import uk.ac.kcl.cerch.bril.ccp4.processor.log.DEFDatabaseProcessor;
import uk.ac.kcl.cerch.bril.ccp4.processor.log.DEFTaskProcessor;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacterisation;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacteriserException;

public class DEFLogFileCharacteriserImpl implements DEFLogFileCharacteriser {

	@Override
	public FileCharacterisation characteriseFile(File file)
			throws FileCharacteriserException {
		FileCharacterisation fileCharacterisation = null;

		fileCharacterisation = new DEFLogFileCharacterisation();
		String filename = file.getName();
		if(filename.equals("database.def")){
			
			DEFDatabaseProcessor dbProcessor = new DEFDatabaseProcessor(file);
			TaskObjectVector toVector = dbProcessor.processFileData();
			try {

				String data = runXMLCreation(toVector);
				fileCharacterisation.setMetadata(data);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}else{
		
			DEFTaskProcessor taskProcessor = new DEFTaskProcessor(file);
			TaskObject taskObject = taskProcessor.readTaskDEFFile();
			try {

				String data = runXMLCreation(taskObject);
				fileCharacterisation.setMetadata(data);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		// DEFTaskProcessor to = dbProcessor.processFileData();
		
		return fileCharacterisation;
	}

	private String runXMLCreation(TaskObjectVector taskObject) throws Exception {
		OutputStream out = new ByteArrayOutputStream();	// close element </script_data>
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = outputFactory.createXMLStreamWriter(out);
		String task_dataURI = "http://cerch.kcl.ac.uk/bril/schema/task";
		String task_dataPrefix = "tk";
		writer.setDefaultNamespace(task_dataURI);
		writer.writeStartDocument();

		// start element <tasks>
		writer.writeStartElement(task_dataURI, task_dataPrefix + ":" + "tasks");
		writer.writeNamespace( task_dataPrefix, task_dataURI);
		Map<Integer,String> jobids = taskObject.getJobIDVectorObject();
		
		for (Map.Entry<Integer,String> entry: jobids.entrySet()){
			String jobId = entry.getValue();
			String task = taskObject.getTaskVectorObject().get(jobId);
			Long date = taskObject.getDateVectorObject().get(jobId);
			String status = taskObject.getStatusVectorObject().get(jobId);
			String title = taskObject.getTitleVectorObject().get(jobId);
			String logfilename = taskObject.getLogfileVectorObject().get(jobId);
			Vector<String> inputs = taskObject.getInputVectorObject().get(jobId);
			Vector<String> outputs = taskObject.getOutputVectorObject().get(jobId);
			// start element <task>
			writer.writeStartElement(task_dataURI, "task");	
			writer.writeAttribute(TaskObjectElement.JOB_ID.localName(), jobId);
		    //add child task_name to task
			 writer.writeStartElement(task_dataURI,TaskObjectElement.TASK_NAME.localName());
		     writer.writeCharacters(task);
		     writer.writeEndElement();       
		     //add child title to task
			 writer.writeStartElement(task_dataURI,TaskObjectElement.TITLE.localName());
		     writer.writeCharacters(title);
		     writer.writeEndElement();  	     
		     //add child status to task
			 writer.writeStartElement(task_dataURI,TaskObjectElement.STATUS.localName());
		     writer.writeCharacters(status);
		     writer.writeEndElement();  
		     //add child date to task
			 writer.writeStartElement(task_dataURI,TaskObjectElement.DATE.localName());
		     writer.writeCharacters(date.toString());
		     writer.writeEndElement(); 
		     //add child status to task
			 writer.writeStartElement(task_dataURI,TaskObjectElement.LOG_FILENAME.localName());
		     writer.writeCharacters(logfilename);
		     writer.writeEndElement();  
		     
		     //add child <input_filenames> to task iterate input filename vector here
			 writer.writeStartElement(task_dataURI,TaskObjectElement.INPUT_FILENAME.localName()+"s");
			 for(int i=0; i<inputs.size();i++){
					//add element <input_filename> to  <input_filenames> 
				 String inputFilename =inputs.get(i);
				 if(!inputFilename.equals("")){
					 writer.writeStartElement(task_dataURI,TaskObjectElement.INPUT_FILENAME.localName());
				     writer.writeCharacters(inputs.get(i));
				     writer.writeEndElement(); 
				     }
				 }
			 //close element </input_filenames>
		     writer.writeEndElement();  
		     
		     //add child <output_filenames> to task iterate output filename vector here
			 writer.writeStartElement(task_dataURI,TaskObjectElement.OUTPUT_FILENAME.localName()+"s");
			 for(int i=0; i<outputs.size();i++){
				 String ouputFilename =outputs.get(i);
				 if(!ouputFilename.equals("")){
					//add element <input_filename> to  <input_filenames> 
					 writer.writeStartElement(task_dataURI,TaskObjectElement.OUTPUT_FILENAME.localName());
				     writer.writeCharacters(outputs.get(i));
				     writer.writeEndElement(); 
				     }
				 }
			 //close element </output_filenames>
		     writer.writeEndElement();  
		    //close element </task>     
			writer.writeEndElement();	
		}
		
		// close element </tasks>
		writer.writeEndElement();
		// flush and close
		writer.flush();
		writer.close();
		return out.toString();
	}
	private String runXMLCreation(TaskObject taskObject) throws Exception {
		String task = taskObject.getTaskName();
		//format 17 Jun 2008  09:29:03
		String taskLog = taskObject.getLogFile();
		String jobId= taskObject.getJobID();
		Vector<String> inputs = taskObject.getInputFileNames();
		Vector<String> outputs = taskObject.getOutputFileNames();
		
		
		OutputStream out = new ByteArrayOutputStream();	
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = outputFactory.createXMLStreamWriter(out);
		String task_dataURI = "http://cerch.kcl.ac.uk/bril/schema/task";
		String task_dataPrefix = "tk";
		writer.setDefaultNamespace(task_dataURI);
		writer.writeStartDocument();
		
		// start element <task>
		writer.writeStartElement(task_dataURI, task_dataPrefix + ":" + "task");
		writer.writeNamespace( task_dataPrefix, task_dataURI);
		writer.writeAttribute(TaskObjectElement.JOB_ID.localName(), jobId);
		//add child task_name to task
		 writer.writeStartElement(task_dataURI,TaskObjectElement.TASK_NAME.localName());
	     writer.writeCharacters(task);
	     writer.writeEndElement();   
	     
	     //add child <input_filenames> to task iterate input filename vector here
		 writer.writeStartElement(task_dataURI,TaskObjectElement.INPUT_FILENAME.localName()+"s");
		 for(int i=0; i<inputs.size();i++){
				//add element <input_filename> to  <input_filenames> 
				 writer.writeStartElement(task_dataURI,TaskObjectElement.INPUT_FILENAME.localName());
			     writer.writeCharacters(inputs.get(i));
			     writer.writeEndElement(); 
			 }
		 //close element </input_filenames>
	     writer.writeEndElement();  
	     
	   //add child <output_filenames> to task iterate output filename vector here
		 writer.writeStartElement(task_dataURI,TaskObjectElement.OUTPUT_FILENAME.localName()+"s");
		 for(int i=0; i<outputs.size();i++){
				//add element <input_filename> to  <input_filenames> 
				 writer.writeStartElement(task_dataURI,TaskObjectElement.OUTPUT_FILENAME.localName());
			     writer.writeCharacters(outputs.get(i));
			     writer.writeEndElement(); 
			 }
		 //close element </output_filenames>
	     writer.writeEndElement();  
	 
	     writer.writeStartElement(task_dataURI,TaskObjectElement.LOG_FILENAME.localName());
	     writer.writeCharacters(taskLog);
	     writer.writeEndElement();
	     
		
		// close element </task>
		writer.writeEndElement();
		// flush and close
		writer.flush();
		writer.close();
		return out.toString();
	}
}
