package uk.ac.kcl.cerch.soapi.objectstore.database;


import java.util.Vector;

import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import uk.ac.kcl.cerch.soapi.objectstore.ArchivalObject;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectArtifact;

public class JdbcObjectArtifactDao extends JdbcDaoSupport implements ObjectArtifactDao {

    private ArchivalObjectDao archivalObjectDao;
    
    /**
     * Important Note: This method, as well as all the XXXXDao Methods in this project,
     * obtain the RECORD for an ArchivalObject or ObjectArtifact from the Database but 
     * NOT the actual Object! To get the actual object from the ObjectStore, get the 
     * desired implementation ObjectStore (e.g by using Spring) and then use the 
     * getObjectArtifact() method.
     *  
     */
    public ObjectArtifact getObjectArtifactById(String id) {

        ObjectArtifact objectArtifact = null;
        
        String sql = "SELECT * FROM objectartifacts WHERE Id = '" + id + "'";
        
        SqlRowSet sqlRowSet = getJdbcTemplate().queryForRowSet(sql);

        if( sqlRowSet != null && sqlRowSet.next()) 
        {
            objectArtifact = new ObjectArtifact();
            objectArtifact.setId(sqlRowSet.getString("id"));
            objectArtifact.setType(sqlRowSet.getString("type"));
            ArchivalObject archivalObject = 
                archivalObjectDao.getArchivalObjectById(sqlRowSet.getString("archivalObjectId"));
            objectArtifact.setArchivalObject(archivalObject);
        }
               
        return objectArtifact;
    }
    
    
    /**
     * Created for Bril, to get all ObjectArtifact with a certain type. This could be very large.
     * 
     * 
     * @param type
     * @return
     */
    

    public Vector<ObjectArtifact> getObjectArtifactByType(String type) {
    	Vector<ObjectArtifact> objectArtifactVector = new Vector<ObjectArtifact>();
    	ObjectArtifact objectArtifact = null;
    	 String sql = "SELECT * FROM objectartifacts WHERE Type = '" + type + "'";
    	 //ORDER BY Type ASC

    	 SqlRowSet sqlRowSet = getJdbcTemplate().queryForRowSet(sql);
    	 int rowCount=0;
         if( sqlRowSet != null && sqlRowSet.next()) 
         {
        	 while(sqlRowSet.next()){
             objectArtifact = new ObjectArtifact();
             objectArtifact.setId(sqlRowSet.getString("id"));
             objectArtifact.setType(sqlRowSet.getString("type"));
             
             ArchivalObject archivalObject = 
                 archivalObjectDao.getArchivalObjectById(sqlRowSet.getString("archivalObjectId"));
             objectArtifact.setArchivalObject(archivalObject);
             objectArtifactVector.add(objectArtifact); 
             rowCount++;
        	 }
        	 System.out.println("Num of records : "+ rowCount);
         }
         
    	return objectArtifactVector;
    }

    // TODO Add it to Archival Object and then Save! 
    // TODO Maybe this should be called from ObjectStore??
    // SQL Update (overwrite) or Insert (doesnt exist - add new)-> Use ao.addObjectArtifact(objectArtifact) method
    // NOTE: Important: This method assumes the objectArtifact has an ID?????????
    public long saveObjectArtifact(ObjectArtifact objectArtifact) {
        //boolean exists = false;
        @SuppressWarnings("unused")
		ArchivalObject ao = objectArtifact.getArchivalObject();
        String objectArtifactId = objectArtifact.getId();
        
        // ObjectArtifact already has an ID
        if( objectArtifactId != null)
        {
            // ObjectArtifact already exists => UPDATE
            if( this.getObjectArtifactById(objectArtifactId) != null)
            {
                // Check if same type or has same ID by accident
                ObjectArtifact tmpObjectArtifact = this.getObjectArtifactById(objectArtifactId);
                if( tmpObjectArtifact.getType().equals(objectArtifact.getType()))
                {
                    System.out.println("TYPE MATCHES!");
                    // UPDATE
                }
            }
            // Doesn't exist -> ADD
            else
            {
                //ao.addObjectArtifact(objectArtifact);
            }
        }
        return 0;
    }
    
    public ArchivalObjectDao getArchivalObjectDao() {
        return archivalObjectDao;
    }

    public void setArchivalObjectDao(ArchivalObjectDao archivalObjectDao) {
        this.archivalObjectDao = archivalObjectDao;
    }



}
