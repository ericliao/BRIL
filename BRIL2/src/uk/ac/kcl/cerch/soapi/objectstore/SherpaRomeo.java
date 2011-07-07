/**
 * 
 */
package uk.ac.kcl.cerch.soapi.objectstore;

/**
 * @author rishis
 * This is file which will hold the result received data from sherpa romeo service, Basically Meta data 
 */
@SuppressWarnings("serial")
public class SherpaRomeo extends ObjectArtifact {
	
	private String metadata;
	private String relatedObjectArtifactId;
	/**
	 * @return the metadata
	 */
	public String getMetadata() {
		return metadata;
	}
	/**
	 * @param metadata the metadata to set
	 */
	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}
	/**
	 * @return the relatedObjectArtifactId
	 */
	public String getRelatedObjectArtifactId() {
		return relatedObjectArtifactId;
	}
	/**
	 * @param relatedObjectArtifactId the relatedObjectArtifactId to set
	 */
	public void setRelatedObjectArtifactId(String relatedObjectArtifactId) {
		this.relatedObjectArtifactId = relatedObjectArtifactId;
	}
}
