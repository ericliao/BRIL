package uk.ac.kcl.cerch.soapi.objectstore;

/**
 * Represents an <code>FileFormat</code> object
 * 
 */
@SuppressWarnings("serial")
public class FileFormat extends ObjectArtifact {
    private String format;
    private String description;
    private String version;
    private String suffix;
    private String mime;
    private String status;
    private String relatedObjectArtifactId; // ObjectArtifact Id whose file format is represented.    
    
    /**
     * Gets the format of the file.
     * 
     * @return Returns the format.
     */
    public String getFormat() {
        return format;
    }
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the format of the file.
     * 
     * @param The Format to set.
     */
    public void setFormat(String format) {
        this.format = format;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setFileSuffix(String suffix) {
        this.suffix = suffix;
    }
    
    public String getFileSuffix() {
        return suffix;
    }
    public void setMimeType(String mime) {
        this.mime = mime;
    }
    
    public String getMimeType() {
        return mime;
    }

    
    /**
     * Gets the version of the file.
     * 
     * @return Returns the version.
     */
    public String getVersion()
    {
        return version;
    }
    
    /**
     * Sets the version of the file.
     * 
     * @param The Version to set.
     */
    public void setVersion(String Version)
    {
        this.version = Version;
    }
    
/*    *//**
     * Returns the metadata of the file.
     * 
     * @return Returns the metadata.
     *//*
    public String getMetadata()
    {
        return metadata;
    }
    
    *//**
     * Sets the metadata of the file.
     * 
     * @param The Metadata to set.
     *//*
    public void setMetadata(String metadata)
    {
        this.metadata = metadata;
    }*/
    
    /**
     * Gets the status of the file. 
     * 
     * @return The status of the file
     */
    public String getStatus() 
    {
        return status;
    }

    /**
     * Sets the status of the file.
     * 
     * @param The status of the file
     */
    public void setStatus(String status) 
    {
        this.status = status;
    }        

    /**
     * Returns the Id of the <code>ObjectArtifact</code> whose file format is represented.
     * 
     * @return Related <code>ObjectArtifact</code> Id.
     */
    public String getRelatedObjectArtifactId() {
        return relatedObjectArtifactId;
    }

    /**
     * Sets the Id of the <code>ObjectArtifact</code> whose file format is represented.
     * 
     * @param relatedObjectArtifactId Related <code>ObjectArtifact</code> Id.
     */
    public void setRelatedObjectArtifactId(String relatedObjectArtifactId) {
        this.relatedObjectArtifactId = relatedObjectArtifactId;
    }
}
