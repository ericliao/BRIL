package uk.ac.kcl.cerch.bril.common.fedora;

import org.fcrepo.server.types.gen.Datastream;
import org.fcrepo.server.types.gen.ObjectFields;
import uk.ac.kcl.cerch.bril.common.types.*;

public interface FedoraAdministration {

	/**
	 * 
	 * @param dsContainer
	 */
	String storeObject (DatastreamObjectContainer dsContainer)throws BrilObjectRepositoryException;

	/**
	 * 
	 * @param pid
	 */
	DatastreamObjectContainer retrieveObject(String objectId);
	
	/**
	 * 
	 * @param pid
	 * @throws BrilObjectRepositoryException 
	 */
	DatastreamObjectContainer retrieveDataFromObject(String objectId, DataStreamType streamtype) throws BrilObjectRepositoryException;

	/**
	 * 
	 * @param property
	 * @param operator
	 * @param value
	 */
	ObjectFields[] findObjectPids(String propertyFieldName, String comparisionOperator, String value, int maximumResult);

	/**
	 * 
	 * @param resultFields
	 * @param property
	 * @param value
	 */
	public ObjectFields[] findObjectFields(String[] resultFields, String propertyFieldName, String comparisionOperator, String value, int maximumResult);
    public Datastream getDatastream( String objectId, String dataStreamID ) throws BrilObjectRepositoryException;
	
	public void addObjectRelation(String subjectIdentifier, String relation,  String objectIdentifier, boolean literal) throws BrilObjectRepositoryException;
	
}