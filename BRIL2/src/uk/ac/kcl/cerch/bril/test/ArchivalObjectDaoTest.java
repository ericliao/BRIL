package uk.ac.kcl.cerch.bril.test;

import java.util.Iterator;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import uk.ac.kcl.cerch.bril.objectstore.RelationshipObject;
import uk.ac.kcl.cerch.bril.relationship.Relationship;
import uk.ac.kcl.cerch.soapi.objectstore.ArchivalObject;
import uk.ac.kcl.cerch.soapi.objectstore.DublinCore;
import uk.ac.kcl.cerch.soapi.objectstore.FileCharacterisation;
import uk.ac.kcl.cerch.soapi.objectstore.FileFormat;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectArtifact;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectStore;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectStoreException;
import uk.ac.kcl.cerch.soapi.objectstore.OriginalContent;
import uk.ac.kcl.cerch.soapi.objectstore.database.ArchivalObjectDao;
import uk.ac.kcl.cerch.soapi.objectstore.database.SIPDao;
import uk.ac.kcl.cerch.soapi.sip.SIP;
import junit.framework.TestCase;

public class ArchivalObjectDaoTest extends TestCase {
	
public void testGetArchivalObjectById(){
	ApplicationContext applicationContext = new FileSystemXmlApplicationContext("config/soapi.xml");
	//object that connects to the database or file system that holds all the objects
    ArchivalObjectDao archivalObjectDao = (ArchivalObjectDao) applicationContext.getBean("archivalObjectDao");
    /*
     * Get archival object with the given archival object id 
     */
    ArchivalObject archivalObject =archivalObjectDao.getArchivalObjectById("bril:33f7e9b9-a62b-4c8d-847c-8e66bc90f830");
    SIPDao sipsDao = (SIPDao) applicationContext.getBean("sipDao");
    ObjectStore objectStore = (ObjectStore)applicationContext.getBean("objectStore");
    
    System.out.println("Archival Object id: "+archivalObject.getId());
    System.out.println("Archival Object path: " + archivalObject.getPath().toString().trim());
    System.out.println("Archival Object filename: "+archivalObject.getFilename());
    SIP sip =archivalObject.getSip();
    System.out.println("SIP id: " + sip.getId());
    
    ObjectArtifact objectArtifact = null;
    //ObjectArtifact objectArtifact1 = null;
    /*
     * Get the Object Artifacts for this Archival Object
     * */
    // Set<ObjectArtifact> objectArtifacts = archivalObject.getObjectArtifactsByType("FileFormat");
     Set<ObjectArtifact> objectArtifactsSet = archivalObject.getObjectArtifacts();

    //Iterator<ObjectArtifact> iter = objectArtifacts.iterator();
    Iterator<ObjectArtifact> iter = objectArtifactsSet.iterator();
    System.out.println("SIZE: "+objectArtifactsSet.size());
    /*
     * list all the object artifacts in the database
     * */
/*    while(iter.hasNext()) {
    	objectArtifact = (ObjectArtifact) iter.next();
        System.out.println("Object Artifact id: "+ objectArtifact.getId());
        System.out.println("Object Artifact type: "+ objectArtifact.getType());
        String type= objectArtifact.getType();
        String objectArtifactId = objectArtifact.getId();
        if(type.equals("OriginalContent")){
        	try {
				OriginalContent originalContent = 
				    (OriginalContent) objectStore.getObjectArtifact(objectArtifactId);
				 System.out.println("OriginalContent filepath: "+ originalContent.getFilePath());
			} catch (ObjectStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         
        }
    }*/
    while(iter.hasNext()) {
    	objectArtifact = (ObjectArtifact) iter.next();
        String type= objectArtifact.getType();
        String objectArtifactId = objectArtifact.getId();
     //   System.out.println("Object Artifact id: "+ objectArtifactId);
     //   System.out.println("Object Artifact type: "+ type);
		// System.out.println("-------------------------------------");
        if(type.equals("OriginalContent")){
        	try {
				OriginalContent originalContent = 
				    (OriginalContent) objectStore.getObjectArtifact(objectArtifactId);
				 System.out.println("OriginalContent filepath: "+ originalContent.getFilePath());
			} catch (ObjectStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 System.out.println("-------------------------------------");
         
        }if (type.equals("FileFormat")){
        	try {
				FileFormat fileformat = (FileFormat)objectStore.getObjectArtifact(objectArtifactId);
				 System.out.println("FileFormat format: "+fileformat.getFormat());
				 System.out.println("FileFormat description: "+fileformat.getDescription());
			} catch (ObjectStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 System.out.println("-------------------------------------");
        }if (type.equals("FileCharacterisation")){
        	try {
        		FileCharacterisation fileCharacterisation = (FileCharacterisation)objectStore.getObjectArtifact(objectArtifactId);
				 System.out.println("FileCharacterisation metadata: "+fileCharacterisation.getMetadata());
				// System.out.println("FileFormat description: "+fileCharacterisation.getDescription());
			} catch (ObjectStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
   	 System.out.println("-------------------------------------");
        if (type.equals("RelationshipObject")){
        	try {
        		RelationshipObject relationshipObject = (RelationshipObject)objectStore.getObjectArtifact(objectArtifactId);
        		Relationship rel =  relationshipObject.getRelationships().get(0);
				 System.out.println("RelationshipObject subject: "+rel.getSubject());
				 System.out.println("RelationshipObject predicate: "+rel.getPredicate());
				 System.out.println("RelationshipObject object: "+rel.getObject());
				
			} catch (ObjectStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        System.out.println("-------------------------------------");
        if (type.equals("DublinCore")){
        	try {
        		DublinCore dublinCore = (DublinCore)objectStore.getObjectArtifact(objectArtifactId);
				 System.out.println("DublinCore id: "+dublinCore.getId());
				 System.out.println("DublinCore title: "+dublinCore.getTitle());
				 System.out.println("DublinCore description: "+dublinCore.getDescription());
				 System.out.println("DublinCore format: "+dublinCore.getFormat());
				 System.out.println("DublinCore date: "+dublinCore.getDate());
				// System.out.println("FileFormat description: "+fileCharacterisation.getDescription());
			} catch (ObjectStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
       
    }
    
    

   // BrilSIP brilsip =(BrilSIP)sip1;
  
}
}
