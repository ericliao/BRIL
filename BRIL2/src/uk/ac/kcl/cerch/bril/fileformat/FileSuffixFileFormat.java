package uk.ac.kcl.cerch.bril.fileformat;
import java.io.File;

import uk.ac.kcl.cerch.soapi.fileformat.*;

public class FileSuffixFileFormat extends FileFormat{
	private String extension;
	private File file;
	
	/**
	 * 
	 * @param extension
	 */
	public void setExtension(String extension) {
		this.extension=extension;
	}

	public String getExtension() {
		return this.extension;
	}


}
