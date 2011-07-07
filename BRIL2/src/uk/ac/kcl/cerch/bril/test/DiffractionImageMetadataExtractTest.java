package uk.ac.kcl.cerch.bril.test;

import java.io.File;
import java.util.Map;

import uk.ac.kcl.cerch.bril.ccp4.processor.DiffractionImageProcessor;
import uk.ac.kcl.cerch.bril.characteriser.DiffractionImageFileCharacterisation;
import uk.ac.kcl.cerch.bril.characteriser.DiffractionImageFileCharacteriser;
import uk.ac.kcl.cerch.bril.characteriser.DiffractionImageFileCharacteriserImpl;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacterisation;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacteriserException;

public class DiffractionImageMetadataExtractTest {
public static void main(String arg[]){
	File file = new File("/home/shrijar/brilstore/expa416b0b2-de69-471c-bd63-fcb64dc15a28/h2-2_MS_3_001.img");
	
	DiffractionImageProcessor processor = new DiffractionImageProcessor();
	System.out.println(file.getPath());
	processor.runDiffDump(file.getPath());
	Map<String, String> metadata = processor.getDiffractionMetadataAsMap();
	System.out.println(metadata);
	
	DiffractionImageFileCharacteriser characteriseFile = new DiffractionImageFileCharacteriserImpl();
	try {
		FileCharacterisation fc =	characteriseFile.characteriseFile(file);
		System.out.println (fc.getMetadata());
		System.out.println ("Diff Set ID:  "+ ((DiffractionImageFileCharacterisation)fc).getDiffImageSetIdentifier());
		System.out.println ("Diff ID: "+ ((DiffractionImageFileCharacterisation)fc).getDiffImageIdentifier());
	
	} catch (FileCharacteriserException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
}
