package uk.ac.kcl.cerch.bril.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import uk.ac.kcl.cerch.bril.common.fedora.FedoraRelsExt;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraNamespaceContext.FedoraNamespace;
import uk.ac.kcl.cerch.bril.common.metadata.DublinCore;
import uk.ac.kcl.cerch.bril.common.metadata.DublinCoreElement;
import uk.ac.kcl.cerch.bril.common.types.BrilTransformException;
import uk.ac.kcl.cerch.bril.common.types.DataStreamType;
import uk.ac.kcl.cerch.bril.common.types.DatastreamMimeType;
import uk.ac.kcl.cerch.bril.common.types.DatastreamObjectContainer;
import uk.ac.kcl.cerch.bril.common.util.FileUtil;
import junit.framework.TestCase;

public class DatastreamObjectContainerTest extends TestCase{
	
	public void testDatastreamObjectContainer() throws ParserConfigurationException, BrilTransformException{
		File f = new File("c:\\BRIL\\data\\5d5.mtz");
		byte[] data=null;
		//byte[] relsextData = null;
		try {
			data = FileUtil.getBytesFromFile(f);
			//relsextData =FileUtil.getBytesFromFile(f1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("--------- Create: DublinCore created ---------");
		DublinCore dc = new DublinCore("bril:123");
		dc.setDescription("This is test ingest");
		dc.setDate("17 Jun 2008 09:29:03", "dd MMM yyyy HH:mm:ss");
		dc.setTitle("Diffraction image");
		dc.setCreator("Stella Fabiane");
		dc.setSubject("mtz");
		OutputStream out =null;
		System.out.println("--------- DublinCore created ---------");
		
		System.out.println("--------- Create: FedoraRelsExt object ---------");
		FedoraRelsExt relsExt = new FedoraRelsExt("bril:123");
	
		QName predicate = new QName( FedoraNamespace.FEDORARELSEXT.getURI(), "isPartOf",FedoraNamespace.FEDORA.getPrefix() );
		QName object = new QName( "","111",FedoraNamespace.FEDORA.getURI()+FedoraNamespace.BRIL.getPrefix());
		QName predicate1 = new QName( FedoraNamespace.BRILRELS.getURI(), "wasDerivedFrom",FedoraNamespace.BRILRELS.getPrefix() );
		QName object1 = new QName( "","112",FedoraNamespace.FEDORA.getURI()+FedoraNamespace.BRIL.getPrefix());
		
		relsExt.addRelationship(predicate, object);	
		relsExt.addRelationship(predicate1, object1);	
		ByteArrayOutputStream relsExt_baos = new ByteArrayOutputStream();
		relsExt.serialize(relsExt_baos,"");		
		System.out.println("--------- Relationship (relsext) created ---------" );
		
		System.out.println("--------- Create: DatastreamObjectContainer ---------");

		DatastreamObjectContainer dsc= new DatastreamObjectContainer(dc.getDCValue(DublinCoreElement.ELEMENT_IDENTIFIER));
		dsc.addMetaData(dc);
		
		dsc.addDatastreamObject(DataStreamType.RelsExt, DatastreamMimeType.APPLICATION_RDF.getMimeType(),"relationship","bril",relsExt_baos.toByteArray());
		dsc.addDatastreamObject(DataStreamType.OriginalData, DatastreamMimeType.APPLICATION_OCTET.getMimeType(),"IMAGE","bril",data);
		System.out.println("--------- DatastreamObjectContainer created ---------");
	}

}
