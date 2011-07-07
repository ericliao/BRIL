package uk.ac.kcl.cerch.bril.ccp4.processor.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Vector;

import uk.ac.kcl.cerch.bril.ccp4.TaskObject;

/**
 * @author Shri
 * @author Eric
 * 
 */
public class SCMCootProcessor {
	private File scmFile;
	private Vector<String> inputs = null;
	private Vector<String> outputs = null;
	private TaskObject taskObject = new TaskObject();

	public SCMCootProcessor(File scmFileLocation) {
		inputs = new Vector<String>();
		outputs = new Vector<String>();
		this.scmFile = scmFileLocation;
		readSCMFile();

	}

	private void readSCMFile() {

		try {
			Scanner scanner = new Scanner(scmFile);
			scanner.useDelimiter(System.getProperty("line.separator"));

			while (scanner.hasNext()) {
				parseLineHeader(scanner.next());
			}
			scanner.close();			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// set the input to the task vector
		if (inputs != null) {
			Vector<String> in = new Vector<String>(new LinkedHashSet<String>(inputs));
			System.out.println("inputs: (" + in.toString() + ")");
			for (int i = 0; i < in.size(); i++) {
				taskObject.setInputFileName(in.get(i).toString());
			}
		}

		// set the outputs to the task vector
		if (outputs != null) {
			Vector<String> out = new Vector<String>(new LinkedHashSet<String>(outputs));
			System.out.println("outputs: (" + out.toString() + ")");
			for (int i = 0; i < out.size(); i++) {
				taskObject.setOutputFileName(out.get(i).toString());
			}
		}				
	}

	private void parseLineHeader(String line) {		
		String aLine;
		Scanner lineScanner = new Scanner(line);
		lineScanner.useDelimiter("\n");
		while (lineScanner.hasNext()) {
			aLine = lineScanner.next();

			// Assumption: each new '0-coot-history.scm' file ingested = a new coot process
			//			   there is one '0-coot-history.scm' file per refinement directory
			
			// parse 0-coot-history.scm   for  (save-coordinates            <- output pdb
			// parse        ""            for  (handle-read-draw-molecule	<- input pdb
			// parse 		""			  for  (make-and-draw-map         	<- input mtz
			
			boolean hasOutputPDB = aLine.contains("save-coordinates");
			boolean hasInputPDB = aLine.contains("handle-read-draw-molecule");
			boolean hasInputMTZ = aLine.contains("make-and-draw-map");			
			
			// If the SCM file has a 'handle-read-draw-molecule' in the line
			if (hasInputPDB == true) {							
				if (aLine.indexOf('"') != -1) {
					String s = aLine.substring(aLine.indexOf('"') + 1).trim();
					if (s != null) {						
						int pos1 = s.indexOf('"');
						if (pos1 != -1) {
							s = s.substring(0, pos1);
							String filename = new File(s).getName();
							inputs.add(filename);
						}												
					}

				}
			}
			
			// If the SCM file has a 'make-and-draw-map' in the line
			if (hasInputMTZ == true) {				
				if (aLine.indexOf('"') != -1) {
					String s = aLine.substring(aLine.indexOf('"') + 1).trim();
					if (s != null) {
						int pos1 = s.indexOf('"');
						if (pos1 != -1) {
							s = s.substring(0, pos1);
							String filename = new File(s).getName();
							inputs.add(filename);
						}					
					}
				}
			}
			
			// If the SCM file has a 'save-coordinates' in the line
			if (hasOutputPDB == true) {				
				if (aLine.indexOf('"') != -1) {
					String s = aLine.substring(aLine.indexOf('"') + 1).trim();
					if (s != null) {						
						int pos1 = s.indexOf('"');
						if (pos1 != -1) {
							s = s.substring(0, pos1);							
							String filename = new File(s).getName();
							outputs.add(filename);
						}
					}
				}
			}
		}

		// set the prefix used in all output files produced by this DEF file as
		// jobid

		taskObject.setJobID(scmFile.getName());
		taskObject.setTaskName("calculate");
		taskObject.setSoftwareName("coot");

	}
	
	public TaskObject getTaskObject() {
		return taskObject;
	}

	public static void main(String arg[]) {
		File file = new File(
				"C:\\brilstore\\00EXPT123\\phaser\\0-coot.state.scm");
		SCMCootProcessor cootProcessor = new SCMCootProcessor(file);

		Vector<String> newInputs = cootProcessor.getTaskObject()
				.getInputFileNames();
		System.out.println(newInputs);
	}
}
