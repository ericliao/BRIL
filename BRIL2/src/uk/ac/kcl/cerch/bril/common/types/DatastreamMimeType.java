package uk.ac.kcl.cerch.bril.common.types;

import org.apache.log4j.Logger;



/**
 * Enum class to control the possible values of mimetypes that bril can handle.
 */
public enum DatastreamMimeType {
	TEXT_XML("text/xml","XML Document "),
	APPLICATION_RDF("application/rdf","RDF Document"),
	TEXT_PLAIN("text/plain","Plain Text Document"),
	APPLICATION_OCTET("application/octet-stream","Application specific Binary File"),
	APPLICATION_BZIP2("application/x-bzip2","bzip2 compressed file");

	private final String mimetype;
	private final String description;
	static Logger log = Logger.getLogger( DatastreamMimeType.class );

	/**
	 * 
	 * @param mimetype
	 * @param description
	 */
	DatastreamMimeType(String mimetype, String description) {
		this.mimetype = mimetype;
        this.description = description;
	}

	/**
	 * @return The name of the mimetype. 
	 */
	public String getMimeType() {
		return this.mimetype;
	}

	/**
	 * @return The description of the mimetype
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Checks the validity of the mimetype.
	 * 
	 * @param mimetype The mimetype whose validity is checked.
	 * @return True if the mimetype is valid else false.
	 * @param mimetype
	 */
	public static boolean validMimetype(String mimetype) {
		DatastreamMimeType CMT = DatastreamMimeType.getDatastreamMimeType(mimetype);
        log.trace( "checking mimetype" );

        if( CMT == null )
        {
            return false;
        }

        return true;
	}

	/**
	 * @ mimetype The mimetype to lookup in the DatastreamMimeType
	 * @return DatastreamMimeType of the matched {@code mimetype}. A null value if not matched.
	 * @param mimetype
	 */
	public static DatastreamMimeType getDatastreamMimeType(String mimetype) {
		DatastreamMimeType DSMT = null;
        for( DatastreamMimeType cmt : DatastreamMimeType.values() )
        {
            if( mimetype.equals( cmt.getMimeType() ) )
            {
            	DSMT = cmt;
            }
        }

        return DSMT;
    }

}