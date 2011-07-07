package uk.ac.kcl.cerch.bril.fileformat;

import uk.ac.kcl.cerch.soapi.fileformat.FileFormat;

public class MIMETypeFileFormat extends FileFormat{
	private String mimeEncoding;
	
	/**
	 * 
	 * @param type  encoding of content or charset for example; binary, us-ascii
	 */
	public void setMimetEncoding(String mimeEncoding) {
		this.mimeEncoding=mimeEncoding;
	}

	public String getMimetEncoding() {
		return this.mimeEncoding;
	}
}
