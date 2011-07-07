package uk.ac.kcl.cerch.soapi.objectstore;

/**
 * <code>DisseminationManifestation</code> contains a file path to the dissemination manifestation of the archival
 * object. It is a file representation of the archival object that has been optimized and produced for dissemination 
 * purposes.
 * <br/>
 * Often there would be more than one <code>DisseminationManifestation</code> object for a single archival
 * object (e.g. Images would have thumbnail, medium and large dissemination manifestations.) Each of these objects
 * are independent of the others.
 * 
 * @author Vijay N Albuquerque
 *
 */
@SuppressWarnings("serial")
public class DisseminationManifestation extends ObjectArtifact {
    private String label; // Description label.
    private String filePath; // File path to the dissemination manifestation.

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