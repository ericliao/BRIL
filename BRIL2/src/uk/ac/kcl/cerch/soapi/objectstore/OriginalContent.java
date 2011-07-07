/*
 * Created on 30 Aug 2007
 */
package uk.ac.kcl.cerch.soapi.objectstore;

/**
 * <code>OriginalContent</code> contains a file path to the data content of the archival
 * object.
 * 
 * @author Vijay N Albuquerque
 *
 */
@SuppressWarnings("serial")
public class OriginalContent extends ObjectArtifact {
    private String label; // Description label.
    private String filePath; // File path to the original content.
    
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
     * Gets the file path to the original content.
     * 
     * @return File path to the original content.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Sets the file path to the original content.
     * 
     * @param filePath File path to the original content.
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
