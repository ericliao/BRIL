package uk.ac.kcl.cerch.bril.characteriser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.Vector;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import uk.ac.kcl.cerch.bril.ccp4.TaskObject;
import uk.ac.kcl.cerch.bril.ccp4.processor.log.SCMCootProcessor;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacterisation;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacteriserException;

public class COOTScmFileCharacteriserImpl implements COOTScmFileCharacteriser {

	@Override
	public FileCharacterisation characteriseFile(File file)
			throws FileCharacteriserException {
		FileCharacterisation fileCharacterisation = null;
		fileCharacterisation = new COOTScmFileCharacterisation();
		SCMCootProcessor cootProcessor = new SCMCootProcessor(file);
		TaskObject taskObject = cootProcessor.getTaskObject();
		try {
			String data = runXMLCreation(taskObject);
			fileCharacterisation.setMetadata(data);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileCharacterisation;

	}

	private String runXMLCreation(TaskObject taskObject) throws Exception {
		String task = taskObject.getTaskName();
		String jobId = taskObject.getJobID();
		String software = taskObject.getSoftwareName();
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
		writer.writeNamespace(task_dataPrefix, task_dataURI);
		writer.writeAttribute(TaskObjectElement.JOB_ID.localName(), jobId);

		writer.writeStartElement(task_dataURI,
				TaskObjectElement.TASK_NAME.localName());
		writer.writeCharacters(task);
		writer.writeEndElement();

		/*
		 * writer.writeStartElement(task_dataURI, TaskObjectElement.DATE
		 * .localName()); writer.writeCharacters(taskObject.getRunDateTime());
		 * writer.writeEndElement();
		 */

		writer.writeStartElement(task_dataURI,
				TaskObjectElement.SOFTWARE_NAME.localName());
		writer.writeCharacters(software);
		writer.writeEndElement();

		// add child <input_filenames> to task iterate input filename vector
		// here
		writer.writeStartElement(task_dataURI,
				TaskObjectElement.INPUT_FILENAME.localName() + "s");
		// System.out.println("size input: "+inputs.size());
		for (int i = 0; i < inputs.size(); i++) {
			// add element <input_filename> to <input_filenames>
			writer.writeStartElement(task_dataURI,
					TaskObjectElement.INPUT_FILENAME.localName());
			writer.writeCharacters(inputs.get(i));
			writer.writeEndElement();
		}
		// close element </input_filenames>
		writer.writeEndElement();

		// add child <output_filenames> to task iterate output filename vector
		// here
		writer.writeStartElement(task_dataURI,
				TaskObjectElement.OUTPUT_FILENAME.localName() + "s");
		for (int i = 0; i < outputs.size(); i++) {
			// add element <input_filename> to <input_filenames>
			writer.writeStartElement(task_dataURI,
					TaskObjectElement.OUTPUT_FILENAME.localName());
			writer.writeCharacters(outputs.get(i));
			writer.writeEndElement();
		}
		// close element </output_filenames>
		writer.writeEndElement();

		// close element </task>
		writer.writeEndElement();
		// flush and close
		writer.flush();
		writer.close();
		return out.toString();
	}

	public static void main(String arg[]) {
		File file = new File(
				"C:\\brilstore\\00EXPT123\\phaser\\0-coot.state.scm");

		COOTScmFileCharacterisation fileCharacterisation;
		try {
			fileCharacterisation = (COOTScmFileCharacterisation) new COOTScmFileCharacteriserImpl()
					.characteriseFile(file);
			System.out.println(fileCharacterisation.getMetadata());

		} catch (FileCharacteriserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}