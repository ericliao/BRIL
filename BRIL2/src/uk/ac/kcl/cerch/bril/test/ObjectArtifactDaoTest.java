package uk.ac.kcl.cerch.bril.test;

import java.util.Vector;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.ac.kcl.cerch.soapi.objectstore.FileFormat;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectArtifact;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectStore;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectStoreException;
import uk.ac.kcl.cerch.soapi.objectstore.database.ObjectArtifactDao;
import junit.framework.TestCase;

public class ObjectArtifactDaoTest extends TestCase {

	public void testGetObjectArtifactById(){
		 ApplicationContext applicationContext = new ClassPathXmlApplicationContext("soapi.xml");
	        ObjectArtifactDao objectArtifactDao = 
	            (ObjectArtifactDao) applicationContext.getBean("objectArtifactDao");
	        ObjectStore objectStore = (ObjectStore) applicationContext.getBean("objectStore");
	        ObjectArtifact objectArtifact = objectArtifactDao.getObjectArtifactById("99");
	        try{
	            if(objectArtifact.getType().equals("FileFormat"))
	            {
	                FileFormat ff
	                = (FileFormat) objectStore.getObjectArtifact(objectArtifact.getId());
	                System.out.println(ff.getRelatedObjectArtifactId());
	                System.out.println("fileformat= "+ ff.getFormat());
	            }
	        }
	        catch(ObjectStoreException e)
	        {
	            e.printStackTrace();
	        }  
	}
	
	public void testGetObjectArtifactsByType(){
		 ApplicationContext applicationContext = new ClassPathXmlApplicationContext("soapi.xml");
	        ObjectArtifactDao objectArtifactDao = 
	            (ObjectArtifactDao) applicationContext.getBean("objectArtifactDao");
	        ObjectStore objectStore = (ObjectStore) applicationContext.getBean("objectStore");
	        Vector<ObjectArtifact> objectArtifact = objectArtifactDao.getObjectArtifactByType("FileFormat");
	        System.out.println(objectArtifact.size());
	   
	}
}
