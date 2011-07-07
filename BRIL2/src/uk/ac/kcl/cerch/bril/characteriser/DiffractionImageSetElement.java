package uk.ac.kcl.cerch.bril.characteriser;


public enum DiffractionImageSetElement {
	    DIFF_ATTRI_ID("id"),
	    DIFF_TYPE_CRYSTAL_ID( "crystal_id"),
	    DIFF_TYPE_CRYSTAL_SUPPORT( "crystal_support"),
	    DIFF_TYPE_DETAILS( "details"),
	    DIFF_TYPE_AMBIENT_TEMP( "ambient_temp"),
	    DIFF_TYPE_AMBIENT_TEMP_DETAILS( "ambient_temp_details"),
	    DETECTOR_TYPE_DETECTOR( "detector"),
	    DETECTOR_TYPE_TYPE( "type"),
	    DETECTOR_TYPE_COLLECTION_DATE( "collection_date"),
	    MEASUREMENT_DETECTOR_DISTANCE( "detector_distance"),
	    MEASUREMENT_BEAM_CENTRE_X( "beam_centre_X"),
	    MEASUREMENT_BEAM_CENTRE_Y( "beam_centre_Y"),
	    MEASUREMENT_OSC_RANGE( "OSC_range"),
	    SOURCE_WAVELENGTH( "wavelength"),
	    SOURCE_WAVELENGTH_LIST("wavelength_list"),
	    SOURCE_SOURCE( "source"),
	    SOURCE_SYNCHROTRON_SITE("synchrotron_site"),
	    SOURCE_SYNCHROTRON_BEAMLINE("synchrotron_beamline");
	 private String localname;
	 DiffractionImageSetElement( String localName )
	    {
	        this.localname = localName;
	    }
	 
	 public String localName()
	    {
	        return this.localname;
	    }


	    public static boolean hasLocalName( String name )
	    {
	        for( DiffractionImageSetElement dcee : DiffractionImageSetElement.values() )
	        {
	            if ( dcee.localName().equals( name ) )
	            {
	                return true;
	            }
	        }

	        return false;
	    }
	    
}
