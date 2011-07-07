package uk.ac.kcl.cerch.bril.characteriser;

public enum DiffractionImageElement {
	DIFF_IMG_ID("img_id"),
	DIFF_PIXEL_SIZE( "pixel_size"),
    DIFF_DATE_TIME( "date_time"),
    DIFF_OSC_START( "osc_start"),
    DIFF_IMAGE_SIZE( "image_size"),
    DIFF_EXPOSER_TIME( "exposer_time"),
    DIFF_TWO_THETA_VALUE( "two_theta_value");
	
	private String localname;
 
	DiffractionImageElement( String localName )
    {
        this.localname = localName;
    }
 
	public String localName()
	{
		return this.localname;
	}


	public static boolean hasLocalName( String name )
	{
	     for( DiffractionImageElement dcee : DiffractionImageElement.values() )
	     {
	         if ( dcee.localName().equals( name ) )
	         {
	             return true;
	         }
	     }
	
	     return false;
	}
 
	public static DiffractionImageElement fromString(  String localName )
	{
	     if ( DiffractionImageElement.hasLocalName( localName ) )
	     {
	         return DiffractionImageElement.valueOf( "DIFF_" + localName.toUpperCase() );
	     }
	
	     throw new IllegalArgumentException( String.format( "No enum value %s", "ELEMENT_" + localName.toUpperCase() ) );
	}  
}
