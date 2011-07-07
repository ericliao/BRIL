package uk.ac.kcl.cerch.bril.fileformat.validator;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import uk.ac.kcl.cerch.bril.common.types.DatastreamMimeType;
import uk.ac.kcl.cerch.bril.fileformat.BrilFileFormatValidatorException;
import uk.ac.kcl.cerch.bril.fileformat.FormatValidation;
import uk.ac.kcl.cerch.bril.fileformat.identifier.FileSuffixFileFormatIdentifierImpl;
import uk.ac.kcl.cerch.bril.fileformat.identifier.MIMETypeFileFormatIdentifierImpl;
import uk.ac.kcl.cerch.soapi.fileformat.FileFormat;
import uk.ac.kcl.cerch.soapi.fileformat.identifier.FileFormatIdentifierException;

public class BrilFileFormatValidatorImpl implements BrilFileFormatValidator {
	private Properties properties;
	FileFormat fsff;
	FileFormat mtff;

	@Override
	public FormatValidation validateBrilFileFormat(File file)
			throws FileFormatIdentifierException {
		// TODO Auto-generated method stub
		FormatValidation fv = new FormatValidation();
		FileSuffixFileFormatIdentifierImpl fsff_Identifier = new FileSuffixFileFormatIdentifierImpl();
		MIMETypeFileFormatIdentifierImpl mtff_Identifier = new MIMETypeFileFormatIdentifierImpl();
		try {
			fsff = fsff_Identifier.identifyFileFormat(file);
			mtff = mtff_Identifier.identifyFileFormat(file);
			String suffixType = fsff.getFormat();
			String mimeType = mtff.getFormat();

			/*
			 * check for valid mimetype supported in the repository using an
			 * enum list of supported mimetypes
			 */
			boolean validMimeType = DatastreamMimeType.validMimetype(mimeType);

			// check if the mimetype and content type matches as in
			// brilcontent.properties file
			boolean validBrilFileFormat = validBrilFileFormat(mimeType.trim(),suffixType.trim());

			if (validMimeType == true && suffixType != null) {
				// System.out.println("validMimeType and contentType not null");
				if (validBrilFileFormat == true) {
					if (mimeType.equals("application/x-bzip2")) {
						//TODO default is true
						if (isACompressedDiffractionImage(file) == true) {
							fv.setResult(true);
							// System.out.println("valid;;"+validBrilContent);
							String info = String
									.format(
											"Validation based on the repo allowed mimetype '%s' and file suffix type '%s': Also both matches the requirement of /'file suffix must be of mimetype/'.",
											mimeType, suffixType);
							fv.setInfo(info);
						}else{
							fv.setResult(false);
							// System.out.println("valid;;"+validBrilContent);
							String info = String
									.format(
											"Validation based on the repo allowed mimetype '%s' and file suffix type '%s': Also both matches the requirement of /'file suffix must be of mimetype/'.",
											mimeType, suffixType);
							fv.setInfo(info);
						}
					} else {
						fv.setResult(true);
						// System.out.println("valid;;"+validBrilContent);
						String info = String
								.format(
										"Validation based on the repo allowed mimetype '%s' and file suffix type '%s': Also both matches the requirement of /'file suffix must be of mimetype/'.",
										mimeType, suffixType);
						fv.setInfo(info);
					}
				}else{
					fv.setResult(false);
					String info = String
					.format(
							"Validation based on the repo allowed mimetype '%s' and file suffix type '%s': DO NOT match the requirement of /'file suffix must be of mimetype/'.",
							mimeType, suffixType);
			fv.setInfo(info);
				}

			}// contentType is not present in brilfileformat.properties file
			else if (validMimeType == true && suffixType == null) {
				fv.setResult(false);
				String info = String
						.format(
								"Valid (allowed) mimetype '%s': But file suffix type '%s' is a Not a valid bril file format.",
								mimeType, suffixType);
				fv.setInfo(info);

			} else {
				fv.setResult(false);
			}
			
		} catch (FileFormatIdentifierException e) {
			e.printStackTrace();
		}catch (BrilFileFormatValidatorException e) {
			e.printStackTrace();
		}
		return fv;
	}
	
	/**
	 * @return FileSuffixFileFormat object
	 */
	public FileFormat getFileSuffixFileFormat(){
		return fsff;
	}
	
	/**
	 * @return MIMETypeFileFormat object
	 */
	public FileFormat getMIMETypeFileFormat(){
		return mtff;
	}

	private boolean validBrilFileFormat(String mime, String content)
			throws BrilFileFormatValidatorException {
		boolean result = false;
		try {
			properties = new Properties();
			properties.load(getClass().getClassLoader().getResourceAsStream(
					"brilcontent.properties"));
		} catch (IOException ex) {
			String error = String.format(
					"Failed to load brilfileformat.properties file.: %s", ex
							.getMessage());

			throw new BrilFileFormatValidatorException(error, ex);

		}
		String mimetype = "";
		// System.out.println("REached here=-------------"+ content);
		for (String key : properties.stringPropertyNames()) {
			// System.out.println("Key content type-------------"+
			// properties.stringPropertyNames());
			// mimetype= properties.getProperty(content);
			if (key.equals(content) || key == content) {
				mimetype = properties.getProperty(key);

			}
			/*
			 * System.out.println(properties.getProperty("Mosflm_saved_metadata")
			 * ); System.out.println(properties.getProperty("Alignment_file"));
			 * System.out.println(properties.getProperty("Parameter_phenix"));
			 * System.out.println(properties.getProperty(content.trim()));
			 */

		}
		// System.out.println("Got mime------------: "+mimetype);
		// System.out.println("check mime------------: "+mime);
		if (mimetype.equals(mime) || mimetype == mime) {
			// System.out.println("prop-------------"+ mime);
			result = true;
		}
		return result;

	}
	
	

	private boolean isACompressedDiffractionImage(File file) {
		boolean diffImage = true;
		//identify that the compressed file is diffraction image by extracting the file and getting the mimetype of the file
		// TODO
		// boolean compressed = uncompressBZ2File(file);
		/*
		 * File newFile = new File(newFilename);
		 * MIMETypeFileObjectFormatIdentifierImpl mt = new
		 * MIMETypeFileObjectFormatIdentifierImpl(); FileObjectFormat fof =
		 * mt.identifyObjectFormat(newFile); if
		 * (fof.getFormat().equals("application/octet")){ diffImage= true; }
		 */
		return diffImage;
	}

}
