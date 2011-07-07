package uk.ac.kcl.cerch.bril.common.types;

/**
 * DatastreamObject is a data structure used in Bril which basically consists of a pair of DatastreamObjectInfo and byte[]. 
 * It is used as a complex type structure by the DatastreamObjectContainer class. This class is the access point to get the 
 * information about the datastream in byte[].
 */
public class DatastreamObject {

	/**
	 * Holds the actual data of the object.
	 */
	private final byte[] data;
	/**
	 * Holds the metadata of the object.
	 */
	private final DatastreamObjectInfo dsoi;

	/**
	 * Constructor for DatastreamObject class. Here the construction of the DatastreamObjectInfo object is created. 
	 * Also the inputstream is read a byte[] holding the actual data of the object. The constructor will create an 
	 * object uuid.using hascode.
	 * 
	 * @param dataStreamType The DataStreamType of the data
	 * @param mimetype The identified MIME type of the data.
	 * @param format The identified format of the data based on the specific content type.
	 * @param data The datastream to be stored in the repository.	
	 */
	public DatastreamObject(DataStreamType dataStreamType, String mimetype, String format,String submitter, byte[] data) {

        DatastreamMimeType dsmt = DatastreamMimeType.getDatastreamMimeType(mimetype);
        /** \todo: fix hashcode generation to ensure uniqueness */
        long id = 0L;
        id += dataStreamType.hashCode();
        id += dsmt.hashCode();
        id += format.hashCode();
        if(data!=null){
        id += data.hashCode();
        }
      //  log.debug( String.format( "id for DatastreamObject = %s", id ) );
        assert( id != 0L );
       
        dsoi = new DatastreamObjectInfo( dataStreamType, dsmt, format, submitter, id );
       
        this.data = data;
       // log.debug( String.format( "length of data: %s", this.data.length ) );
	}
	
	/**
	 * Returns a globally unique identifier for the datastream object .
	 * @returns The id of the datastream object
	 */
	public long getId() {
		return dsoi.getId();
	}

	/**
	 * Returns this DatastreamObject DatastreamObjectInfo's timestamp.
	 * @returns The timestamp of the underlying DatastreamObjectInfo    
	 *  
	 */
	public long getTimestamp() {
		return dsoi.getTimestamp();
	}

	/**
	 * Get the official mimetype of the data associated with the underlying DatastreamObjectInfo.
	 * 
	 * @returns the mimetype of the data as a String.
	 */
	public String getMimetype() {
		return dsoi.getMimetype();
	}

	/**
	 * Gets the format (type of material, e.g DC, RELS-EXT) of the DatastreamObject.
	 * 
	 * @returns the format as string
	 */
	public String getFormat() {
		return dsoi.getFormat();
	}

	/**
	 * Gets the submitter of the DatastreamObject.
	 * 
	 * @returns the format as string
	 */
	public String getSubmitter() {
		return dsoi.getSubmitter();
	}


	/**
	 * Gets the name of the datastream in the {@code DatastreamType}
	 * 
	 * @returns the enum value of the name or ID of the datastream    
	 *  
	 * .
	 */
	public DataStreamType getDataStreamType() {
		return dsoi.getDataStreamType();
	}

	/**
	 * Returns the underlying data (these are the xml elements for DC, object metadata and relationship triples or original data) in the DatastreamObject in byte[].
	 * 
	 * @returns a byte[] containing the actual data of the DatastreamObject.
	 */
	public byte[] getDataBytes() {
		byte[] d=null; 
		if(data!=null){
		  d = data.clone();
		}
		return d;
	}

	/**
	 * Checks if the mimetype of the submitted data is allowed in the DatastreamObject for submission.
	 * 
	 * @param mimetype mimetype to be checked
	 * @returns true if mimetype is allowed otherwise false..
	 * @param mimetype
	 */
	public boolean validMimeType(String mimetype) {
		return dsoi.validMimetype( mimetype );
	}

}