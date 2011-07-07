package uk.ac.kcl.cerch.soapi.objectstore;

import java.util.Date;

/**
 * <code>Checksum</code> represents the checkum value for a file. The checksum is contained as a <code>String</code>.
 * The <code>algorithm</code> property denotes the algorithm used to generate the checksum value (e.g. MD5, SHA-1, SHA-256 etc.).
 * 
 * @author Andreas Mavrides
 * @author Vijay N Albuquerque
 *
 */
@SuppressWarnings("serial")
public class Checksum extends ObjectArtifact {
    private String checksum;
    private String algorithm;
    private String relatedObjectArtifactId; // ObjectArtifact Id whose checksum is represented.
    private Date dateCreated;
    
    /**
     * Gets the date value
     * 
     * @return dateCreated <code>Date</code>
     */
    public Date getDateCreated() {
        return dateCreated;
    }

    /**
     * Sets the date value
     * 
     * @param date The date <code>String</code> value.
     */
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * Gets the checksum value.
     * 
     * @return Checksum <code>String</code> value.
     */
    public String getChecksum() {
        return checksum;
    }
    
    /**
     * Sets the checksum value.
     * 
     * @param checksum The checksum <code>String</code> value.
     */
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    /**
     * Gets the checksum algorithm type (e.g. MD5, SHA-1, SHA-256 etc.).
     * 
     * @return Algorithm type.
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * Sets the checksum algorithm type (e.g. MD5, SHA-1, SHA-256 etc.).
     * 
     * @param algorithm The checksum algorithm type.
     */
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * Returns the Id of the <code>ObjectArtifact</code> whose checksum is represented.
     * 
     * @return Related <code>ObjectArtifact</code> Id.
     */
    public String getRelatedObjectArtifactId() {
        return relatedObjectArtifactId;
    }
    
    /**
     * Sets the Id of the <code>ObjectArtifact</code> whose checksum is represented.
     * 
     * @param relatedObjectArtifactId Related <code>ObjectArtifact</code> Id.
     */
    public void setRelatedObjectArtifactId(String relatedObjectArtifactId) {
        this.relatedObjectArtifactId = relatedObjectArtifactId;
    }
}
