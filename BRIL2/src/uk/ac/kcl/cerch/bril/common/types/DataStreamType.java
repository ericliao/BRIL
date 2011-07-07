package uk.ac.kcl.cerch.bril.common.types;

/**
 * An Enum class.
 * OriginalData, DublinCoreData, RelsExt, ObjectMetadata and PremisMetadata        
 */
public enum DataStreamType {
	OriginalData("originalData","original data"),
	DublinCore("dublinCore","dublincore data"),
	RelsExt("relsExt","relationship expressions"),
	ObjectMetadata("objectMetadata","object specific metadata"),
	PremisMetadata("premisMetadata","premis metadata");

	private String name;
	private String description;

	/**
	 * 
	 * @param name
	 * @param description
	 */
	DataStreamType(String name, String description) {
		this.name = name;
        this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	/**
	 * 
	 * @param nameType
	 */
	public static boolean validDataStreamType(String nameType) {
		DataStreamType DSName = DataStreamType.getDataStreamTypeFrom(nameType);
	       
        if( DSName == null )
        {
                return false;
        }
       
        return true;
	}

	/**
	 * 
	 * @param name
	 */
	public static DataStreamType getDataStreamTypeFrom(String name) {
		DataStreamType DSName = null;
        for (DataStreamType dsn : DataStreamType.values() )
        {
            
            if( name.toLowerCase().equals( dsn.getName().toLowerCase() ) )
            {
                DSName = dsn;
            }
        }
       
        return DSName;
	}

}