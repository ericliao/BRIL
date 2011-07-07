package uk.ac.kcl.cerch.bril.relationship;

import java.io.Serializable;

public class Relationship  implements Serializable {

	private String subject;
	private String predicate;
	private String object;

	/**
	 * 
	 * @param subject
	 */
	public void setSubject(String subject) {
		this.subject=subject;
	}

	public String getSubject() {
		return subject;
	}

	/**
	 * 
	 * @param predicate
	 */
	public void setPredicate(String predicate) {
		this.predicate=predicate;
	}

	public String getPredicate() {
		return predicate;
	}

	/**
	 * 
	 * @param object
	 */
	public void setObject(String object) {
		this.object=object;
	}

	public String getObject() {
		return object;
	}

}