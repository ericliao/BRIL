package uk.ac.kcl.cerch.bril.test;

import java.io.File;
import java.util.Vector;

import uk.ac.kcl.cerch.bril.ccp4.processor.log.DEFPhenixProcessor;
import uk.ac.kcl.cerch.bril.characteriser.PhenixDEFFileCharacterisation;
import uk.ac.kcl.cerch.bril.characteriser.PhenixDEFFileCharacteriserImpl;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacteriserException;

public class DEFPhenixProcessorTest {
	public static void main(String arg[]){
		//File file = new File("C:\\brilstore\\00EXPT123\\1\\1ref_002.def");
		//File file = new File("C:\\brilstore\\00EXPT123\\1\\1ref_002.def");
		//File file = new File("/home/shrijar/brilstore/expa416b0b2-de69-471c-bd63-fcb64dc15a28/1ref.def");
		File file = new File("/home/shrijar/brilstore/expa416b0b2-de69-471c-bd63-fcb64dc15a28/2ref_002.def");
		
		DEFPhenixProcessor d = new DEFPhenixProcessor(file);
		//System.out.println(d.getInputs());
	
		Vector<String> newInputs = d.getTaskObject().getInputFileNames();
		Vector<String> newOutputs = d.getTaskObject().getOutputFileNames();
		
		System.out.println(newInputs);
		System.out.println("id: "+d.getTaskObject().getJobID());
		System.out.println("taskname: "+ d.getTaskObject().getTaskName());
		System.out.println("software: "+ d.getTaskObject().getSoftwareName());
		System.out.println("PhenixDEFFileCharacterisation ----- ");
		try {
			PhenixDEFFileCharacterisation fileCharacterisation = (PhenixDEFFileCharacterisation) new PhenixDEFFileCharacteriserImpl()
					.characteriseFile(file);
			System.out.println(fileCharacterisation.getMetadata());
		} catch (FileCharacteriserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
