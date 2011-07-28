package uk.ac.kcl.cerch.bril.common.fedora;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import uk.ac.kcl.cerch.bril.characteriser.TaskObjectElement;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraNamespaceContext.FedoraNamespace;
import uk.ac.kcl.cerch.bril.common.types.BrilTransformException;
import uk.ac.kcl.cerch.bril.common.types.DataStreamType;

public class FedoraRelsExt {
	
	private String id;
	private Set<String> relations;
	private Map<QName, QName> triples;
	private Map<Vector<QName>, QName> triplesN;
	public static final DataStreamType type = DataStreamType.RelsExt;
	
	
    private static FedoraNamespace rdf = new FedoraNamespaceContext().getNamespace( "rdf" );
    private static FedoraNamespace rdfs = new FedoraNamespaceContext().getNamespace( "rdfs" );
    private static FedoraNamespace oai_dc = new FedoraNamespaceContext().getNamespace( "oai_dc" );
    private static FedoraNamespace dc = new FedoraNamespaceContext().getNamespace( "dc" );
    private static FedoraNamespace rels = new FedoraNamespaceContext().getNamespace( "rel" );

    private static FedoraNamespace opmv = new FedoraNamespaceContext().getNamespace( "opmv" );
    
    private static FedoraNamespace bril_rels = new FedoraNamespaceContext().getNamespace( "bril_rel" );
    
    public FedoraRelsExt( String id ) throws ParserConfigurationException
    {
        this.id = id;
        relations = new HashSet<String>();
        triples = new HashMap<QName, QName>();
    }
    
    public boolean addRelationship( QName predicate, QName object )
    {
        boolean added = relations.add( new Integer( predicate.hashCode() ).toString()+new Integer( object.hashCode() ).toString() );
        if( added )
        {
        //	System.out.println("Added: "+ object);
        	//There can be two or more same predicate for a subject
        	//The objects are always unique id or literate values for this subject.
         //   triples.put( predicate, object );
            triples.put(object, predicate);
            
        }
        return added;
    }

    public void serialize( OutputStream  out, String identifier ) throws BrilTransformException
    {

    	// Create an output factory
        XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlw;

        try
        {
            xmlw = xmlof.createXMLStreamWriter( out );
//            xmlw.writeStartDocument();
            xmlw.writeStartElement( rdf.getPrefix(), "RDF", rdf.getURI() );

            xmlw.writeNamespace( dc.getPrefix(), dc.getURI() );

            // hack, as fedora rels-ext ns spec does not conform to
            // their own prefix guidelines:
            xmlw.writeNamespace( "fedora", rels.getURI() );

            xmlw.writeNamespace( oai_dc.getPrefix(), oai_dc.getURI() );

            xmlw.writeNamespace( rdf.getPrefix(), rdf.getURI() );

            xmlw.writeNamespace( rdfs.getPrefix(), rdfs.getURI() );
            xmlw.writeNamespace( bril_rels.getPrefix(), bril_rels.getURI() );
            xmlw.writeNamespace( opmv.getPrefix(), opmv.getURI() );

            xmlw.writeStartElement( rdf.getURI(), "Description" );
            xmlw.writeAttribute( rdf.getPrefix(), rdf.getURI(), "about", this.id );
            for( Map.Entry<QName, QName> set : triples.entrySet() )
            {               
                QName key = set.getValue(); //predicate -                
                QName val = set.getKey(); //object 

                String attr_value;
         
               if(val.getPrefix()==""){//its a literal value
                    xmlw.writeStartElement( key.getPrefix(), key.getLocalPart(), key.getNamespaceURI() );                 
                	attr_value = val.getLocalPart();
                	xmlw.writeCharacters(attr_value);
                	xmlw.writeEndElement();  
          
                }else{
                    xmlw.writeEmptyElement( key.getPrefix(), key.getLocalPart(), key.getNamespaceURI() );
                    attr_value = val.getPrefix()+":"+val.getLocalPart();	                    
                    xmlw.writeAttribute(rdf.getPrefix(), rdf.getURI(), "resource", attr_value);                  
                }

               // xmlw.writeAttribute(rdf.getPrefix(), rdf.getURI(), "resource", attr_value);

            }

            xmlw.writeEndElement();//closes "rdf:Description" element
            xmlw.writeEndElement();//closes "rdf:RDF" element
            xmlw.flush();
        }
        catch( XMLStreamException ex )
        {
            String error = String.format( "Could not write to stream writer %s", ex.getMessage() );
            //log.error( error, ex );
            throw new BrilTransformException( error, ex );
        }
    }
    public String getIdentifier()
    {
        return this.id;
    }

    public DataStreamType getType()
    {
        return type;
    }

}
