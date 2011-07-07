package uk.ac.kcl.cerch.bril.common.fedora;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.validation.Validator;

import org.apache.xml.serialize.OutputFormat;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.fcrepo.utilities.XmlTransformUtility;
import fedora.utilities.NamespaceContextImpl;

import uk.ac.kcl.cerch.bril.common.fedora.FedoraNamespaceContext.FedoraNamespace;

/**
 * This class represents the fedora object XML document (FOXML). 
 * This is implemented based on class Foxml11Document provided by Fedora commons ( package fedora.utilities). 
 * This is implementated to suit our purpose: we specify methods to add DC and RELS-EXT.
 */
public final class FedoraFoxmlDocument {

	public static final String FOXML_NS = FedoraNamespaceContext.FedoraNamespace.FOXML.getURI();;
	public static final String FEDORA_MODEL = FedoraNamespaceContext.FedoraNamespace.FEDORAMODEL.getURI();;
	public static final FedoraNamespace model = FedoraNamespaceContext.FedoraNamespace.FEDORAMODEL;;
	public static final FedoraNamespace view = FedoraNamespaceContext.FedoraNamespace.FEDORAVIEW;;
	public static final String FOXML_VERSION_NS = FOXML_NS + "";
	private DocumentBuilder builder;
	private Document doc;
	private Element rootElement;
	private Element objectProperties;
	private XPathFactory factory;
	private XPath xpath;
	private TransformerFactory xformFactory;
	//private fedoraObject attribute;/**
	
	/*  Helper class for defining properties on the Digital Object
	 */
	
	public enum Property {
		STATE(model.getElementURI( "state" )),
		LABEL(model.getElementURI("label")),
		CONTENT_MODEL(model.getElementURI( "contentModel"  )),
		CREATE_DATE(model.getElementURI( "createdDate"  )),
		OWNERID(model.getElementURI( "ownerId"  )),
		MOD_DATE(view.getElementURI( "lastModifiedDate"  ));

		private final String uri;

		/**
		 * 
		 * @param uri
		 */
		Property(String uri) {
			this.uri=uri;
		}

		public String uri() {
			return this.uri;
		}

	}/**
	 * Helper class for defining the controlgroup of the individual datastreams.
	 * M: Managed Content. Tells the repository to store the datastream's content byte stream inside the repository.
	 * E: External referenced content. Tells the repository to store the URL of the datastream content, not the content bytestream.
	 * R: Redirected Content. Like �E� this tells the repository to store the URL for the datastream content, not the content byte stream itself.  
	 *    Instead, the repository will redirect to the URL at run time.
	 */
	public enum ControlGroup {
		X, M, E, R

	}/**
	 * Helper class for defining the state of the individual datastreams in the Digital Object
	 */
	public enum State {
		A, I, D

	}
	
	/**
     * Helper class to specify the LocationType of a referring String if the
     * {@link ControlGroup} has specified referenced content
     * ({@code FedoraFoxmlDocument.ControlGroup.R})
     */
    public enum LocationType
    {
        /**
         * the referring String denotes a pid in a fedora repository
         */
        INTERNAL_ID,
        /**
         * the referring String denotes an Url
         */
        URL;
    }
	
	

	/**
	 * Creates a skeletal FedoraObject document. Its serialized representation can be genertaed using  {@link FedoraFoxmlDocument#serializeDocument(java.io.OutputStream, java.net.URL)} method.
	 * @param state
	 * @param pid
	 * @param label
	 * @param owner
	 * @param timestamp
	 * @throws ParserConfigurationException 
	 */
	public FedoraFoxmlDocument(State state, String pid, String label, String owner, long timestamp) throws ParserConfigurationException {
		initDocument( pid );
        constructFoxmlProperties( state, label, owner, getTimestamp( timestamp ) );
	}

	/**
	 * 
	 * @param id
	 * @throws ParserConfigurationException 
	 */
	private void initDocument(String id) throws ParserConfigurationException {
		DocumentBuilderFactory  dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware( true );

        builder = dbFactory.newDocumentBuilder();
        DOMImplementation impl = builder.getDOMImplementation();
        doc = impl.createDocument( FOXML_NS, "foxml:digitalObject", null );
        rootElement = doc.getDocumentElement();
        rootElement.setAttributeNS( "http://www.w3.org/2000/xmlns/",
                "xmlns:xsi",
                "http://www.w3.org/1999/XMLSchema-instance" );
        rootElement.setAttributeNS( "http://www.w3.org/1999/XMLSchema-instance",
                "xsi:schemaLocation",
                "info:fedora/fedora-system:def/foxml# http://www.fedora.info/definitions/1/0/foxml1-1.xsd" );
        rootElement.setAttribute( "VERSION", "1.1" );
        rootElement.setAttribute( "PID", id );

        factory = XPathFactory.newInstance();
        xpath = factory.newXPath();
        xformFactory = XmlTransformUtility.getTransformerFactory();

        NamespaceContextImpl nsCtx = new NamespaceContextImpl();
        nsCtx.addNamespace( "foxml", FOXML_NS );


        xpath.setNamespaceContext( nsCtx );
	}

	/**
	 * 
	 * @param state
	 * @param label
	 * @param owner
	 * @param timestamp
	 */
	private void constructFoxmlProperties(State state, String label, String owner, String timestamp) {
		addObjectProperties();
		addObjectProperty( Property.STATE,  state.name());
		addObjectProperty( Property.LABEL,  label);
		addObjectProperty( Property.OWNERID,  owner);
		addObjectProperty( Property.CREATE_DATE,  timestamp);
		
		
	}

	private void addObjectProperties() {
		if( objectProperties == null )
        {
            objectProperties = doc.createElementNS( FOXML_NS, "foxml:objectProperties" );
            rootElement.appendChild( objectProperties );
        }
		//objectProperties = doc.createElementNS( FOXML_NS, "foxml:objectProperties" );
	}

	/**
	 * 
	 * @param name
	 * @param value
	 */
	private void addObjectProperty(Property name, String value) {		
		    addObjectProperties();
	        Element property = doc.createElementNS( FOXML_NS, "foxml:property" );
	        property.setAttribute( "NAME", name.uri );
	        property.setAttribute( "VALUE", value );
	        objectProperties.appendChild( property );
	}

	/**
	 * Adds a Datastream to the DigitalObject Document.  
	 * Please note that this method also handles the construction of the underlying DatastreamVersion
	 * @param id
	 * @param state
	 * @param controlGroup
	 * @param versionable
	 */
	private String addDatastream(String id, State state, ControlGroup controlGroup, boolean versionable) 
	throws XPathExpressionException, IOException
    {
        Element ds = doc.createElementNS( FOXML_NS, "foxml:datastream" );
        ds.setAttribute( "ID", id );
        ds.setAttribute( "STATE", state.toString() );
        ds.setAttribute( "CONTROL_GROUP", controlGroup.toString() );
        ds.setAttribute( "VERSIONABLE", Boolean.toString( versionable ) );
        rootElement.appendChild( ds );

        return id;
    }
	/**
	 * 
	 * @param datastreamId
	 * @param datastreamversionId
	 * @param mimeType
	 * @param label
	 * @param size
	 * @param created
	 */
	private void addDatastreamVersion(String datastreamId, String datastreamversionId, String mimeType, String label, int size, String created)
		throws XPathExpressionException
	    {

	        // if ( dsId.contains( ":" ))
	        // {
	        //     String error = String.format( "Datastream id contains illegal character ':' dsId == '%s'", dsId );
	        //     throw new IllegalArgumentException( error );
	        // }

	        String expr = String.format( "//foxml:datastream[@ID='%s']", datastreamId );
	        NodeList nodes = (NodeList) xpath.evaluate( expr, doc, XPathConstants.NODESET );
	        Node node = nodes.item( 0 );
	        if( node == null )
	        {
	            throw new IllegalArgumentException( datastreamId + "does not exist." );
	        }

	        if( datastreamversionId == null || datastreamversionId.equals( "" ) )
	        {
	        	datastreamversionId = datastreamId + ".0";
	        }

	        Element dsv = doc.createElementNS( FOXML_NS, "foxml:datastreamVersion" );
	        dsv.setAttribute( "ID", datastreamversionId );
	        dsv.setAttribute( "MIMETYPE", mimeType );
	        dsv.setAttribute( "LABEL", label );
	        if( size != 0 )
	        {
	            dsv.setAttribute( "SIZE", Integer.toString( size ) );
	        }
	        //dsv.setAttribute( "CREATED", created );
	        node.appendChild( dsv );
	    }


	/**
	 * Adds a dublincore xml document (in the form of a string) to the Digital Object. 
	 * No checks regarding the validity and  well-formedness  of the dublin core xml String is done here. 
	 * A SAXException will be thrown when trying to add non-well-formed xml to the Digital Object xml document.
	 *  
	 * @param dcData
	 * @param timenow
	 */
	public void addDublinCoreDatastream(String dcData, long timenow)throws XPathExpressionException, SAXException, IOException
    {
        String label = "Dublin Core data";
        String id = "DC";
        this.addXmlContent( id, dcData, label, timenow, true );
    }

	/**
	 *  Adds rels-ext triples in the form of a string to the Digital Object. 
	 *  No checks regarding the validity and  well-formedness  of the xml String is done here. 
	 *  A SAXException will be thrown when  trying to add non-well-formed xml to the Digital Object xml document.    
	 * @param relsextData
	 * @param timenow
	 */
	public void addRelsExtDatastream(String relsextData, long timenow) throws XPathExpressionException, IOException, SAXException  {
        String label="Relationships";
        String id = "RELS-EXT";
        String format_URI="info:fedora/fedora-system:FedoraRELSExt-1.0";
       
        String datastreamId = id;
        String xmlContent = relsextData;
        boolean versionable = false;
       

        String dsId = addDatastream( datastreamId, State.A, ControlGroup.X, versionable );
        String dsvId = dsId + ".0";

       // log.debug( String.format( "addXmlContent: %s, %s, %s, %s", datastreamId, xmlContent, label, dsId ) );

        addDatastreamVersion( dsId, dsvId, "application/rdf+xml", label, xmlContent.length(), getTimestamp( timenow ) );
        Document contentDoc = builder.parse( new InputSource( new StringReader( xmlContent ) ) );
        try {
			System.out.println(documentToString(contentDoc));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Node importedContent = doc.adoptNode( contentDoc.getDocumentElement() );   
        Node dsv = getDatastreamVersion( dsvId );
        Element content = doc.createElementNS( FOXML_NS, "foxml:xmlContent" );
        dsv.appendChild( content );
        content.appendChild( importedContent );

       
    }


	/**
	 * Constructs a datastream and a datastreamversion in the Digital Object. 
	 * The mimetype of the datastream is set to "text/xml" no matter what mimetype the delivered {@code xmlContent} has.  
	 * @param datastreamId
	 * @param xmlContent
	 * @param label
	 * @param timenow
	 * @param versionable
	 */
	public void addXmlContent(String datastreamId, String xmlContent, String label, long timenow, boolean versionable) throws SAXException, IOException, XPathExpressionException
    {
        String dsId = addDatastream( datastreamId, State.A, ControlGroup.X, versionable );
        String dsvId = dsId + ".0";

   //     log.debug( String.format( "addXmlContent: %s, %s, %s, %s", datastreamId, xmlContent, label, dsId ) );

        addDatastreamVersion( dsId, dsvId, "text/xml", label, xmlContent.length(), getTimestamp( timenow ) );
        Document contentDoc = builder.parse( new InputSource( new StringReader( xmlContent ) ) );
        OutputFormat format = new OutputFormat(contentDoc);
        format.setOmitXMLDeclaration(true);
        try {
			System.out.println(documentToString(contentDoc));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Node importedContent = doc.adoptNode( contentDoc.getDocumentElement() );
        Node dsv = getDatastreamVersion( dsvId );
        Element content = doc.createElementNS( FOXML_NS, "foxml:xmlContent" );
        dsv.appendChild( content );
        content.appendChild( importedContent );
    }
	
	/**
	 * A method to add data to the repository object.
	 * 
	 * @param datastreamId datastream id to be created in the object.
	 * @param content a byte[] with data
	 * @param label name given to the content
	 * @param mimetype 
	 * @param timenow time in long
	 * @throws XPathExpressionException
	 * @throws IOException
	 */
	public void addBinaryContent(String datastreamId, byte[] content, String label, String mimetype, long timenow) throws XPathExpressionException, IOException{
		 String dsId = addDatastream( datastreamId, State.A, ControlGroup.M, true );
	        String dsvId = dsId + ".0";	
	        addDatastreamVersion( dsId, dsvId, mimetype, label, 0, getTimestamp( timenow ) );
	        String b = fedora.utilities.Base64.encodeToString(content);
	        Node dsv =  getDatastreamVersion(dsvId);
	        Element binaryElement = doc.createElementNS(FOXML_NS, "foxml:binaryContent");
	        dsv.appendChild(binaryElement);
	        binaryElement.setTextContent(b);
	}



	/**
	 * Add a content location as a datastream on the Digital Object. 
	 * The content can be either stored internally in the fedora r
	 * epository, in which case type should be set to {@link INTERNAL_REF},
	 *  or externally stored in which case type should be set to {@link URL}. 
	 *  This method performs no checks on whether the content referred by 
	 *  ref actually exists or is reachable, nor does it perform any checks on the uri-scheme of ref.       
	 *
	 * @param datastreamId
	 * @param ref
	 * @param label
	 * @param mimetype
	 * @param type
	 * @param timenow
	 */
	public void addContentLocation(String datastreamId, String ref, String label, String mimetype, LocationType type, long timenow) throws XPathExpressionException, SAXException, IOException
    {
        String dsId = addDatastream( datastreamId, State.A, ControlGroup.M, true );
        String dsvId = dsId + ".0";
        addDatastreamVersion( dsId, dsvId, mimetype, label, 0, getTimestamp( timenow ) );
        String expr = String.format( "//foxml:datastreamVersion[@ID='%s']/foxml:contentLocation", datastreamId );

        NodeList nodes = (NodeList) xpath.evaluate( expr, doc, XPathConstants.NODESET );
        Element location = (Element) nodes.item( 0 );
        if( location == null )
        {
            location = setContentLocationElement( dsvId );
        }

        location.setAttribute( "REF", ref );
        location.setAttribute( "TYPE", type.name() );
	}

	/**
	 * 
	 * @param dsvId
	 */
	private Element setContentLocationElement(String dsvId) {
		Node node = getDatastreamVersion( dsvId );
        Element location = doc.createElementNS( FOXML_NS, "foxml:contentLocation" );
        node.appendChild( location );

        return location;
	}

	/**
	 * 
	 * @param dsvId
	 */
	private Node getDatastreamVersion(String dsvId) {
		  String expr = String.format( "//foxml:datastreamVersion[@ID='%s']", dsvId );

	        try
	        {
	            NodeList nodes = (NodeList) xpath.evaluate( expr, doc, XPathConstants.NODESET );
	            Node node = nodes.item( 0 );
	            if( node == null )
	            {
	                throw new IllegalArgumentException( String.format( "%s does not exist.", dsvId ) );
	            }

	            return node;
	        }
	        catch( XPathExpressionException e )
	        {
	            throw new IllegalArgumentException( String.format( "%s does not exist.", dsvId ) );
	        }
	}

	/**
	 * Serializes the Foxml Document into a foxml 1.1 string representation that is written to the OutputStream.
	 * @param out
	 * @param schemaurl
	 */
	public void serializeDocument(OutputStream out, URL schemaurl) throws TransformerConfigurationException, TransformerException, SAXException, IOException
    {
        Transformer idTransform;
        idTransform = xformFactory.newTransformer();
        Source input = new DOMSource( doc );
        if( schemaurl != null )
        {
            SchemaFactory schemaf = javax.xml.validation.SchemaFactory.newInstance( FOXML_NS );
            Schema schema = schemaf.newSchema( schemaurl );
            Validator validator = schema.newValidator();
            validator.validate( input );
        }

        Result output = new StreamResult( out );
        idTransform.transform( input, output );
    }
	
	  /**
     * gets a string representation of a timestamp. If 0l is given as an
     * argument, a timestamp constructed from System.currentTimeMillis is
     * returned
     */
    private String getTimestamp( long time )
    {
        if( time == 0 )
        {
            time = System.currentTimeMillis();
        }

        return new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS" ).format( new Date( time ) );
    }
private Document setXMLDeclaration(Document document){
	OutputFormat format = new OutputFormat(document);
	//format.setIndenting(true);
	format.setOmitXMLDeclaration(true);
	return document;
}

private String documentToString(Document document) throws Exception{
	TransformerFactory factory = TransformerFactory.newInstance();
	Transformer transformer = factory.newTransformer();
	StringWriter writer = new StringWriter();
	Result result = new StreamResult(writer);
	Source source = new DOMSource(document);
	transformer.transform(source, result);
	writer.close();
	String xml = writer.toString();
	return xml;

}
	
	


}