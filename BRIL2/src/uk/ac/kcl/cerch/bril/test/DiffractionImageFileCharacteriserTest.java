package uk.ac.kcl.cerch.bril.test;

import java.io.File;


import uk.ac.kcl.cerch.bril.characteriser.DiffractionImageFileCharacterisation;
import uk.ac.kcl.cerch.bril.characteriser.DiffractionImageFileCharacteriser;
import uk.ac.kcl.cerch.bril.characteriser.DiffractionImageFileCharacteriserImpl;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacterisation;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacteriserException;
import junit.framework.TestCase;

public class DiffractionImageFileCharacteriserTest extends TestCase {
	
	public void testCharacteriseFile(){
		File file = new File("C:\\brilstore\\00EXPT123\\h2-2_MS_3_162.img");
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
