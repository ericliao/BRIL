package uk.ac.kcl.cerch.bril.sip;

import uk.ac.kcl.cerch.soapi.sip.SIP;



public class BrilSIP extends SIP{
private String filePath;
private String metadataXMlString;
private String identifier;
	public BrilSIP(String filePath){
		this.filePath=filePath;
	}
	
	public void setFilePath(String filePath){
		this.filePath=filePath;
	}
	public String getFilePath(){
		return filePath;
	}
	public void setMetadataXMLString(String metadataXMlString){
		this.metadataXMlString=metadataXMlString;
	}
	
	public String getMetadataXMLString(){
		return metadataXMlString;
	}
	
	public void setIdentifier(String identifier){
		this.identifier=identifier;
	}
	
	public String getIdentifier(){
		return identifier;
	}
	
	

	
}
