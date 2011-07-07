package uk.ac.kcl.cerch.bril.test;

import java.io.File;

import uk.ac.kcl.cerch.bril.characteriser.MTZReflectionFileCharacteriser;
import uk.ac.kcl.cerch.bril.characteriser.MTZReflectionFileCharacteriserImpl;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacterisation;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacteriserException;

public class MTZCharacteriseFileMain {
public static void main(String arg[]){
	//File file = new File("/home/shrijar/brilstore/expa416b0b2-de69-471c-bd63-fcb64dc15a28/high.mtz");
	File file = new File("/home/shrijar/brilstore/expa416b0b2-de69-471c-bd63-fcb64dc15a28/truncate.mtz");
	MTZReflectionFileCharacteriser t = new MTZReflectionFileCharacteriserImpl();
	try {
		FileCharacterisation v =t.characteriseFile(file);
		System.out.println(v.getMetadata());
	} catch (FileCharacteriserException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
}
