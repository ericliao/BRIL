package uk.ac.kcl.cerch.bril.common.fedora;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;
import javax.xml.parsers.ParserConfigurationException;
/*import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;*/
import javax.xml.transform.TransformerFactory;
/*import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;*/
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
//import javax.xml.xpath.XPathFactory;

//import fedora.utilities.Base64;
//import fedora.utilities.NamespaceContextImpl;
import org.fcrepo.utilities.XmlTransformUtility;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Shri
 * 
 */
public final class FedoraMetsDocument {

	public static final String METS_NS = FedoraNamespaceContext.FedoraNamespace.METS
			.getURI();
	public static final String FEDORA_MODEL = FedoraNamespaceContext.FedoraNamespace.FEDORAMODEL
			.getURI();
	// public static final FedoraNamespace model =
	// FedoraNamespaceContext.FedoraNamespace.FEDORAMODEL;
	// public static final FedoraNamespace view =
	// FedoraNamespaceContext.FedoraNamespace.FEDORAVIEW;
	public static final String METS_VERSION_NS = METS_NS + "";

	private DocumentBuilder builder;
	private Document doc;
	private Element rootElement;
	private Element metsHdrElement;
	private Element fileSec;
	private XPathFactory factory;
	private XPath xpath;
	private TransformerFactory xformFactory;

	public FedoraMetsDocument(String state, String pid, String label,
			String owner, long timestamp) throws ParserConfigurationException {
		/**
		 * \todo: a fedora document v1.1 pid must conform to the following rules
		 * a maximum length of 64 chars must satisfy the pattern
		 * "([A-Za-z0-9]|-|\.)+:(([A-Za-z0-9])|-|\.|~|_|(%[0-9A-F]{2}))+"
		 */
		initDocument(pid);
		// constructMetsAttributeProperties( state, label, owner, getTimestamp(
		// timestamp ) );
	}

	private void initDocument(String id) throws ParserConfigurationException {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setNamespaceAware(true);

		builder = dbFactory.newDocumentBuilder();
		DOMImplementation impl = builder.getDOMImplementation();
		doc = impl.createDocument(METS_NS, "METS:mets", null);
		rootElement = doc.getDocumentElement();
		rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/",
				"xmlns:xsi", "http://www.w3.org/1999/XMLSchema-instance");
		rootElement
				.setAttributeNS(
						"http://www.w3.org/1999/XMLSchema-instance",
						"xsi:schemaLocation",
						"http://www.loc.gov/METS/ http://www.fedora.info/definitions/1/0/mets-fedora-ext1-1.xsd");
		rootElement.setAttribute("EXT_VERSION", "1.1");
		rootElement.setAttribute("PID", id);

		factory = XPathFactory.newInstance();
		xpath = factory.newXPath();
		xformFactory = XmlTransformUtility.getTransformerFactory();

		/*
		 * NamespaceContextImpl nsCtx = new NamespaceContextImpl();
		 * nsCtx.addNamespace("METS", METS_NS);
		 * 
		 * xpath.setNamespaceContext(nsCtx);
		 */

	}

	private void contruct_metsHdr(String state) {
		metsHdrElement = doc.createElementNS(METS_NS, "METS:metsHdr");
		rootElement.appendChild(metsHdrElement);
		rootElement.setAttribute("RECORDSTATUS", state);

	}

	/**
	 * @param state
	 * @param label
	 *            label for the digital object
	 * @param owner
	 *            this can be null
	 * @param timestamp
	 *            this can be null
	 */
	private void constructMetsAttributes(String state, String label,
			String owner, String timestamp) {
		contruct_metsHdr(state);
		rootElement.setAttribute("LABEL", label);

	}

	/**
	 * Adds a amdSec element ( i.e., Datastream) to the DigitalObject Document
	 * 
	 * 
	 * @param id
	 *            identify the type of datastream DC or RELS-EXT
	 * @param state
	 * @param controlGroup
	 * @param versionable
	 */
	private String addDatastream_amdSec(String id, String state,
			String controlGroup, boolean versionable)
			throws XPathExpressionException, IOException {
		Element amdSec = doc.createElementNS(METS_NS, "METS:amdSec");
		amdSec.setAttribute("ID", id);
		amdSec.setAttribute("STATE", state.toString());
		amdSec.setAttribute("CONTROL_GROUP", controlGroup.toString());
		amdSec.setAttribute("VERSIONABLE", Boolean.toString(versionable));
		rootElement.appendChild(amdSec);
		return id;
	}

	private String addDatastream_fileSec(String id, String state, boolean versionable)throws SAXException,
			IOException, XPathExpressionException {
		/*
		 * <METS:fileSec> 
		 * <METS:fileGrp ID="DATASTREAMS">
		 * 		<METS:fileGrp ID="IMAGE" STATUS="A">
		 * 		</METS:fileGrp>
		 * </METS:fileGrp>
		 * </METS:fileSec> 
		 */

		Element fileSec = doc.createElementNS(METS_NS, "METS:fileSec");
		Element fileGrp = doc.createElementNS(METS_NS, "METS:fileGrp");
		fileGrp.setAttribute("ID", "DATASTREAMS");
		Element fileGrpFile = doc.createElementNS(METS_NS, "METS:fileGrp");
		fileGrpFile.setAttribute("ID", id);
		fileGrpFile.setAttribute("STATE", state);
		//fileGrp.setAttribute("CONTROL_GROUP", controlGroup.toString());
		fileGrpFile.setAttribute("VERSIONABLE", Boolean.toString(versionable));
		
		rootElement.appendChild(fileSec);
		fileSec.appendChild(fileGrp);
		fileGrp.appendChild(fileGrpFile);

		return id;
	}

	public void addFile(String datastreamId, String xmlContent, String mimetype, String label,
			long timenow, boolean versionable) throws SAXException,
			IOException, XPathExpressionException {
		String dsId = addDatastream_fileSec(datastreamId, "A", versionable);
		String dsvId = dsId + ".0";
		addDatastreamVersion_fileGrp(dsId, dsvId, mimetype, label, "");
		Document contentDoc = builder.parse(new InputSource(new StringReader(
				xmlContent)));
		Node importedContent = doc.adoptNode(contentDoc.getDocumentElement());
		Node techMD = getDatastreamVersion_techMD(dsvId);
		Element content = doc.createElementNS(METS_NS, "METS:xmlData");
		techMD.appendChild(content);
		content.appendChild(importedContent);

	}

	private void addDatastreamVersion_fileGrp(String dsId, String dsvId, String mimeType,
			String label, String owner) throws XPathExpressionException {
			String expr = String.format("//METS:fileGrp[@ID='%s']", dsId);
			NodeList nodes = (NodeList) xpath.evaluate(expr, doc,
					XPathConstants.NODESET);
			Node node = nodes.item(0);
			if (node == null) {
				throw new IllegalArgumentException(dsId + "does not exist.");
			}

			if (dsvId == null || dsvId.equals("")) {
				dsvId = dsId + ".0";
			}
/*	<METS:file ID="IMAGE.0" MIMETYPE="image/x-mrsid-image" OWNERID="E">
					
				</METS:file>*/
			Element file = doc.createElementNS(METS_NS, "METS:file");
			file.setAttribute("ID", dsvId);
			file.setAttribute("MIMETYPE", mimeType);
			file.setAttribute("OWNERID", owner);
			
			node.appendChild(file);
	}
	
	public void addContentLocation(String datastreamId, String ref, String label, String mimetype, String locationType, long timenow ) 
	throws  XPathExpressionException, SAXException, IOException
    {
		//String dsId = addDatastream( datastreamId, "A", ControlGroup.E, true );
		String dsId = addDatastream_fileSec(datastreamId, "A", false);
        String dsvId = dsId + ".0";
       // addDatastreamVersion( dsId, dsvId, mimetype, label, 0, getTimestamp( timenow ) );
        addDatastreamVersion_fileGrp(dsId, dsvId, mimetype, label, "");
        String expr = String.format( "//METS:file[@ID='%s']", dsvId );
/*
  <METS:fileGrp ID="IMAGE" STATUS="A">
  <METS:file ID="IMAGE.0" MIMETYPE="image/x-mrsid-image" OWNERID="E">
					<METS:FLocat LOCTYPE="URL" 
						xlink:href="http://iris.lib.virginia.edu/mrsid/mrsid_images/iva/archerp01.sid" 
						xlink:title="Image of Pavilion III, University of Virginia"/>
  </METS:file>
   </METS:fileGrp>
*/
        NodeList nodes = (NodeList) xpath.evaluate( expr, doc, XPathConstants.NODESET );
        Element location = (Element) nodes.item( 0 );
        
        
        if( location == null )
        {
            location = setFlocatElement( dsvId );
        }

        location.setAttribute( "LOCTYPE", locationType );
        location.setAttribute( "xlink:href", ref );
        location.setAttribute( "xlink:title", label );
	}
	

    private Element setFlocatElement( String dsvId )
    {
        Node node = getDatastreamVersion_file( dsvId );
        Element location = doc.createElementNS( METS_NS, "METS:FLocat" );
        node.appendChild( location );

        return location;
    }

	

	public void addDublinCoreDatastream(String dcdata, long timenow)
			throws XPathExpressionException, SAXException, IOException {
		String label = "Dublin Core data";
		String id = "DC";
		this.addXmlData(id, dcdata, label, timenow, true);
	}

	public void addXmlData(String datastreamId, String xmlContent,
			String label, long timenow, boolean versionable)
			throws SAXException, IOException, XPathExpressionException {
		String dsId = addDatastream_amdSec(datastreamId, "A", "X", versionable);
		String dsvId = dsId + ".0";
		addDatastreamVersion_techMD(dsId, dsvId, "text/xml", label, xmlContent
				.length(), "");
		Document contentDoc = builder.parse(new InputSource(new StringReader(
				xmlContent)));
		Node importedContent = doc.adoptNode(contentDoc.getDocumentElement());
		Node techMD = getDatastreamVersion_techMD(dsvId);
		Element content = doc.createElementNS(METS_NS, "METS:xmlData");
		techMD.appendChild(content);
		content.appendChild(importedContent);
	}

	private void addDatastreamVersion_techMD(String dsId, String dsvId,
			String mimeType, String label, int size, String created)
			throws XPathExpressionException {
		String expr = String.format("//METS:amdSec[@ID='%s']", dsId);
		NodeList nodes = (NodeList) xpath.evaluate(expr, doc,
				XPathConstants.NODESET);
		Node node = nodes.item(0);
		if (node == null) {
			throw new IllegalArgumentException(dsId + "does not exist.");
		}

		if (dsvId == null || dsvId.equals("")) {
			dsvId = dsId + ".0";
		}

		Element techMD = doc.createElementNS(METS_NS, "METS:techMD");
		techMD.setAttribute("ID", dsvId);

		if (size != 0) {
			techMD.setAttribute("SIZE", Integer.toString(size));
		}
		// techMD.setAttribute("CREATED", created);
		node.appendChild(techMD);

		Element mdWrap = doc.createElementNS(METS_NS, "METS:mdWrap");
		mdWrap.setAttribute("MIMETYPE", mimeType);
		mdWrap.setAttribute("LABEL", label);
		techMD.appendChild(mdWrap);

	}

	private void addDatastreamVersion_fileSec(String dsId, String dsvId,
			String mimeType, String label, int size, String created)
			throws XPathExpressionException {
		String expr = String.format("//METS:fileGrp[@ID='%s']", dsId);
		NodeList nodes = (NodeList) xpath.evaluate(expr, doc,
				XPathConstants.NODESET);
		Node node = nodes.item(0);
		if (node == null) {
			throw new IllegalArgumentException(dsId + "does not exist.");
		}

		if (dsvId == null || dsvId.equals("")) {
			dsvId = dsId + ".0";
		}
	}

	private Object getTimestamp(long timenow) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Get datastreamversion in techMD element identified by dsvId
	 */
	private Node getDatastreamVersion_techMD(String dsvId) {
		String expr = String.format("//METS:techMD[@ID='%s']", dsvId);

		try {
			NodeList nodes = (NodeList) xpath.evaluate(expr, doc,
					XPathConstants.NODESET);
			Node node = nodes.item(0);
			if (node == null) {
				throw new IllegalArgumentException(String.format(
						"%s does not exist.", dsvId));
			}

			return node;
		} catch (XPathExpressionException e) {
			throw new IllegalArgumentException(String.format(
					"%s does not exist.", dsvId));
		}
	}
	
	private Node getDatastreamVersion_file(String dsvId){
		String expr = String.format("//METS:file[@ID='%s']", dsvId);

		try {
			NodeList nodes = (NodeList) xpath.evaluate(expr, doc,
					XPathConstants.NODESET);
			Node node = nodes.item(0);
			if (node == null) {
				throw new IllegalArgumentException(String.format(
						"%s does not exist.", dsvId));
			}

			return node;
		} catch (XPathExpressionException e) {
			throw new IllegalArgumentException(String.format(
					"%s does not exist.", dsvId));
		}
	}

}
