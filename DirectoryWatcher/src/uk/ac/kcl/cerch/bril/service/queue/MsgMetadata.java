package uk.ac.kcl.cerch.bril.service.queue;

/**
 * @author shrijar
 *
 */
public interface MsgMetadata {
	 /**
	 * @param metadata parameters in the queue message in xml format
	 * 
	 * Reads the string and sets the id/path, entrytype, and datatime of a message.
	 */
	 //void setMetadata(String metadata);
	 public String getMetadata();	
	 public String getIdPath();	
	 public String getEntryType();
	 public String getDateTime();
	 public String getChecksum();
	 public String getExperimentType();
}
