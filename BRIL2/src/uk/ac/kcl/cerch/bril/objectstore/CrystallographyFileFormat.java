package uk.ac.kcl.cerch.bril.objectstore;

public class CrystallographyFileFormat {
	    private String format;
	    private String description;
	    private String suffix;
	    private String mime;
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
	    
	    /**
	     * @param suffix
	     */
	    public void setFileSuffix(String suffix) {
	        this.suffix = suffix;
	    }
	    
	    /**
	     * @return
	     */
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
