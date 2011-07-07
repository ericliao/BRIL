package uk.ac.kcl.cerch.bril.common.types;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
/*import javax.xml.rpc.ServiceException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.configuration.ConfigurationException;
import org.xml.sax.SAXException;*/

import uk.ac.kcl.cerch.bril.common.fedora.BrilObjectRepositoryException;
//import uk.ac.kcl.cerch.bril.common.fedora.FedoraAdminstrationImpl;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraHandler;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraRelsExt;
//import uk.ac.kcl.cerch.bril.common.fedora.FedoraUtils;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraNamespaceContext.FedoraNamespace;
import uk.ac.kcl.cerch.bril.common.util.FileUtil;
import uk.ac.kcl.cerch.bril.common.metadata.DublinCore;
//import uk.ac.kcl.cerch.bril.common.metadata.DublinCoreElement;

public class TestClass {

	public static void main(String arg[]) throws ParserConfigurationException,
			BrilTransformException {
		File f = new File("C:\\brilstore\\00EXPT123\\1YY8b.pdb");
		File f1 = new File("c:\\Experiment\\RELS_EXT.txt");
		byte[] data = null;
		String id = "bril:test500";
		// byte[] relsextData = null;
		try {
			byte[] dataff = FileUtil.getBytesFromFile(f);
			// relsextData =FileUtil.getBytesFromFile(f1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// putInATmpFile(data);
		/*
		 * upload file
		 */
		FedoraHandler fedoraHandler = null;
		String url = null;
		try {
			fedoraHandler = new FedoraHandler();
			try {
				url = fedoraHandler.getFC().uploadFile(f);
				data = url.getBytes();
				System.out.println("internalid: " + url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (BrilObjectRepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		/*
		 * DatastreamObject dso = new
		 * DatastreamObject(DataStreamType.DublinCore,
		 * "text/xml","metadata","stella",data);
		 * System.out.println("Valid mimetype? "+
		 * dso.validMimeType("text/xml")); System.out.println("id "+
		 * dso.getId());
		 */

		System.out.println("Create DublinCore object---");

		DublinCore dc = new DublinCore(id);
		dc.setDescription("this is test ingest");
		// 2008-08-11 HH:mm:ss
		// 1213691343
		dc.setDate("17 Jun 2008 09:29:03", "dd MMM yyyy HH:mm:ss");
		dc.setTitle("Diffraction image");
		dc.setCreator("Stella Fabiane");
		dc.setSubject("img");
		System.out.println("DublinCore created---");

		System.out.println("Create FedoraRelsExt object---");
		FedoraRelsExt relsExt = new FedoraRelsExt(FedoraNamespace.FEDORA
				.getURI()
				+ id);
		// System.out.println(FedoraNamespace.FEDORA.getURI());
		QName predicate = new QName(FedoraNamespace.FEDORARELSEXT.getURI(),
				"isPartOf", FedoraNamespace.FEDORA.getPrefix());
		QName object = new QName("", "111", FedoraNamespace.FEDORA.getURI()
				+ FedoraNamespace.BRIL.getPrefix());
		// QName(String namespaceURI, String localPart, String prefix)
		QName predicate1 = new QName(FedoraNamespace.BRILRELS.getURI(),
				"wasDerivedFrom", FedoraNamespace.BRILRELS.getPrefix());
		QName object1 = new QName("", "112", FedoraNamespace.FEDORA.getURI()
				+ FedoraNamespace.BRIL.getPrefix());

		relsExt.addRelationship(predicate, object);
		relsExt.addRelationship(predicate1, object1);
		ByteArrayOutputStream relsExt_baos = new ByteArrayOutputStream();
		relsExt.serialize(relsExt_baos, "");
		System.out.println("Relationship (relsext) created---");

	/*	DatastreamObjectContainer dsc = new DatastreamObjectContainer(dc
				.getDCValue(DublinCoreElement.ELEMENT_IDENTIFIER));
		dsc.addMetaData(dc);
		// DataStreamType dataStreamType, String mimetype, String format, String
		// submitter, byte[] data
		// dsc.addDatastreamObject(DataStreamType.DublinCore,
		// "text/xml","dc","bril",data);
		dsc.addDatastreamObject(DataStreamType.RelsExt,
				DatastreamMimeType.APPLICATION_RDF.getMimeType(),
				"relationship", "bril", relsExt_baos.toByteArray());
		dsc.addDatastreamObject(DataStreamType.OriginalData,
				DatastreamMimeType.APPLICATION_OCTET.getMimeType(),
				"DiffractionImage", "bril", data);
		// System.out.println(String.format( "Original data:%s", "IMAGE"));
		System.out.println("DatastreamObjectContainer created---");
*/
		/*
		 * FedoraUtils fu = new FedoraUtils(); try { byte[] foxmlByte =
		 * fu.DataStreamObjectToFoxml(dsc); String foxmlString = new String
		 * (foxmlByte); FedoraAdminstrationImpl fedoraAdmin = new
		 * FedoraAdminstrationImpl(); fedoraAdmin.storeObject(dsc);
		 * 
		 * 
		 * //wecan save the string as xml in the same name as the original file
		 * name
		 * System.out.println("-----------------foxml document--------------");
		 * 
		 * System.out.println(foxmlString); } catch (BrilTransformException e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); } catch
		 * (BrilObjectRepositoryException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); }
		 */
		try {
			runXMLCreation();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String value = " 206.000000 -> 207.000000 deg";
		int dash = value.lastIndexOf('-');
		int greaterthen = value.lastIndexOf('>');
		int deg = value.lastIndexOf("deg");
		String range1 = value.substring(0, dash-1);
		String range2 = value.substring(greaterthen+1,deg-1);
		float deg1= Float.valueOf(range1).floatValue();
		float deg2= Float.valueOf(range2).floatValue();
		float range = deg2-deg1; 
		System.out.println(range1);
		System.out.println(range2);
		System.out.println(range);
		String value1 = "(157.354004 mm,157.591003 mm)";
		int openbrac = value1.lastIndexOf('(');
		int cloasedbrac = value1.lastIndexOf(')');
		int comma = value1.lastIndexOf(',');
		String x = value1.substring(openbrac+1, comma);
		String y = value1.substring(comma+1, cloasedbrac);
		System.out.println(x);
		System.out.println(y);
		/*
		 * try { fedoraHandler.addDatastream(id, "DiffImage", new String[]
		 * {"0"}, "my data",
		 * true,DatastreamMimeType.APPLICATION_OCTET.getMimeType() , null, url,
		 * "M", "A", null, null, "added me"); } catch (ConfigurationException e)
		 * { // TODO Auto-generated catch block e.printStackTrace(); } catch
		 * (MalformedURLException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (ServiceException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } catch (IOException
		 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 */
	}

	public static void runXMLCreation() throws Exception {
        // Create XMLOutputFactory
        XMLOutputFactory factory = XMLOutputFactory.newInstance();

        // Create XMLStreamWriter to System.out
        XMLStreamWriter writer =
                factory.createXMLStreamWriter(System.out);

        // Write the start document (XML version 1.0)
      //  writer.writeStartDocument("1.0");
        String data_collectionURI = "http://cerch.kcl.ac.uk/bril/schema/data_collection";
		String data_collectionPrefix= "diffset";
		writer.setDefaultNamespace( data_collectionURI );
		writer.writeStartDocument();
		
		writer.writeStartElement( data_collectionURI, data_collectionPrefix+":"+"diffraction_dataset" );
		
		writer.writeNamespace( data_collectionPrefix, data_collectionURI);

		writer.writeStartElement(data_collectionURI, "diffraction_data");
        // Write the id attribute for <diffraction_data>
        writer.writeAttribute("id", "1");
        writer.writeStartElement(data_collectionURI,"crystal_id");
        writer.writeCharacters("001");
        writer.writeEndElement();       
        writer.writeEndElement();
        
        // write element <diffraction_detector> with diffrn_id attribute
        writer.writeStartElement(data_collectionURI, "diffraction_detector");
        writer.writeAttribute("diffrn_id", "1");
        //add child element detector to <diffraction_detector>
        writer.writeStartElement(data_collectionURI, "detector");
        writer.writeCharacters( "ADSC");
        writer.writeEndElement();  
      //add child element <type> to <diffraction_detector>
        writer.writeStartElement(data_collectionURI, "type");
        writer.writeCharacters("921");
        writer.writeEndElement(); 
        //add child element collection_date to <diffraction_detector>
        writer.writeStartElement(data_collectionURI, "collection_date");
        writer.writeCharacters("sun 23 december");
        writer.writeEndElement();
        writer.writeEndElement();
        
        writer.writeStartElement(data_collectionURI, "diffraction_measurement");
        writer.writeAttribute("diffrn_id", "1");
        //add child element beam_centre_X to <diffraction_measurement>
        writer.writeStartElement(data_collectionURI, "detector_distance");
        writer.writeCharacters("5.5");
        writer.writeEndElement();  
      //add child element beam_centre_X to <diffraction_measurement>
        writer.writeStartElement(data_collectionURI, "beam_centre_X");
        writer.writeCharacters("5.5");
        writer.writeEndElement();  
      //add child element beam_centre_Y to <diffraction_measurement>
        writer.writeStartElement(data_collectionURI, "beam_centre_Y");
        writer.writeCharacters("54.5");
        writer.writeEndElement();  
      //add child element OSC_range to <diffraction_measurement>
        writer.writeStartElement(data_collectionURI,"OSC_range");
        writer.writeCharacters("1");
        writer.writeEndElement();  
        writer.writeEndElement();  

        // Write element title under <diffraction_source>
        writer.writeStartElement(data_collectionURI, "diffraction_source");
        writer.writeAttribute("diffrn_id", "1");
        // Write element <wavelength> under <diffraction_source>
        writer.writeStartElement(data_collectionURI, "wavelength");
        writer.writeCharacters("8.99");
        writer.writeEndElement();      
        // Write element source under <diffraction_source>
        writer.writeStartElement(data_collectionURI, "source");
        writer.writeCharacters("synchrotron");
        writer.writeEndElement();
        writer.writeEndElement();
        // write end element
        writer.writeEndDocument();

        // flush and close
        writer.flush();
        writer.close();



	}

}
