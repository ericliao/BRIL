package uk.ac.kcl.cerch.bril.test;

import java.io.File;

import uk.ac.kcl.cerch.bril.fileformat.CrystallographyFileFormat;
import uk.ac.kcl.cerch.bril.fileformat.identifier.CrystallographyFileFormatIdentifier;
import uk.ac.kcl.cerch.bril.fileformat.identifier.CrystallographyFileFormatIdentifierImpl;
import uk.ac.kcl.cerch.soapi.fileformat.FileFormat;
import uk.ac.kcl.cerch.soapi.fileformat.identifier.FileFormatIdentifierException;
import junit.framework.TestCase;

public class CrystallographyFileFormatIdentifierTest extends TestCase {
	
	public void testIdentifyFileFormat(){
		//File file = new File("C:/brilstore/00EXPT123/h2-2_MS_1_003.img.bz2");
		//File file = new File("C:/brilstore/00EXPT123/truncate.mtz");
		//File file = new File("C:/brilstore/00EXPT123/database.def");
		//File file = new File("C:/brilstore/00EXPT123/1YY8b.pdb");
		File file = new File("C:/brilstore/expe10d07ab-263a-4964-ba51-90ec1bca72d0/5d5.mtz");
		
		CrystallographyFileFormat fileFormat;

	
		
		
		FileFormat cff = new CrystallographyFileFormat();
		
		CrystallographyFileFormatIdentifier cffi = new CrystallographyFileFormatIdentifierImpl();
		try {
			cff= cffi.identifyFileFormat(file);
			fileFormat = (CrystallographyFileFormat)cffi.identifyFileFormat(file);
			System.out.println(((CrystallographyFileFormat)cff).getFormat());
			System.out.println(((CrystallographyFileFormat)cff).getDescription());
			System.out.println(((CrystallographyFileFormat)cff).getFileSuffix());
			System.out.println(((CrystallographyFileFormat)cff).getMimeType());
			
			System.out.println("----------------------------------------------------");
			
			System.out.println(fileFormat.getFormat());
			System.out.println(fileFormat.getDescription());
			System.out.println(fileFormat.getFileSuffix());
			System.out.println(fileFormat.getMimeType());
			
		} catch (FileFormatIdentifierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
