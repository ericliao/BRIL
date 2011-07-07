package uk.ac.kcl.cerch.bril.objectstore;

import java.util.List;

import uk.ac.kcl.cerch.bril.relationship.Relationship;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectArtifact;

@SuppressWarnings("serial")
public class RelationshipObject extends ObjectArtifact {
	private List<Relationship> relationships;
	private String relatedObjectArtifactId;
	
	public List<Relationship> getRelationships() {
		return relationships;
	}
	public void setRelationships(List<Relationship>relationships){
		this.relationships=relationships;
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
