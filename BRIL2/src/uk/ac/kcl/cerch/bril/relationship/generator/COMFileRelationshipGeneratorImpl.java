/**
 * 
 */
package uk.ac.kcl.cerch.bril.relationship.generator;

import uk.ac.kcl.cerch.bril.relationship.ObjectRelationship;

/**
 * @author Shri
 *
 */
public class COMFileRelationshipGeneratorImpl implements COMFileCCP4RelationshipGenerator{
	private ObjectRelationship objectRelationship;
	@Override
	public ObjectRelationship generateRelationships(String objectID,
			String experimentId) {
		if (experimentId.contains("bril:") == false) {
			experimentId = "bril:" + experimentId;
		}
		 objectRelationship = new ObjectRelationship();

		objectRelationship.addRelationship(objectID, "isPartOf", experimentId);
		
		return objectRelationship;

	}

}
