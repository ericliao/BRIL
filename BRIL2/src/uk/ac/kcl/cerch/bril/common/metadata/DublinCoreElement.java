package uk.ac.kcl.cerch.bril.common.metadata;

public enum DublinCoreElement
{
    ELEMENT_TITLE( "title"),
    ELEMENT_CREATOR( "creator"),
    ELEMENT_SUBJECT( "subject"),
    ELEMENT_DESCRIPTION( "description"),
    ELEMENT_PUBLISHER( "publisher"),
    ELEMENT_CONTRIBUTOR( "contributor"),
    ELEMENT_DATE( "date"),
    ELEMENT_TYPE( "type"),
    ELEMENT_FORMAT( "format"),
    ELEMENT_IDENTIFIER( "identifier"),
    ELEMENT_SOURCE( "source"),
    ELEMENT_LANGUAGE( "language"),
    ELEMENT_RELATION( "relation"),
    ELEMENT_COVERAGE( "coverage"),
    ELEMENT_RIGHTS( "rights");
    
    private String localname;
    DublinCoreElement( String localName )
    {
        this.localname = localName;
    }


    public String localName()
    {
        return this.localname;
    }


    public static boolean hasLocalName( String name )
    {
        for( DublinCoreElement dcee : DublinCoreElement.values() )
        {
            if ( dcee.localName().equals( name ) )
            {
                return true;
            }
        }

        return false;
    }
    
    public static DublinCoreElement fromString(  String localName )
    {
        if ( DublinCoreElement.hasLocalName( localName ) )
        {
            return DublinCoreElement.valueOf( "ELEMENT_" + localName.toUpperCase() );
        }

        throw new IllegalArgumentException( String.format( "No enum value %s", "ELEMENT_" + localName.toUpperCase() ) );
    }
}
