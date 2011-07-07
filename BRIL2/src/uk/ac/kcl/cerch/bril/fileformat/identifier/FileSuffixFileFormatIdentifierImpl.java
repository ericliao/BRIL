package uk.ac.kcl.cerch.bril.fileformat.identifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import java.util.StringTokenizer;

import uk.ac.kcl.cerch.bril.common.util.FileUtil;
import uk.ac.kcl.cerch.bril.fileformat.FileSuffixFileFormat;
import uk.ac.kcl.cerch.soapi.fileformat.FileFormat;
import uk.ac.kcl.cerch.soapi.fileformat.identifier.FileFormatIdentifierException;

public class FileSuffixFileFormatIdentifierImpl implements
		FileSuffixFileFormatIdentifier {
	private Properties properties;

	@Override
	public FileFormat identifyFileFormat(File file)
			throws FileFormatIdentifierException {
		// TODO Auto-generated method stub
		try {
			properties = new Properties();
			properties.load(getClass().getClassLoader().getResourceAsStream(
					"brilfileformat.properties"));
		} catch (IOException ex) {
			String error = String.format(
					"Failed to load brilfileformat.properties file.: %s", ex
							.getMessage());
			// log.error( error );
			throw new FileFormatIdentifierException(error, ex);

		}

		FileFormat fsff = new FileSuffixFileFormat();
		FileUtil.setFileName(file.getAbsolutePath());
		String ext = FileUtil.getFileNameExtension();
		@SuppressWarnings("unused")
		String type = null;
		((FileSuffixFileFormat) fsff).setExtension(ext);

		/*
		 * iterate properties file that contains the list of file extensions and
		 * their corresponding description of the content type
		 
		for (String key : properties.stringPropertyNames()) {
			if (key.equals(ext) || key == ext) {
				type = properties.getProperty(key);
				if (ext.equals("def")) {
					//type = checkSuffixDEF(file, key);
				}

			}

		}*/
		((FileSuffixFileFormat) fsff).setFormat(type);

		return fsff;
	}



}
