package uk.ac.kcl.cerch.bril.test;

import java.io.File;

import uk.ac.kcl.cerch.bril.characteriser.DEFLogFileCharacteriser;
import uk.ac.kcl.cerch.bril.characteriser.DEFLogFileCharacteriserImpl;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacterisation;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacteriserException;
import junit.framework.TestCase;

public class DEFLogFileCharacteriserTest extends TestCase{
public void testFileCharacteriser(){
	//File file = new File("C:\\brilstore\\00EXPT123\\3_chainsaw.def");
	File file = new File("C:\\brilstore\\00EXPT123\\phaser\\CCP4_DATABASE\\database.def");
	DEFLogFileCharacteriser characteriseFile= new DEFLogFileCharacteriserImpl();
	try {
		FileCharacterisation fc =	characteriseFile.characteriseFile(file);
		System.out.println(fc.getMetadata());
	} catch (FileCharacteriserException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
}
