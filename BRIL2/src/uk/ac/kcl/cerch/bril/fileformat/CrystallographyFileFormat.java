package uk.ac.kcl.cerch.bril.fileformat;

import uk.ac.kcl.cerch.soapi.fileformat.FileFormat;

public class CrystallographyFileFormat extends FileFormat{

	private String description;
	private String fileSuffix;
	private String mimeType;
	
	public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getFileSuffix(){
    	return fileSuffix;
    }
    
    public void setFileSuffix(String suffix){
    	this.fileSuffix=suffix;
    }
    
    public String getMimeType(){
    	return mimeType;
    }
    
    public void setMimeType(String mimeType){
    	this.mimeType=mimeType;
    }
}
