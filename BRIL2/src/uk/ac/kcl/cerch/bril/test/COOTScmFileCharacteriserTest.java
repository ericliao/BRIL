package uk.ac.kcl.cerch.bril.test;

import java.io.File;

import junit.framework.TestCase;

import uk.ac.kcl.cerch.bril.characteriser.COOTScmFileCharacterisation;
import uk.ac.kcl.cerch.bril.characteriser.COOTScmFileCharacteriserImpl;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacteriserException;


public class COOTScmFileCharacteriserTest extends TestCase{

	public void test(){
		File file = new File("C:\\brilstore\\00EXPT123\\phaser\\0-coot.state.scm");
		COOTScmFileCharacterisation fileCharacterisation;
		try {
			fileCharacterisation = (COOTScmFileCharacterisation) new COOTScmFileCharacteriserImpl().characteriseFile(file);
			System.out.println(fileCharacterisation.getMetadata());
		} catch (FileCharacteriserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
