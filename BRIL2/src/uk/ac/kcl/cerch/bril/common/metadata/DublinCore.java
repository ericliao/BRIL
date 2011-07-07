package uk.ac.kcl.cerch.bril.common.metadata;

import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import uk.ac.kcl.cerch.bril.common.fedora.FedoraNamespaceContext;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraNamespaceContext.FedoraNamespace;
import uk.ac.kcl.cerch.bril.common.types.BrilTransformException;
import uk.ac.kcl.cerch.bril.common.types.DataStreamType;

public class DublinCore {

    private static final SimpleDateFormat  dateFormat = new  SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS" );
    private DateFormat formatter = DateFormat.getDateTimeInstance();
	

    private Map< DublinCoreElement, String > dcvalues;

    public static final DataStreamType type = DataStreamType.DublinCore;
    private static FedoraNamespace dc = new FedoraNamespaceContext().getNamespace( "dc"  );
    private static FedoraNamespace oai_dc = new FedoraNamespaceContext().getNamespace( "oai_dc" );
    
    /**
     * Initializes an empty Dub
     * lin Core element
     */  
    public DublinCore( ) {
        dcvalues = new HashMap< DublinCoreElement, String >();
    }
    
    /**
     * Initializes an empty Dublin Core element , identified by {@code identifier}
     * @param identifier An unambiguous reference to the resource within a given
     * context. Recommended best practice is to use the digital repository object
     * identifier.
     */  
    public DublinCore( String identifier) {
    	dcvalues = new  HashMap< DublinCoreElement, String >();
        dcvalues.put( DublinCoreElement.ELEMENT_IDENTIFIER, identifier);
    }
    
    /**
     * Initializes a Dublin Core element with values taken from {@code
     * inputValues}, identified by {@code identifier}
     * @param identifier An unambiguous reference to the resource within a given
     * context. Recommended best practice is to use the digital repository object
     * identifier.
     */
    public DublinCore( String identifier, Map< DublinCoreElement, String > inputValues )
    {
        dcvalues = new HashMap< DublinCoreElement, String >( inputValues );
        dcvalues.put( DublinCoreElement.ELEMENT_IDENTIFIER, identifier);
    }
    
    public void setContributor(String contributor) {
    	dcvalues.put( DublinCoreElement.ELEMENT_CONTRIBUTOR, contributor );
    }
    
    public void setCoverage(String coverage) {
    	  dcvalues.put( DublinCoreElement.ELEMENT_COVERAGE, coverage );
    }

    public void setCreator(String creator) {
    	dcvalues.put( DublinCoreElement.ELEMENT_CREATOR, creator );
    }
    
    public void setDate(String date, String pattern) {
    	String stringdate="";
    	//"dd MMM yyyy HH:mm:ss"
    	((SimpleDateFormat) formatter).applyPattern(pattern);

		try {	
			 Date d =formatter.parse(date);
		
			date = (new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")).format(d);
			stringdate = dateFormat.format( d );
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        dcvalues.put( DublinCoreElement.ELEMENT_DATE, stringdate );
    }
              
    public void setDescription(String description) {
    	dcvalues.put( DublinCoreElement.ELEMENT_DESCRIPTION, description );
    }
    
    public void setFormat(String format) {
    	dcvalues.put( DublinCoreElement.ELEMENT_FORMAT, format );
    }
    
    public void setIdentifier(String identifier) {
    	dcvalues.put( DublinCoreElement.ELEMENT_IDENTIFIER, identifier );
    }

    public void setLanguage(String language) {
    	  dcvalues.put( DublinCoreElement.ELEMENT_LANGUAGE, language );
    }
    
    public void setPublisher(String publisher) {
    	dcvalues.put( DublinCoreElement.ELEMENT_PUBLISHER, publisher );
    }

    public void setRelation(String relation) {
    	dcvalues.put( DublinCoreElement.ELEMENT_RELATION, relation );
    }

    public void setRights(String rights) {
    	dcvalues.put( DublinCoreElement.ELEMENT_RIGHTS, rights );
    }

    public void setSource(String source) {
    	dcvalues.put( DublinCoreElement.ELEMENT_SOURCE, source );
    }
    
    public void setSubject(String subject) {
    	  dcvalues.put( DublinCoreElement.ELEMENT_SUBJECT, subject );
    }
    

    public void setTitle(String title) {
    	dcvalues.put( DublinCoreElement.ELEMENT_TITLE, title );
    }
    
    public void setType(String type) {
    	  dcvalues.put( DublinCoreElement.ELEMENT_TYPE, type );
    }
	
    /**
     * Retrieves values associated with the {@link DublinCoreElement} {@code
     * dcElement}. If no value was registered with the element, this
     * method will return an empty String.
     *
     * @param dcElement a {@link DublinCoreElement}
     *
     * @return the value associated with {@code dcElement} or an empty String
     */
    public String getDCValue( DublinCoreElement dcElement )
    {
        String retval = dcvalues.get( dcElement );
        if( retval == null )
        {
       //     log.warn( String.format( "No value registered for element %s", dcElement ) );
            retval = "";
        }
       
        return retval;
    }
    
    /**
    *
    * @return the type of this metadata element
    */
   public DataStreamType getType()
   {    
       return type;
   } 
   
   /**
   *
   * @return the identifier of this metadata element
   */
  public String  getIdentifier()
  {
	  return dcvalues.get(DublinCoreElement.ELEMENT_IDENTIFIER);
  } 
    
    public void serialize( OutputStream  out, String identifier )throws BrilTransformException
    {
        // Create an output factory
        XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlw;

        try
        {
        //	xmlw = xmlof.createXMLStreamWriter(System.out);
            xmlw = xmlof.createXMLStreamWriter( out );

            xmlw.setDefaultNamespace( oai_dc.getURI() );

            xmlw.writeStartDocument();
            xmlw.writeStartElement( oai_dc.getURI(), oai_dc.getPrefix()+":"+dc.getPrefix() );
            xmlw.writeNamespace( oai_dc.getPrefix(), oai_dc.getURI() );
            xmlw.writeNamespace( dc.getPrefix(), dc.getURI() );

            for( Entry<DublinCoreElement, String> set : dcvalues.entrySet() )
            {
                xmlw.writeStartElement( dc.getURI(), set.getKey().localName() );
                if ( set.getValue() != null )
                {
                    xmlw.writeCharacters( set.getValue() );
                }

                xmlw.writeEndElement();
            }

          //  xmlw.writeEndElement();//closes "oai_dc:dc" element
            xmlw.writeEndDocument();//closes document
            xmlw.flush();
        }
        catch( XMLStreamException ex )
        {
            String error = "Could not write to stream writer";
          //  log.error( error );
            throw new BrilTransformException( error, ex );
        }
    }

}
