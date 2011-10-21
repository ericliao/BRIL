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

import uk.ac.kcl.cerch.bril.common.fedora.BrilObjectRepositoryException;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraAdminstrationImpl;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraNamespaceContext.FedoraNamespace;

public class FedoraAdminstrationImplMainTest {
	public static void main(String args[]){
		getBrilPredicateRelatedObjectIds();
		
	}
	public static void getBrilPredicateRelatedObjectIds(){
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
							   //id.add(childList.item(j).getTextContent());
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
	private static Document stringToDom(String xmlSource) throws SAXException,
	ParserConfigurationException, IOException {
DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
DocumentBuilder builder = factory.newDocumentBuilder();
return builder.parse(new InputSource(new StringReader(xmlSource)));
}
}
