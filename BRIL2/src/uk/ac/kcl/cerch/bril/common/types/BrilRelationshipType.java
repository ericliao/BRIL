package uk.ac.kcl.cerch.bril.common.types;

import org.apache.log4j.Logger;

public enum BrilRelationshipType {
	wasDerivedFrom("wasDerivedFrom", "relates to the input that was used in some process that derived this output"),
	wasGeneratedBy("wasGeneratedBy", "relates to name of the process used to generate this output"),
	used("used", "relates to the input used by this process"),
	wasTriggeredBy("wasTriggeredBy", "relates to the process that triggered this process"),
	wasControlledBy("wasControlledBy", "relates to the user/software that controlled this process");

	private final String relation;
	private final String description;
	static Logger log = Logger.getLogger( BrilRelationshipType.class );
	
	/**
	 * 
	 * @param relation
	 * @param description
	 */
	BrilRelationshipType(String relation, String description) {
		this.relation = relation;
        this.description = description;
	}
	

	/**
	 * @return The name of the relation. 
	 */
	public String getRelation() {
		return this.relation;
	}

	/**
	 * @return The description of the relation
	 */
	public String getDescription() {
		return this.description;
	}
}
