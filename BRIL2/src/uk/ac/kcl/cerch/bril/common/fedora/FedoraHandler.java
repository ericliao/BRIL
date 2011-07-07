package uk.ac.kcl.cerch.bril.common.fedora;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.trippi.TupleIterator;
import uk.ac.kcl.cerch.bril.common.config.FedoraConfig;
import org.fcrepo.client.FedoraClient;
import org.fcrepo.server.access.FedoraAPIA;
import org.fcrepo.server.management.FedoraAPIM;
import org.fcrepo.server.types.gen.Datastream;
import org.fcrepo.server.types.gen.DatastreamDef;
import org.fcrepo.server.types.gen.FieldSearchQuery;
import org.fcrepo.server.types.gen.FieldSearchResult;
import org.fcrepo.server.types.gen.MIMETypedStream;
import org.fcrepo.server.types.gen.RelationshipTuple;

public class FedoraHandler {

	private static Logger log = Logger.getLogger(FedoraHandler.class);

	private static FedoraAPIM _fapim;
	private static FedoraAPIA _fapia;
	private static FedoraClient _fc;
	private static String fedoraURL; 

	public FedoraHandler() throws BrilObjectRepositoryException {
		log.debug("FedoraHandle constructor");
		String fedora_base_url;
		String host;
		String port;
		String user;
		String pass;

		try {

			host = FedoraConfig.getHost();
			port = FedoraConfig.getPort();
			user = FedoraConfig.getUser();
			pass = FedoraConfig.getPassPhrase();
			
		} catch (ConfigurationException ex) {
			String error = String
					.format(
							"Failed to obtain configuration values for FedoraHandler: %s",
							ex.getMessage());
			log.error(error);
			throw new BrilObjectRepositoryException(error, ex);
		}
		fedora_base_url = String.format("http://%s:%s/fedora", host, port);
		fedoraURL= fedora_base_url;
		log.debug(String.format(
				"connecting to fedora base using %s, user=%s, pass=%s",
				fedora_base_url, user, pass));

		try {
			_fc = new FedoraClient(fedora_base_url, user, pass);
		} catch (MalformedURLException ex) {
			String error = String.format(
					"Failed to obtain connection to fedora repository: %s", ex
							.getMessage());
			log.error(error);
			throw new BrilObjectRepositoryException(error, ex);

		}
		try {
			_fapia = _fc.getAPIA();
			_fapim = _fc.getAPIM();
		} catch (ServiceException ex) {
			String error = String.format(
					"Failed to obtain connection to fedora repository: %s", ex
							.getMessage());
			log.error(error);
			throw new BrilObjectRepositoryException(error, ex);

		} catch (IOException ex) {
			String error = String.format(
					"Failed to obtain connection to fedora repository: %s", ex
							.getMessage());
			log.error(error);
			throw new BrilObjectRepositoryException(error, ex);

		}
	}

	/*
	 * public static synchronized FedoraHandler getInstance() throws
	 * ConfigurationException, ServiceException, MalformedURLException,
	 * IOException { log.trace("FedoraHandler getInstance"); if (_INSTANCE ==
	 * null) { _INSTANCE = new FedoraHandler(); } return _INSTANCE;
	 * 
	 * }
	 */

	public FedoraAPIM getAPIM() {
		log.trace("FedoraHandle getAPIM");
		return _fapim;
	}

	public FedoraAPIA getAPIA() {
		log.trace("FedoraHandle getAPIA");
		return _fapia;
	}

	public FedoraClient getFC() {
		log.trace("FedoraHandle getFC");
		return _fc;
	}
	
	public String getFedoraURL(){
		return fedoraURL;
	}

	public synchronized String ingest(byte[] data, String datatype, String logmessage)
			throws ConfigurationException, ServiceException, IOException {
		long timer = 0;
		if (log.isDebugEnabled()) {
			timer = System.currentTimeMillis();
		}

		String pid = this.getAPIM().ingest(data, datatype, logmessage);

		if (log.isDebugEnabled()) {
			timer = System.currentTimeMillis() - timer;
			log.trace(String
					.format("Timing: ( %s ) %s", this.getClass(), timer));
		}

		return pid;
	}
	
	public synchronized String uploadFile(File fileToUpload) throws IOException {
		long timer = 0;

		if (log.isDebugEnabled()) {
			timer = System.currentTimeMillis();
		}

		String msg = this.getFC().uploadFile(fileToUpload);

		if (log.isDebugEnabled()) {
			timer = System.currentTimeMillis() - timer;
			log.trace(String
					.format("Timing: ( %s ) %s", this.getClass(), timer));
		}

		return msg;
	}
	
	/**
	 * Change the reference location for a datastream.
	 * @param pid
	 * @param dataStreamID
	 * @param alternativeDsIds
	 * @param dsLabel
	 * @param mimetype
	 * @param StringformatURI
	 * @param dsLocation
	 * @param checksumType
	 * @param checksum
	 * @param logmessage
	 * @param force
	 * @return
	 * @throws IOException
	 */
	public synchronized String modifyDatastreamByReference(String pid, String dataStreamID, String[] alternativeDsIds, String dsLabel, String mimetype,String StringformatURI, 
			String dsLocation, String checksumType,  String checksum,  String logmessage, boolean force) throws IOException {
		long timer = 0;

		if (log.isDebugEnabled()) {
			timer = System.currentTimeMillis();
		}

		String msg = this.getAPIM().modifyDatastreamByReference(pid, dataStreamID, alternativeDsIds, dsLabel, mimetype, StringformatURI, 
				dsLocation, checksumType, checksum, logmessage, force);
		if (log.isDebugEnabled()) {
			timer = System.currentTimeMillis() - timer;
			log.trace(String
					.format("Timing: ( %s ) %s", this.getClass(), timer));
		}

		return msg;
	}

	public String[] getNextPID(int numberOfPids, String prefix)
			throws RemoteException {
		NonNegativeInteger val = new NonNegativeInteger(Integer.toString(numberOfPids));
		String pidlist[]=null;
		
		pidlist=this.getAPIM().getNextPID(val, prefix);
		
		if (pidlist==null){
			log.error("Could not retrieve pids from Fedora repository");
			throw new IllegalStateException("Could not retrieve pids from Fedora repository");
		}
		return pidlist;

	}

	/**
	 * Get the specified datastream
	 * @param objectId
	 * @param dataStreamID
	 * @return
	 * @throws RemoteException
	 */
	public Datastream getDatastream(String objectId, String dataStreamID)
			throws RemoteException {
		Datastream ds = null;
		long timer = 0;
		if (log.isDebugEnabled()) {
			timer = System.currentTimeMillis();
		}
// null get current object view (the most recent time)
		ds = this.getAPIM().getDatastream(objectId, dataStreamID, null);

		if (log.isDebugEnabled()) {
			timer = System.currentTimeMillis() - timer;
			log.trace(String
					.format("Timing: ( %s ) %s", this.getClass(), timer));
		}

		return ds;
	}

	public synchronized Datastream[] getDatastreamXML(String objectId)
			throws RemoteException {
		long timer = 0;
		if (log.isDebugEnabled()) {
			timer = System.currentTimeMillis();
		}

		Datastream[] ds = this.getAPIM().getDatastreams(objectId, null,
				null);
		
		if (log.isDebugEnabled()) {
			timer = System.currentTimeMillis() - timer;
			log.trace(String
					.format("Timing: ( %s ) %s", this.getClass(), timer));
		}

		return ds;
	}

	
	public String addDatastream(String pid, String datastreamID,
			String[] alternativeDsIds, String dsLabel, boolean versionable,
			String MIMEType, String formatURI, String dsLocation,
			String controlGroup, String datastreamState, String checksumType,
			String checksum, String logmessage) throws ConfigurationException,
			ServiceException, MalformedURLException, IOException {
		long timer = 0;

		if (log.isDebugEnabled()) {
			timer = System.currentTimeMillis();
		}

		String returnedSID = this.getAPIM().addDatastream(pid, datastreamID,
				alternativeDsIds, dsLabel, versionable, MIMEType, formatURI,
				dsLocation, controlGroup, datastreamState, checksumType,
				checksum, logmessage);
		if (log.isDebugEnabled()) {
			timer = System.currentTimeMillis() - timer;
			log.trace(String
					.format("Timing: ( %s ) %s", this.getClass(), timer));
		}

		return returnedSID;
	}

	public synchronized boolean addRelationship(String pid, String predicate, String object,
			boolean isLiteral, String datatype) throws ConfigurationException,
			ServiceException, MalformedURLException, IOException {
		long timer = 0;

		if (log.isDebugEnabled()) {
			timer = System.currentTimeMillis();
		}

		boolean ret = this.getAPIM().addRelationship(pid, predicate, object,
				isLiteral, datatype);

		if (log.isDebugEnabled()) {
			timer = System.currentTimeMillis() - timer;
			log.trace(String
					.format("Timing: ( %s ) %s", this.getClass(), timer));
		}

		return ret;
	}

	FieldSearchResult findObjects(String[] resultFields,
			NonNegativeInteger maxResults, FieldSearchQuery fsq)
			throws ConfigurationException, MalformedURLException, IOException,
			ServiceException {
		long timer = 0;

		if (log.isDebugEnabled()) {
			timer = System.currentTimeMillis();
		}

		FieldSearchResult fsr = this.getAPIA().findObjects(resultFields,
				maxResults, fsq);

		if (log.isDebugEnabled()) {
			timer = System.currentTimeMillis() - timer;
			log.trace(String
					.format("Timing: ( %s ) %s", this.getClass(), timer));
		}

		return fsr;
	}

	FieldSearchResult resumeFindObjects(String token)
			throws ConfigurationException, MalformedURLException, IOException,
			ServiceException {
		FieldSearchResult fsr = this.getAPIA().resumeFindObjects(token);
		return fsr;
	}
	
	public DatastreamDef[] listDatastreams(String pid, String datastreamId) throws RemoteException{
		DatastreamDef[] datastreams = this.getAPIA().listDatastreams(pid, datastreamId);
		
		return datastreams;
	}
	
	public byte[] getDatastreamDissemination(String pid, String datastreamId, String asOfDataTime) throws RemoteException{
		MIMETypedStream ds = null;
		
		ds =this.getAPIA().getDatastreamDissemination(pid,datastreamId , asOfDataTime);
		
		return ds.getStream();
	}

	/**
	 * Gets the relationship asserted in the object's RELS-EXT or RELS-INT
	 * Datastream that matches the given criteria
	 * 
	 * @param subject
	 *            The subject either a fedora object URI (e.g., bril:111)
	 * @param predicate
	 *            The predicate that matches. A null value matches all
	 *            predicates.
	 * @throws ConfigurationException
	 *             , ServiceException, MalformedURLException, IOException
	 * */
	public RelationshipTuple[] getRelationships(String subject, String predicate)
			throws ConfigurationException, ServiceException,
			MalformedURLException, IOException {
		long timer = 0;

		if (log.isDebugEnabled()) {
			timer = System.currentTimeMillis();
		}

		RelationshipTuple[] rt = this.getAPIM().getRelationships(subject,
				predicate);

		if (log.isDebugEnabled()) {
			timer = System.currentTimeMillis() - timer;
			log.trace(String
					.format("Timing: ( %s ) %s", this.getClass(), timer));
		}

		return rt;
	}

	/**
	 * Gets tuples from the fedora resource index. The Map consists of 2
	 * parameters 1) lang (e.g., itql or sparql ) 2)query (e.g., select $s from
	 * <#ir> where $s <%s> <%s>;)
	 * 
	 * @param params
	 *            This is the map consists of parameters that should be passed
	 *            to the service
	 * @return
	 * @throws ConfigurationException
	 * @throws ServiceException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public TupleIterator getTuples(Map<String, String> params)
			throws ConfigurationException, ServiceException,
			MalformedURLException, IOException {
		long timer = 0;

		if (log.isDebugEnabled()) {
			timer = System.currentTimeMillis();
		}

		TupleIterator tuples = this.getFC().getTuples(params);
		if (log.isDebugEnabled()) {
			timer = System.currentTimeMillis() - timer;
			log.trace(String
					.format("Timing: ( %s ) %s", this.getClass(), timer));
		}

		return tuples;
	}
	

	public boolean hasObject(String identifier) throws RemoteException {
		try {

			DatastreamDef[] d = this.getAPIA().listDatastreams(identifier, null);
			log.debug(String.format("length of DatastreamDef: '%s'", d.length));
		} catch (IOException ioe) {
			return false;
		}

		return true;
	}
	
	synchronized String[] purgeDatastream(String pid, String sID, String startDate, String endDAte, String logmessage,boolean breakDep) throws RemoteException{
		long timer =0;
		if(log.isDebugEnabled()){
			timer =System.currentTimeMillis();
			
		}
		
		String[]rt = this.getAPIM().purgeDatastream(pid, sID, startDate, endDAte, logmessage, breakDep);

		if(log.isDebugEnabled()){
			timer =System.currentTimeMillis()- timer;
			log.trace(String
					.format("Timing: ( %s ) %s", this.getClass(), timer));
		}
		return rt;
	}
	
	public synchronized String purgeObject(String identifier, String logmessage, boolean force) throws RemoteException{
		long timer =0;
		if(log.isDebugEnabled()){
			timer =System.currentTimeMillis();
			
		}
		
		String timestamp =this.getAPIM().purgeObject(identifier, logmessage, force);
		
		if(log.isDebugEnabled()){
			timer =System.currentTimeMillis()- timer;
			log.trace(String
					.format("Timing: ( %s ) %s", this.getClass(), timer));
		}
		
		return timestamp;
	}
	
	synchronized boolean purgeRelationship(String identifier, String predicate, String object, boolean isLiteral, String datatype) throws RemoteException{
		long timer =0;
		if(log.isDebugEnabled()){
			timer =System.currentTimeMillis();
			
		}
		
		boolean ret =this.getAPIM().purgeRelationship(identifier, predicate, object, isLiteral, datatype);
		
		if(log.isDebugEnabled()){
			timer =System.currentTimeMillis()- timer;
			log.trace(String
					.format("Timing: ( %s ) %s", this.getClass(), timer));
		}
		
		return ret;
	}
	
	

}