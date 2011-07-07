package uk.ac.kcl.cerch.bril.fileformat.identifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import uk.ac.kcl.cerch.bril.fileformat.CrystallographyFileFormat;
import uk.ac.kcl.cerch.bril.fileformat.CrystallographyObjectType;
import uk.ac.kcl.cerch.bril.fileformat.FileSuffixFileFormat;
import uk.ac.kcl.cerch.soapi.fileformat.FileFormat;
import uk.ac.kcl.cerch.soapi.fileformat.identifier.FileFormatIdentifierException;

public class CrystallographyFileFormatIdentifierImpl implements CrystallographyFileFormatIdentifier{
	private Properties properties;
	private static Logger log = Logger.getLogger(CrystallographyFileFormatIdentifierImpl.class);
	@Override
	public FileFormat identifyFileFormat(File file)
			throws FileFormatIdentifierException {
		log.info("Crystallography FileFormatIdentifier");
		FileFormat cffi = new CrystallographyFileFormat();
		
		FileSuffixFileFormatIdentifier fsffi = new FileSuffixFileFormatIdentifierImpl();
		FileFormat fsff = fsffi.identifyFileFormat(file);
		
		MIMETypeFileFormatIdentifier mtffi = new MIMETypeFileFormatIdentifierImpl();
		FileFormat mtff = mtffi.identifyFileFormat(file);
		
		String extension = ((FileSuffixFileFormat) fsff).getExtension();
		String mime = mtff.getFormat();
		if(mime.contains("charset=us-ascii")){
			mime = mime.substring(0,mime.indexOf("charset=us-ascii"));
		}
		log.info("mime1: "+mime +" and fileextension: " +extension+"  ");
		((CrystallographyFileFormat) cffi).setMimeType(mime);
		((CrystallographyFileFormat) cffi).setFileSuffix(extension);
		String sf= ((CrystallographyFileFormat) cffi).getFileSuffix();
		String mt= ((CrystallographyFileFormat) cffi).getMimeType();
		log.info("mime2: "+mt +" and fileextension: " +sf+"  ");	
		if(isCompressedDiffractionImage(extension, file.getPath())==true){
			System.out.println("Checked is compressed diffraction image");	
			log.info("Checked is compressed diffraction image  ");
			((CrystallographyFileFormat) cffi).setFormat(CrystallographyObjectType.CompressedDiffractionImage.getType());
			((CrystallographyFileFormat) cffi).setDescription(CrystallographyObjectType.CompressedDiffractionImage.getDescription());
	
		} else
		
		if(isDiffracionImage(extension)==true){
			System.out.println("diffraction image");	
			log.info("Diffraction image  ");
			((CrystallographyFileFormat) cffi).setFormat(CrystallographyObjectType.DiffractionImage.getType());
			((CrystallographyFileFormat) cffi).setDescription(CrystallographyObjectType.DiffractionImage.getDescription());
	
		} else			
		
		if(extension.equals("def")){
			
			String format =checkSuffixDEF(file);
			System.out.println("def format: "+format);
			((CrystallographyFileFormat) cffi).setFormat(format);
			if(format.equals(CrystallographyObjectType.CCP4IDefFile.getType())){
				log.info("CCP4I def file type:  ");
				((CrystallographyFileFormat) cffi).setDescription(CrystallographyObjectType.CCP4IDefFile.getDescription());
			}else{
				log.info("Phenix def file type:  ");
				((CrystallographyFileFormat) cffi).setDescription(CrystallographyObjectType.PhenixDefFile.getDescription());					
			}
	
		}else
	
		//if(extension.equals("mtz") && mime.equals("application/octet-stream")){
		if(extension.equals("mtz")){
			log.info("mtz reflection file type:  ");
			((CrystallographyFileFormat) cffi).setFormat(CrystallographyObjectType.MTZReflectionFile.getType());
			((CrystallographyFileFormat) cffi).setDescription(CrystallographyObjectType.MTZReflectionFile.getDescription());
	
		}else
		if(extension.equals("pdb")){
			log.info("PDB coordinate file type:  ");
			((CrystallographyFileFormat) cffi).setFormat(CrystallographyObjectType.CoordinateFile.getType());
			((CrystallographyFileFormat) cffi).setDescription(CrystallographyObjectType.CoordinateFile.getDescription());
	
		}else
		
		if(extension.equals("com")){
			System.out.println("COM file");	
			log.info("COM file type:  ");
			((CrystallographyFileFormat) cffi).setFormat(CrystallographyObjectType.COMFile.getType());
			((CrystallographyFileFormat) cffi).setDescription(CrystallographyObjectType.COMFile.getDescription());
	
		}else
		if(extension.equals("lp")){
			System.out.println("mosflm lp file");	
			log.info("Mosflm lp file type:  ");
			((CrystallographyFileFormat) cffi).setFormat(CrystallographyObjectType.MosflmLpFile.getType());
			((CrystallographyFileFormat) cffi).setDescription(CrystallographyObjectType.MosflmLpFile.getDescription());
	
		}else
		if(extension.equals("sav")){
			System.out.println("mosflm sav file");	
			log.info("Mosflm sav file type:  ");
			((CrystallographyFileFormat) cffi).setFormat(CrystallographyObjectType.MosflmSavFile.getType());
			((CrystallographyFileFormat) cffi).setDescription(CrystallographyObjectType.MosflmSavFile.getDescription());
	
		}else
		if(extension.equals("gen")){
			System.out.println("GEN file");	
			log.info("Log file type:  ");
			((CrystallographyFileFormat) cffi).setFormat(CrystallographyObjectType.MosflmGenFile.getType());
			((CrystallographyFileFormat) cffi).setDescription(CrystallographyObjectType.MosflmGenFile.getDescription());
	
		}else
		
		if(extension.equals("aln")){
			System.out.println("alignment ALN file");	
			log.info("alignment ALN file type:  ");
			((CrystallographyFileFormat) cffi).setFormat(CrystallographyObjectType.AlignmentFile.getType());
			((CrystallographyFileFormat) cffi).setDescription(CrystallographyObjectType.AlignmentFile.getDescription());
		}else
		if(extension.equals("txt")){
			System.out.println("TXT file");	
			log.info("TXT file type:  ");
			((CrystallographyFileFormat) cffi).setFormat(CrystallographyObjectType.LOGFile.getType());
			((CrystallographyFileFormat) cffi).setDescription(CrystallographyObjectType.LOGFile.getDescription());
	
		}else
			if(extension.equals("log")){
			System.out.println("TXT file");	
			log.info("Log file type:  ");
			((CrystallographyFileFormat) cffi).setFormat(CrystallographyObjectType.LOGFile.getType());
			((CrystallographyFileFormat) cffi).setDescription(CrystallographyObjectType.LOGFile.getDescription());
	
		}else
		if(extension.equals("spt")){
			System.out.println("TXT spt file");	
			log.info("Log file type:  ");
			((CrystallographyFileFormat) cffi).setFormat(CrystallographyObjectType.LOGFile.getType());
			((CrystallographyFileFormat) cffi).setDescription(CrystallographyObjectType.LOGFile.getDescription());
	
		}else
			if(extension.equals("scm")){
				System.out.println("TXT COOT scm file");	
				log.info("Log COOT scm file type:  ");
				((CrystallographyFileFormat) cffi).setFormat(CrystallographyObjectType.CootStateExeFile.getType());
				((CrystallographyFileFormat) cffi).setDescription(CrystallographyObjectType.CootStateExeFile.getDescription());		
			
		} else if (extension.equals("seq")){
			System.out.println("SEQ file");	
			log.info("SEQ file type:  ");
			((CrystallographyFileFormat) cffi).setFormat(CrystallographyObjectType.SEQFile.getType());
			((CrystallographyFileFormat) cffi).setDescription(CrystallographyObjectType.SEQFile.getDescription());
			
		} else if (extension.equals("doc") || extension.equals("docx") || extension.equals("rtf")){
			System.out.println("DOC/DOCX/RTF file");	
			log.info("DOC/DOCX/RTF file type:  ");
			((CrystallographyFileFormat) cffi).setFormat(CrystallographyObjectType.DOCFile.getType());
			((CrystallographyFileFormat) cffi).setDescription(CrystallographyObjectType.DOCFile.getDescription());	
		}
		
		else{
			//if none of the type is identified then make it a misc file type
			log.info("Log file type: " + extension);
			((CrystallographyFileFormat) cffi).setFormat(CrystallographyObjectType.MiscFile.getType());
			((CrystallographyFileFormat) cffi).setDescription(CrystallographyObjectType.MiscFile.getDescription());
		
		}
		return cffi;
	}
	
	private boolean isCompressedDiffractionImage(String extension, String filePath){
		boolean isCompressedDiffImg = false;
		String compressedName = filePath.substring(filePath.lastIndexOf("\\")+1);
		String name =compressedName.substring(0,compressedName.lastIndexOf('.'));
		String nameExtension = name.substring(name.lastIndexOf('.')+1);
	//	System.out.println(name);
	//	System.out.println(nameExtension);
		if(isDiffracionImage(nameExtension)==true){
			isCompressedDiffImg=true;
		}
		return isCompressedDiffImg;
	}
	
	private boolean isDiffracionImage(String extension){
		try {
			properties = new Properties();
			properties.load(getClass().getClassLoader().getResourceAsStream(
					"diffractionImageExtension.properties"));
		} catch (IOException ex) {
			String error = String.format(
					"Failed to load diffractionImageExtension.properties file.: %s", ex
							.getMessage());
			log.error(error);
		
		}
		int count = 0;
		boolean isDiffImg = false;
		 for (String key : properties.stringPropertyNames()) {
		//	if (key.equals(extension) || key == extension) {
				String type = properties.getProperty(key);
				if(type.equals(extension)){
					isDiffImg=true;
				}
				count++;
			}
		 
		 return isDiffImg;
	}
	

	private String checkSuffixDEF(File file) {
		String type = "";
		boolean containsCCP4IString= isCCP4I_DefFile(file);

		if (file.getName().equals("database") || containsCCP4IString == true) {
			type = CrystallographyObjectType.CCP4IDefFile.getType();
		} else {
			// if(key.equals(ext+".phenix") || key == ext+".phenix"){
			type = CrystallographyObjectType.PhenixDefFile.getType();
		}

		return type;
	}
	
	public boolean isCCP4I_DefFile(File DEF_File){
		boolean isCCP4_DEF=false;
		try {
			String aLine;
			//File file = new File(DEF_FileLocation);
			Scanner scanner = new Scanner(DEF_File);
			scanner.useDelimiter(System.getProperty("line.separator"));

			while (scanner.hasNext()) {
				aLine = scanner.next();
				
				if (hasToken(aLine, "#CCP4I") == true) {
					isCCP4_DEF=true;
				}
			}
		scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return isCCP4_DEF;
	
 }
	
	private boolean hasToken(String line, String token){
		boolean lineHasToken=false;
		 StringTokenizer st = new StringTokenizer(line);
		     while (st.hasMoreTokens()) {
		    	 //System.out.println();
		    	 String tok = st.nextToken();
		    	if(tok.equals(token)){
		    		lineHasToken=true;
		    		//System.out.println("title line:" +line); 
		    	  }
		     }
		return lineHasToken;
	}
	
/*	private void uncompressBZ2(File file) throws Exception {
		String path = file.getPath();
		String compressedName = path.substring(path.lastIndexOf("\\")+1);
		String name =compressedName.substring(0,compressedName.lastIndexOf('.'));
		System.out.println(path);
		System.out.println(compressedName);
		System.out.println(name);
		
				 int sChunk = 8192;

			FileInputStream fileinput = new FileInputStream(file) ;
			//goto http://www.kohsuke.org/bzip2/
			//read two bytes 'B' and 'Z' from the fileinput before using CBZip2InputStream. 
			org.apache.tools.bzip2.CBZip2InputStream bzipin = new org.apache.tools.bzip2.CBZip2InputStream(fileinput);
			
			byte[] buffer = new byte[sChunk];
		    FileOutputStream out = new FileOutputStream(name);
		    int length;
		  
				while ((length = bzipin.read(buffer, 0, sChunk)) != -1)
				  out.write(buffer, 0, length);
			
		    out.close();
		    bzipin.close();
			
	
		
	
		
	}
	private void unZipBz2(File file){
		  try {
			  System.setProperty("java.library.path", "C:\\BRIL\\Libraries\\chilkatJava");
			  System.load("C:\\BRIL\\Libraries\\chilkatJava\\chilkat.dll");

		      //  System.loadLibrary("chilkat");
		    } catch (UnsatisfiedLinkError e) {
		      System.err.println("Native code library failed to load.\n" + e);
		      System.exit(1);
		    }
		  CkBz2 bz2 = new CkBz2();
String path = file.getPath();
String compressedName = path.substring(path.lastIndexOf("\\")+1);
String name =compressedName.substring(0,compressedName.lastIndexOf('.'));
System.out.println(path);
System.out.println(compressedName);
System.out.println(name);
		    boolean success;

		    //  Any string unlocks the component for the 1st 30-days.
		    success = bz2.UnlockComponent("Anything for 30-day trial.");
		    if (success != true) {
		        System.out.println(bz2.lastErrorText());
		        return;
		    }
		    success = bz2.UncompressFile(file.getPath(),name);
		    if (success != true) {
		        System.out.println(bz2.lastErrorText());
		        return;
		    }



	}*/
	

	

}
