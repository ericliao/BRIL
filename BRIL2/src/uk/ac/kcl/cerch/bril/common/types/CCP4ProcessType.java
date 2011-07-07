package uk.ac.kcl.cerch.bril.common.types;

import org.apache.log4j.Logger;

public enum CCP4ProcessType {
	
	sortmtz("SORTMTZ", "Sort a MTZ reflection data file"),
	scala("SCALA", "Scale together multiple observations of reflections"),
	truncate("TRUNCATE", "Obtain structure factor amplitudes using Truncate procedure"),
	freerflag("FREERFLAG", "tags each reflection in an MTZ file with a flag for cross-validation");		

	private final String task;
	private final String description;
	static Logger log = Logger.getLogger( BrilRelationshipType.class );
	
	/**
	 * 
	 * @param relation
	 * @param description
	 */
	CCP4ProcessType(String task, String description) {
		this.task = task;
        this.description = description;
	}
	

	/**
	 * @return The name of the relation. 
	 */
	public String getTask() {
		return this.task;
	}

	/**
	 * @return The description of the relation
	 */
	public String getDescription() {
		return this.description;
	}
}
