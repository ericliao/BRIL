package uk.ac.kcl.cerch.soapi.objectstore;

/**
 * <code>FileCharacterisation</code> represents the characterisation of a file. The characterisation
 * could be represented using one of many file characterisation metadata strategies e.g. PREMIS
 * (http://www.oclc.org/research/projects/pmwg). 
 * 
 * @author Vijay N Albuquerque
 *
 */
@SuppressWarnings("serial")
public class FileCharacterisation extends ObjectArtifact {
    private String label; // Description label.
    private String metadata; // Characterisation metadata.
    private String relatedObjectArtifactId; // ObjectArtifact Id whose characterisation is represented.

    /**
     * Returns the descriptive label.
     * 
     * @return Descriptive label.
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * Sets the descriptive label.
     * 
     * @param Descriptive label.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Returns the characterisation metadata.
     * 
     * @return Characterisation metadata.
     */
    public String getMetadata() {
        return metadata;
    }
    
    /**
     * Sets the characterisation metadata.
     * 
     * @param Characterisation metadata.
     */
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    /**
     * Returns the Id of the <code>ObjectArtifact</code> whose characterisation is represented.
     * 
     * @return Related <code>ObjectArtifact</code> Id.
     */
    public String getRelatedObjectArtifactId() {
        return relatedObjectArtifactId;
    }

    /**
     * Sets the Id of the <code>ObjectArtifact</code> whose characterisation is represented.
     * 
     * @param relatedObjectArtifactId Related <code>ObjectArtifact</code> Id.
     */
    public void setRelatedObjectArtifactId(String relatedObjectArtifactId) {
        this.relatedObjectArtifactId = relatedObjectArtifactId;
    }
}
