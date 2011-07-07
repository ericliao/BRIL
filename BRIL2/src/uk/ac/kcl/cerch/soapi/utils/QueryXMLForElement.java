package uk.ac.kcl.cerch.soapi.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryXMLForElement {

    
    // TODO Method is used standalone where needed; (i.e CharacteriserEventAdvice). Class to be used if required 
    public String getValueByElement(String XML, String element)
    {
        String value = null;
        
        String[] metadataLines = XML.split("\n");
            
        for(String metadataLine : metadataLines) {
            Pattern pattern = Pattern.compile("<" + element + ">(.*)</" + element + ">");
            Matcher matcher = pattern.matcher(metadataLine.trim());
            if(matcher.matches()) {
                value = matcher.group(1);
            }
        }
        return value;
    }
    
    // This method is a helper method and only works for JHOVE metadata
    public String getMimeTypeFromJHOVEMetadata(String metadata)
    {
        String mimeType = null;
        
        mimeType = this.getValueByElement(metadata, "mimeType");
        
        if( mimeType.equals("application/octet-stream") 
                && (this.getValueByElement(metadata, "module") != null))
        {
            mimeType = this.getValueByElement(metadata, "module");
        }
        System.out.println(mimeType);
        
        if(mimeType.contains("TIFF"))
            mimeType = "image/tiff";
        
        return mimeType;
    }
}
