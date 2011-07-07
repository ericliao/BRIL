package uk.ac.kcl.cerch.bril.test;

import java.io.File;

import uk.ac.kcl.cerch.bril.characteriser.COMScriptFileCharacteriser;
import uk.ac.kcl.cerch.bril.characteriser.COMScriptFileCharacteriserImpl;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacterisation;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacteriserException;

import junit.framework.TestCase;

public class COMScriptFileCharacteriserTest extends TestCase{
	
	public void testCharacteriseFile(){
		File file = new File("C:\\brilstore\\00EXPT123\\free.com");
		COMScriptFileCharacteriser characteriseFile= new COMScriptFileCharacteriserImpl();
		try {
			FileCharacterisation fc =	characteriseFile.characteriseFile(file);
			System.out.println(fc.getMetadata());
		} catch (FileCharacteriserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
