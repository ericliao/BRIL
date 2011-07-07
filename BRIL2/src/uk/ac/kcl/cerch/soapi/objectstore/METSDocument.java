package uk.ac.kcl.cerch.soapi.objectstore;

/**
 * An <code>ObjectArtifact</code> that represents a METS Document. 
 *  
 * @author Andreas Mavrides
 *
 */

@SuppressWarnings("serial")
public class METSDocument extends ObjectArtifact {
    private String metsDocument;

    /**
     * Gets the METS Document <code>String</code>
     * 
     * @return  METS Document <code>String</code>
     */
    public String getMETSDocument() {
        return metsDocument;
    }

    /**
     * Sets the METS Document <code>String</code>
     * 
     * @param  METS Document <code>String</code> to be set
     */
    public void setMETSDocument(String metsDocument) {
        this.metsDocument = metsDocument;
    }
}
