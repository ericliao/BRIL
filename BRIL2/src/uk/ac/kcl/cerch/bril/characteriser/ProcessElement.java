package uk.ac.kcl.cerch.bril.characteriser;

public enum ProcessElement {
	PROCESS_NAME("process_name"),
	PROCESS_CONTROLLING_USER("process_user"),
	PROCESS_CONTROLLING_SOFTWARE("process_software");
	
	private String localname;
	
	ProcessElement( String localName )
    {
        this.localname = localName;
    }
	
	public String localName()
	{
		return this.localname;
	}
}
