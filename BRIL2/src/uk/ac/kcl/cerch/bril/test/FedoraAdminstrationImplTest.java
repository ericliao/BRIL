package uk.ac.kcl.cerch.bril.test;


import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.fcrepo.server.types.gen.ObjectFields;

import uk.ac.kcl.cerch.bril.common.fedora.BrilObjectRepositoryException;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraAdminstrationImpl;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraNamespaceContext.FedoraNamespace;
/*import uk.ac.kcl.cerch.bril.common.fedora.FedoraHandler;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraNamespaceContext.FedoraNamespace;
import uk.ac.kcl.cerch.bril.common.metadata.DublinCore;
import uk.ac.kcl.cerch.bril.common.metadata.DublinCoreElement;
import uk.ac.kcl.cerch.bril.common.types.DataStreamType;
import uk.ac.kcl.cerch.bril.common.types.DatastreamObject;
import uk.ac.kcl.cerch.bril.common.types.DatastreamObjectContainer;
import uk.ac.kcl.cerch.bril.common.util.FileUtil;
import uk.ac.kcl.cerch.bril.fileformat.CrystallographyObjectType;*/
import junit.framework.TestCase;

public class FedoraAdminstrationImplTest extends TestCase{
	
/*	public void testHasObject(){
		try {
			FedoraAdminstrationImpl admin = new FedoraAdminstrationImpl();
			
			boolean test = admin.hasObject("bril:00EXPT123");
			System.out.println(test);
		} catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/
	
	/*public void testFindObjectPids(){
		FedoraAdminstrationImpl admin;
		try {
			admin = new FedoraAdminstrationImpl();
			String comparisionOperator= "has";
			String propertyFieldName ="title";
			String value = "baa5d5";
			
			int maximumResult=1000;
			
			ObjectFields[] objectFields = admin.findObjectPids(propertyFieldName, comparisionOperator, value, maximumResult);
			String[] pids = new String[objectFields.length];
			for (int i = 0; i < objectFields.length; i++) {
				pids[i] = objectFields[i].getPid();
				
				// log.debug( "pid " + i + ": " + pids[i].toString() );
			}
			System.out.println("pids: " + pids[0] + ", " + pids[1]);
			
		} catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/
	
	/*public void testAddObjectRelation(){
		try {
			FedoraAdminstrationImpl admin = new FedoraAdminstrationImpl();
			String subj="00EXPT123";
			String pred ="hasPart";
			String object ="bril:a328b474-190f-44d4-98dc-40327f5dce6f";
			String prefix_predicate = null;
			String fedoraURI= FedoraNamespace.FEDORA.getURI();
			String brilPrefix =  FedoraNamespace.BRIL.getPrefix();
		
		    String namespaceURI_predicate =null;
			if (pred.equals("isPartOf") || pred.equals("hasPart")) {
				prefix_predicate = FedoraNamespace.FEDORA.getPrefix();
				namespaceURI_predicate = FedoraNamespace.FEDORARELSEXT.getURI();

			} else {
				namespaceURI_predicate = FedoraNamespace.BRILRELS.getURI();
				prefix_predicate = FedoraNamespace.BRILRELS.getPrefix();
			}
			subj = fedoraURI+brilPrefix+":"+subj;
			pred = namespaceURI_predicate+pred;
			object =fedoraURI+object;
			System.out.println(subj);
			System.out.println(pred);
			System.out.println(object);
			System.out.println(admin.hasObject(subj));
			
		   admin.addObjectRelation(subj, pred, object, false);
		
		} catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/
	
/*	public void testRetrieveDataFromObject(){
		try {
			String objectId="bril:00EXPT123";

			FedoraAdminstrationImpl admin = new FedoraAdminstrationImpl();
			DatastreamObjectContainer ds =admin.retrieveDataFromObject(objectId, DataStreamType.RelsExt);
			DatastreamObject dso= ds.getDatastreamObject(DataStreamType.RelsExt);
			byte[] data = dso.getDataBytes();
			//DublinCore dc = ds.getDublinCoreMetaData();
			//String id = dc.getDCValue(DublinCoreElement.ELEMENT_IDENTIFIER);
			//System.out.println(id);
			//System.out.println( dc.getDCValue(DublinCoreElement.ELEMENT_DATE));
			InputStream inputstream = new ByteArrayInputStream(data);
			
			FileUtil.writeByteArrayToFile("returnedRelsext.xml", data);
		} catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}*/
/*	public void testStoreObject(){
		String foxmldata = FileUtil.writeFileToString("c:/tmp/archive/foxmlexample.xml");
		byte[] foxmlbyte = null;
		FedoraHandler handler;
		try {
		
			foxmlbyte = foxmldata.getBytes("UTF-8");
			handler= new FedoraHandler();
		    String pid = handler.ingest(foxmlbyte, "info:fedora/fedora-system:FOXML-1.1", "Ingest of FOXML");
			
		} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (BrilObjectRepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}*/
/*	public void testGetExperimentRelatedObjects(){
		try {
			FedoraAdminstrationImpl admin = new FedoraAdminstrationImpl();
			String expId ="info:fedora/bril:00EXPT123";
			String expId1 ="info:fedora/bril:expaa4ca950-a82f-49a7-93e9-59641de0ddb8";
			//String objectFormat="mtzReflectionFile";
			String objectFormat= CrystallographyObjectType.CoordinateFile.getType();
			String identifierToCheck="bril:3224837b-05dd-476e-9ea6-bb8b035352ca";
			System.out.println(objectFormat);
			Vector objects = admin.getExperimentRelatedObjectIds(expId1, objectFormat);
			
			String xmlstring =admin.getDiffractionSetObjectIds(expId1,"title","DiffractionImageSet");
			System.out.println(objects);
			System.out.println(admin.getExperimentRelatedObjectIdsTitle(expId1, objectFormat));
			//System.out.println(xmlstring);
		} catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/

/*	public void testHasRelationship(){
		FedoraAdminstrationImpl admin;
		String brilrels= FedoraNamespace.BRILRELS.getURI();
		String predicate=  brilrels+"wasGeneratedByTask";
		try {
			admin = new FedoraAdminstrationImpl();
			//System.out.println(predicate);
			//System.out.println("---------------");
			boolean res = admin.hasRelationship("bril:847856b0-c065-4da7-a092-a10c9fb04c94",predicate, "chainsaw");
			System.out.println(res);
		}catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/
	
/*	public void testGetRelationship(){
		FedoraAdminstrationImpl admin;
		//bril:3f89124c-205a-428a-989b-0448cf94a029
		String brilrels= FedoraNamespace.BRILRELS.getURI();
		String predicate=  brilrels+"wasDerivedFrom";
		try {
			admin = new FedoraAdminstrationImpl();
			//System.out.println(predicate);
			//System.out.println("---------------");
			Vector<String> res= admin.getObjectsWithPredicate("bril:3f89124c-205a-428a-989b-0448cf94a029",predicate);
			System.out.println(res);
			for(int i=0;i<res.size();i++){
				String objectId =res.get(i);
				if(objectId.contains("/")){
					objectId = objectId.substring(objectId.indexOf("/")+1);
					
					if(!objectId.contains("bril:")){
						objectId="bril:"+objectId;
					}
					
				}else
				{
					if(!objectId.contains("bril:")){
						objectId="bril:"+objectId;
					}
				}
				System.out.println(objectId);
				String format= admin.getDCFormat(objectId);
				//System.out.println(format);
				if(format.equals("coordinateFile"))
				System.out.println("format: "+format);
			}
		}catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/
/*	public void testHasPredicate(){
		FedoraAdminstrationImpl admin;
		//bril:3f89124c-205a-428a-989b-0448cf94a029
		String brilrels= FedoraNamespace.BRILRELS.getURI();
		String predicate=  brilrels+"wasDerivedFrom";
		try {
			admin = new FedoraAdminstrationImpl();
			//System.out.println(predicate);
			//System.out.println("---------------");
			boolean res= admin.hasPredicate("bril:3f89124c-205a-428a-989b-0448cf94a029",predicate);
			System.out.println(res);
		}catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/
	
	public void testGetDCFormat(){
		FedoraAdminstrationImpl admin;
		//bril:3f89124c-205a-428a-989b-0448cf94a029
		//String brilrels= FedoraNamespace.BRILRELS.getURI();
		//String predicate=  brilrels+"wasDerivedFrom";
		try {
			admin = new FedoraAdminstrationImpl();
			//System.out.println(predicate);
			//System.out.println("---------------");
			//String res= admin.getDCFormat("bril:3f89124c-205a-428a-989b-0448cf94a029");
			
			//String expId="bril:expaa4ca950-a82f-49a7-93e9-59641de0ddb8";
			//in bril-dev
			String expId ="bril:expa416b0b2-de69-471c-bd63-fcb64dc15a28";
			String objectFormat="coordinateFile";
			String predicate= "wasDerivedFrom";
			//String object="bril:cefaeb9f-2e87-40da-b97f-8eb4ba0d1dc2";
			//in bril-dev
		   String object="bril:9c9f4921-5839-4afc-b266-fbede4de4b9e";
		
			//currently objects has id
			//info:fedora/cefaeb9f-2e87-40da-b97f-8eb4ba0d1dc2
			String resultXML =admin.getBrilPredicateRelatedObjectIds( expId, objectFormat,  predicate, object);
			System.out.println(resultXML);
			
			try {
				Document sourceDoc= stringToDom(resultXML.trim());
				
				NodeList list = sourceDoc.getElementsByTagName("*");
				for (int i = 0; i < list.getLength(); i++) {
					Element element = (Element) list.item(i);
					//System.out.println(element);
					if(element.getTagName().equals("result")){
						NodeList childList = element.getChildNodes();
					//	System.out.println(childList.getLength());
						String pid=null;
						Vector<String> id=new Vector<String>();
						String[] date1=null;
						for(int j=0;j<childList.getLength();j++){
							
						 
						   if(childList.item(j).getNodeName().equals("object")){
						   Element obje = (Element) childList.item(j);
						//   System.out.println(childList.item(j).getNodeName());
						//   System.out.println("pid: "+obj.getAttribute("uri").trim());
						   pid=obje.getAttribute("uri").trim();
						   }
						   int count =0;
						   if(childList.item(j).getNodeName().equals("id")){
						//	   System.out.println(childList.item(j).getTextContent());
							   id.add(childList.item(j).getTextContent());
							 //  date1[count]=childList.item(j).getTextContent();
							   count++;
						   }							
					
						}
						
						System.out.println(id);
					}
		
					
				}
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private Document stringToDom(String xmlSource) throws SAXException,
	ParserConfigurationException, IOException {
DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
DocumentBuilder builder = factory.newDocumentBuilder();
return builder.parse(new InputSource(new StringReader(xmlSource)));
}

}
