package uk.ac.kcl.cerch.bril.characteriser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import uk.ac.kcl.cerch.bril.ccp4.TaskObject;
import uk.ac.kcl.cerch.bril.ccp4.processor.com.ScriptProcessor;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacterisation;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacteriserException;

public class COMScriptFileCharacteriserImpl implements COMScriptFileCharacteriser{

	@Override
	public FileCharacterisation characteriseFile(File file)
			throws FileCharacteriserException {
		FileCharacterisation fileCharacterisation = null;
		fileCharacterisation = new COMScriptFileCharacterisation();
			
		ScriptProcessor processor =new ScriptProcessor(file);
		TaskObject taskObject= processor.readScriptFile();
		taskObject.setSoftwareName("CCP4");
		try {

			String data = runXMLCreation(taskObject);
			fileCharacterisation.setMetadata(data);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileCharacterisation;
	}
	private String runXMLCreation(TaskObject taskObject)
			throws Exception {
		OutputStream out = new ByteArrayOutputStream();
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = outputFactory.createXMLStreamWriter(out);
		String task_dataURI = "http://cerch.kcl.ac.uk/bril/schema/task";
		String task_dataPrefix = "tk";
		writer.setDefaultNamespace(task_dataURI);
		writer.writeStartDocument();

		// start element <task>
		writer.writeStartElement(task_dataURI, task_dataPrefix+ ":" + "task");
		writer.writeNamespace( task_dataPrefix, task_dataURI);
		writer.writeAttribute(TaskObjectElement.JOB_ID.localName(), taskObject.getJobID());
		
		writer.writeStartElement(task_dataURI,TaskObjectElement.TASK_NAME.localName());
		writer.writeCharacters(taskObject.getTaskName());
		writer.writeEndElement();
		
		writer.writeStartElement(task_dataURI,TaskObjectElement.DATE.localName());
		writer.writeCharacters(taskObject.getRunDateTime());
		writer.writeEndElement();
		
		writer.writeStartElement(task_dataURI,TaskObjectElement.SOFTWARE_NAME.localName());
		writer.writeCharacters(taskObject.getSoftwareName());
		writer.writeEndElement();
		
		 //add child <input_filenames> to task iterate input filename vector here
		writer.writeStartElement(task_dataURI,TaskObjectElement.INPUT_FILENAME.localName()+"s");
		for (int i= 0; i<taskObject.getInputFileNames().size();i++){
			String input= taskObject.getInputFileNames().get(i);
			writer.writeStartElement(task_dataURI,TaskObjectElement.INPUT_FILENAME.localName());
			writer.writeCharacters(input);
			writer.writeEndElement();
		} 
		//close element </input_filenames>
		writer.writeEndElement();
		
		//add child <output_filenames> to task iterate output filename vector here
		writer.writeStartElement(task_dataURI,TaskObjectElement.OUTPUT_FILENAME.localName()+"s");		
		for (int i= 0; i<taskObject.getOutputFileNames().size();i++){
			String output= taskObject.getOutputFileNames().get(i);
			writer.writeStartElement(task_dataURI,TaskObjectElement.OUTPUT_FILENAME.localName());
			writer.writeCharacters(output);
			writer.writeEndElement();			
		}
		 //close element </output_filenames>
		writer.writeEndElement();
		
		writer.writeStartElement(task_dataURI,TaskObjectElement.LOG_FILENAME.localName());
		writer.writeCharacters(taskObject.getLogFile());
		writer.writeEndElement();
		
		// close element </task>
		writer.writeEndElement();
		// flush and close
		writer.flush();
		writer.close();
		return out.toString();
	}
}
