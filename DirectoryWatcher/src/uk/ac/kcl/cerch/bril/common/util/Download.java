package uk.ac.kcl.cerch.bril.common.util;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class Download {
public Download(){
	
}
	private String localFileName;
	private String exactFilePath;
	private String directory;
	private String latestDownloadFileName;
	private String fileLocation;
	private String fileNameExt;
	private String fileNameNoExt;	

	
	public void download(String address, String localFileName) {
		
		OutputStream out = null;
		URLConnection conn = null;
		InputStream  in = null;		
		
		try {
			URL url = new URL(address);
			out = new BufferedOutputStream(new FileOutputStream(localFileName));
			conn = url.openConnection();
			in = conn.getInputStream();
			byte[] buffer = new byte[1024];
			int numRead;
			long numWritten = 0;
			while ((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
				numWritten += numRead;
			}
			System.out.println(localFileName + "\t" + numWritten);
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException ioe) {
			}
		}
	}
	
/*	public static void main(String arg[]) throws FileFormatIdentifierException{
		Download d = new Download();
		//d.download("http://localhost:8161/fileserver/h2-2_MS_1_004.img.bz2", "C:\\BRIL\\h2-2_MS_1_004.img.bz2");
	    File file = new File("C:\\BRIL\\h2-2_MS_1_004.img.bz2");
	//    BrilFileFormatValidatorImpl ofv = new BrilFileFormatValidatorImpl();			
	//	FormatValidation fv = new FormatValidation();
		fv = ofv.validateBrilFileFormat(file);
		System.out.println("Valid object?: "+ fv.getResult());
		System.out.println("Info: "+ fv.getInfo());
	}*/

}
