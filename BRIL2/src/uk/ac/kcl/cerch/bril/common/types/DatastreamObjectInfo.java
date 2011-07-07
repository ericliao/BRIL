package uk.ac.kcl.cerch.bril.common.types;

import java.util.Date;



public class DatastreamObjectInfo {

	private DataStreamType dataStreamName;
	private DatastreamMimeType mimeType;
	private String format;
	private Date timestamp;
	//private String mimetype;
	private String submitter;
	private long id;

	/**
	 * 
	 * @param dataStreamType
	 * @param mimetype
	 * @param format
	 * @param date
	 * @param id
	 */
	public DatastreamObjectInfo(DataStreamType dataStreamType, DatastreamMimeType mimetype, String format,String submitter,long id) {
		assert( dataStreamType != null && format != null  && mimeType != null  && id != 0 );
        this.dataStreamName = dataStreamType;
        this.format = format;
        this.submitter=submitter;
        this.mimeType = mimetype;    
        this.timestamp = new Date();
        this.id = id;
	}

	public long getId() {
		return id;
	}

	public long getTimestamp() {
		return timestamp.getTime();
	}

	public String getMimetype() {
		return mimeType.getMimeType();
	}

	public String getFormat() {
		return format;
	}
	public String getSubmitter() {
		return submitter;
	}

	public DataStreamType getDataStreamType() {
		return dataStreamName;
	}

	/**
	 * 
	 * @param mimetype
	 */
	public boolean validMimetype(String mimetype) {
		return DatastreamMimeType.validMimetype( mimetype );
	}

}