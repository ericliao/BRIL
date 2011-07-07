package uk.ac.kcl.cerch.bril.relationship;

import java.util.ArrayList;
import java.util.List;

/**
 * The class holds the Object relationship for a digital object. This defined methods to have provenance 
 * relationships between objects that is partly based on the OPM specification. The relationships that this
 * class holds only pertains to the OPM arifact (digital object).  
 */
public class ObjectRelationship {

	private List<Relationship> relationships;
	
	public ObjectRelationship(){
		relationships = new ArrayList<Relationship>();
		
	}

	/**
	 * Creates a Relationship object with these triples and puts the object in the list
	 * @param subject
	 * @param predicate
	 * @param object
	 *
	 ***/
	public void addRelationship(String subject, String predicate, String object) {
		Relationship triple = new Relationship();
		triple.setSubject(subject);
		triple.setPredicate(predicate);
		triple.setObject(object);
		this.relationships.add(triple);
	}
	
	public void addRelationship(Relationship relationship){
		this.relationships.add(relationship);
	}

	/**
	 * 
	 * @param objectId
	 */
	public List<Relationship> getRelationshipsForObjectId(String objectId) {
		List <Relationship>lisOfRelationshipObject = new ArrayList<Relationship>();
		if (relationships != null && relationships.size() >= 1) {
			for (Relationship rel:relationships) {
				String subject = rel.getSubject();
				if (subject.equals(objectId)){
					lisOfRelationshipObject.add(rel);
				}
		    }
		}
		return lisOfRelationshipObject;
	}

	public List<Relationship>  getRelationships() {
		return relationships;
	}

}