package uk.ac.kcl.cerch.bril.common.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.kcl.cerch.bril.common.metadata.DublinCore;
import uk.ac.kcl.cerch.bril.common.metadata.DublinCoreElement;

/**
 * This a complex class that holds all the different datastreams {@code DatastreamObject} created in Bril. 
 * The datastreams in byte[] are the kind of data that is fraction of elements in the fedora datastream element. 
 * A {@link DatastramContainer} must contain only one of these datastreams: DC , RELS-EXT ( etc defined in emun 
 * {@code DataStreamType}) that corresponds to a digital object.
 */
public class DatastreamObjectContainer {

	private ArrayList<DatastreamObject> datastream;
	private Map<DataStreamType, DublinCore> metadata;
	/**
	 * The constructor that initializes the {@link datastream} representation-- ArrayList object of DatastreamContainer.
	 */
	public DatastreamObjectContainer() {
		datastream = new ArrayList<DatastreamObject>();
	}
	/**
     * Initializes a DatastreamObjectContainer object with an identifier, which would
     * typically be an object identifier for a digital object that this
     * DatastreamObjectContainer will contain
     *
     * @param identifier identifier for this DatastreamObjectContainer
     */
    public DatastreamObjectContainer( String identifier )
    {
    	datastream = new ArrayList<DatastreamObject>();
        metadata = new HashMap<DataStreamType, DublinCore>();
        this.addMetaData( new DublinCore( identifier ) );
     // log.trace( String.format( "Constructing new CargoContainer" ) );
    }
    

    public String getIdentifier()
    {
        return this.getDublinCoreMetaData().getDCValue( DublinCoreElement.ELEMENT_IDENTIFIER );
    }


    public void setIdentifier( String identifier )
    {
        String id = this.getDublinCoreMetaData().getDCValue( DublinCoreElement.ELEMENT_IDENTIFIER );
        if( null != id || !"".equals( id.trim() ) || identifier.trim().equals( id.trim() ) )
        {
           // log.warn( String.format( "Overwriting existing identifier '%s' with new one: '%s'", id, identifier ) );
        }
        this.getDublinCoreMetaData().setIdentifier( identifier );
    }

    /**
     * Adds a metadata element conforming to the {@link MetaData}
     * interface. If this class already contains a {@link MetaData}
     * element with the same identifier, the supplied metadata will
     * overwrite the existing metadata in this {@link DatastreamObjectContainer}
     *
     * @param metadataelement the MetaData element to be added to this DatastreamObjectContainer
     */
    public void addMetaData( DublinCore metadataelement )
    {
        for( Entry<DataStreamType, DublinCore> meta : metadata.entrySet() )
        {
            if( meta.getValue().getClass() == metadataelement.getClass() )
            {
            //    log.warn( String.format( "DatastreamObjectContainer already contains the dublincore element. Will overwrite with metadata type '%s'", metadataelement.getClass() ) );
            }
        }
        metadata.put( metadataelement.getType(), metadataelement );
    }
	/**
	 * 
	 * @param dataStreamType
	 * @param mimetype
	 * @param format
	 * @param submitter
	 * @param data
	 */
	public long addDatastreamObject(DataStreamType dataStreamType, String mimetype, String format, String submitter, byte[] data) {
		if( dataStreamType == null )
        {
           // log.fatal( "dataStreamName cannot be null" );
            throw new IllegalArgumentException( "dataStreamName cannot be null" );
        }
        else if( (mimetype == null) || ( "".equals( mimetype.trim() ) ) )
        {
            //log.fatal( "mimetype must be specified" );
            throw new IllegalArgumentException( "mimetype must be specified" );
        }
        else if( (format == null) || ( "".equals( format.trim() ) ) )
        {
         //   log.fatal( "format must be specified" );
            throw new IllegalArgumentException( "format must be specified" );
        }
       
     /*   else if( (data == null) || (data.length <= 0) )
        {
       //     log.fatal( "data must be present " );
            throw new IllegalArgumentException( "data must be present " );
        }
*/
        else if( (data == null) || (data.length <= 0) ){
        	System.out.println("Data is null !!");
        }
		
		DatastreamObject dso = new DatastreamObject( dataStreamType,
                mimetype,
                format,
                submitter,
                data );

        this.datastream.add( dso );
        //log.debug( String.format( "datastreamObject with id '%s' added to container", dso.getId() ) );
        //log.debug( String.format( "number of DatastreamObjects: %s", getCargoObjectCount() ) );

        return dso.getId();
	}

	/**
	 * Based on the {@link DataStreamType}, the {@link DatastreamObject} matching the type is returned. There must be exactly one {@link DatastreamObject } with the type in the {@link DatastreamObjectContainer}.
	 * 
	 * Please note that this method returns null if no matching DatastreamObjects were found. Use {@link #hasDatastream(DataStreamType)} to check beforehand or check for nulls afterward.
	 * @param objetcType
	 */
	public DatastreamObject getDatastreamObject(uk.ac.kcl.cerch.bril.common.types.DataStreamType objectType) {

		DatastreamObject return_dso = null;
        for( DatastreamObject dso : datastream )
        {
            if( objectType == dso.getDataStreamType() )
            {
            	return_dso = dso;
            }
        }

        if( null == return_dso )
        {
          //  log.warn( String.format( "Could not retrieve DatastreamObject with DataStreamType %s", objectType ) );
          
        }

        return return_dso;
	}

	/**
	 * Returns a List of all the DatastreamObjects that are contained in the {@link DatastreamContainer}. If no DatastreamObjects are found, a null List object is returned.
	 */
	public List<DatastreamObject> getDatastreamObjects() {
		return datastream;
	}

	/**
	 * Given a {@link DataStreamType}, this method returns true if {@link DatastreamObject} with the type was found, false otherwise. 
	 * @param dstype
	 */
	public boolean hasDatastream(DataStreamType dstype) {
	    for( DatastreamObject dso : datastream )
        {
            if( dso.getDataStreamType() == dstype )
            {
                return true;
            }
        }

        return false;
	}

	/**
	 * Get the total number of DatastreamObjects in the {@link DatastreamObjectContainer}.
	 */
	public int getDatastreamObjectCount() {
		  int count = 0;
	        for( DatastreamObject dso : datastream )
	        {
	        	//to check how many of this type
	            //if( type == dso.getDataStreamType() )
	            //{
	                count++;
	            //}
	        }

	        return count;
	}

	  public boolean hasDublinCoreMetadata( DataStreamType type )
	    {
	        for( Entry<DataStreamType, DublinCore> meta : metadata.entrySet() )
	        {
	            if( meta.getKey() == type )
	            {
	                return true;
	            }
	        }
	        return false;
	    }
	  
	  public DublinCore getDublinCoreMetaData()
	    {
	        DublinCore retval = null;
	        for( Entry<DataStreamType, DublinCore> meta : metadata.entrySet() )
	        {
	            if( meta.getKey() == DataStreamType.DublinCore )
	            {
	                retval = (DublinCore) meta.getValue();
	            }
	        }
	        if( retval == null )
	        {
	           // log.warn( "No DublinCore element found in CargoContainer" );
	        }
	        return retval;
	    }
	  
	  public void setOriginalContent(){
		  
	  }

}