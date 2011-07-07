package uk.ac.kcl.cerch.bril.relationship.generator;

import uk.ac.kcl.cerch.bril.relationship.ObjectRelationship;

/**
 * @author Shri
 *
 */
public interface ObjectRelationshipGenerator {


	/**
	 * @param objectID
	 * @param experimentId
	 * @return ObjectRelationship containing list of Relationship objects
	 */
	public ObjectRelationship generateRelationships(String objectID, String experimentId);

}