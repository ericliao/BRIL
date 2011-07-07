package uk.ac.kcl.cerch.bril.characteriser;

public enum TaskObjectElement {
	JOB_ID("job_id"),
	TASK_NAME( "task_name"),
	DATE( "date"),
	TITLE( "title"),
	STATUS( "status"),
	SOFTWARE_NAME( "software_name"),
	LOG_FILENAME("log_filename"),
    INPUT_FILENAME( "input_filename"),
    OUTPUT_FILENAME( "output_filename");
	
	private String localname;
	TaskObjectElement( String localName )
    {
        this.localname = localName;
    }
 public String localName()
 {
     return this.localname;
 }
}
