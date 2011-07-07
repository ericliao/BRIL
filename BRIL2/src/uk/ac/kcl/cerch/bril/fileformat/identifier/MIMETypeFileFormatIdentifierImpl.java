package uk.ac.kcl.cerch.bril.fileformat.identifier;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;

import uk.ac.kcl.cerch.bril.fileformat.MIMETypeFileFormat;
import uk.ac.kcl.cerch.soapi.fileformat.FileFormat;
import uk.ac.kcl.cerch.soapi.fileformat.identifier.FileCommandMIMETypeFileFormatIdentifier;
import uk.ac.kcl.cerch.soapi.fileformat.identifier.FileFormatIdentifierException;

public class MIMETypeFileFormatIdentifierImpl implements MIMETypeFileFormatIdentifier{
	//private Properties properties;
	private static Logger log = Logger.getLogger(CrystallographyFileFormatIdentifierImpl.class);

	/* This method imports implementation of SOAPI class
	 * FileCommandMIMETypeFileFormatIdentifier which uses file.exe (windows)
	 * (non-Javadoc)
	 * @see uk.ac.kcl.cerch.soapi.fileformat.identifier.FileFormatIdentifier#identifyFileFormat(java.io.File)
	 */

	@Override
	public FileFormat identifyFileFormat(File file)
			throws FileFormatIdentifierException {
		// TODO Auto-generated method stub
		FileFormat fileFormat = new MIMETypeFileFormat();
		FileCommandMIMETypeFileFormatIdentifier fFormat;
		log.info("MIMETypeFileFormatIdentifier");
		try {
			fFormat = new FileCommandMIMETypeFileFormatIdentifier();
			String soapiResult = fFormat.identifyFileFormat(file).getFormat(); 
			
			int wincolom = soapiResult.lastIndexOf(';');
			int linuxcolom = soapiResult.lastIndexOf(':');
			String finalResult = soapiResult;
			String encoding ="";
			//filter the result to add the content type
			// check if ';' is present in the string
			if (wincolom >= 0) {
				//text/plain; charset=us-ascii
				finalResult = soapiResult.substring(0, wincolom);
				encoding =soapiResult.substring(wincolom);
			}
			// check if ':' is present in the string
			if (linuxcolom >= 0) {
				finalResult = soapiResult.substring(linuxcolom + 1);
				encoding =soapiResult.substring(linuxcolom);
			}
			
			if (finalResult != null) {
				fileFormat.setFormat(finalResult);
			}
		}catch (FileFormatIdentifierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e);
		}
		return fileFormat;
	}

}
