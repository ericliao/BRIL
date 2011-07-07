package uk.ac.kcl.cerch.bril.test;

import java.io.File;

import uk.ac.kcl.cerch.bril.characteriser.MTZReflectionFileCharacteriser;
import uk.ac.kcl.cerch.bril.characteriser.MTZReflectionFileCharacteriserImpl;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacteriserException;
import junit.framework.TestCase;

public class MTZReflectionFileCharacteriserTest extends TestCase{

	public void testRunMTZdmpHeader(){
	//	File file = new File("C:\\brilstore\\00EXPT123\\truncate.mtz");
		File file = new File("/home/shrijar/brilstore/expa416b0b2-de69-471c-bd63-fcb64dc15a28/high.mtz");
		MTZReflectionFileCharacteriser t = new MTZReflectionFileCharacteriserImpl();
		try {
			t.characteriseFile(file);
		} catch (FileCharacteriserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
