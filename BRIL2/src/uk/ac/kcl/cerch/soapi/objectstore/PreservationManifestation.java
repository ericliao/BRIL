package uk.ac.kcl.cerch.soapi.objectstore;

/**
 * <code>PreservationManifestation</code> contains a file path to the preservation manifestation (normalised format) of the archival
 * object. It is a file representation of the archival object that has been optimized and produced for preservation 
 * purposes.
 * 
 * @author Vijay N Albuquerque
 *
 */
@SuppressWarnings("serial")
public class PreservationManifestation extends ObjectArtifact {
    private String label; // Description label.
    private String filePath; // File path to the preservation manifestation.

    /**
     * Gets the description label. The label is <i>optional</i> and only exists to
     * describe the manifestation.
     * 
     * @return Description label.
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * Sets the description label. The label is <i>optional</i> and only exists to
     * describe the manifestation.
     * 
     * @param label Description label.
     */
    public void setLabel(String label) {
        this.label = label;
    }
    
    /**
     * Gets the file path to the dissemination manifestation.
     * 
     * @return File path to the dissemination manifestation.
     */
    public String getFilePath() {
        return filePath;
    }
    
    /**
     * Sets the file path to the dissemination manifestation.
     * 
     * @param filePath File path to the dissemination manifestation.
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}