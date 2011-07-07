package uk.ac.kcl.cerch.bril.test;

import java.io.File;

import junit.framework.TestCase;

import uk.ac.kcl.cerch.bril.characteriser.PhenixDEFFileCharacteriser;
import uk.ac.kcl.cerch.bril.characteriser.PhenixDEFFileCharacteriserImpl;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacterisation;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacteriserException;

public class PhenixDEFFileCharacteriserImplTest extends TestCase{
	
	public void testCharacteriseFile(){
		//File file = new File("C:\\brilstore\\00EXPT123\\1\\1ref.def");
		File file = new File("C:\\brilstore\\00EXPT123\\1\\1ref.def");
		PhenixDEFFileCharacteriser characteriseFile= new PhenixDEFFileCharacteriserImpl();
		try {
			FileCharacterisation fc =	characteriseFile.characteriseFile(file);
			System.out.println(fc.getMetadata());
		} catch (FileCharacteriserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
