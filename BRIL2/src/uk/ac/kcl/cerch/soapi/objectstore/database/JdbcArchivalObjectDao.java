/*
 * Created on 22 Aug 2007
 */
package uk.ac.kcl.cerch.soapi.objectstore.database;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import uk.ac.kcl.cerch.soapi.objectstore.ArchivalObject;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectArtifact;
import uk.ac.kcl.cerch.soapi.sip.SIP;

public class JdbcArchivalObjectDao extends JdbcDaoSupport implements ArchivalObjectDao {
    
    public ArchivalObject getArchivalObjectById(String id) {
        ArchivalObject archivalObject = null;
        Set<ObjectArtifact> objectArtifacts = new HashSet<ObjectArtifact>();
        String sql = "SELECT * FROM objectartifacts WHERE ArchivalObjectId = '" + id + "'";
               
        SqlRowSet sqlRowSet = getJdbcTemplate().queryForRowSet(sql);
       // sqlRowSet.

        while(sqlRowSet.next()) {
            ObjectArtifact objectArtifact = new ObjectArtifact();
            objectArtifact.setId(sqlRowSet.getString("id"));
            objectArtifact.setType(sqlRowSet.getString("type"));
            objectArtifacts.add(objectArtifact);
        }
       
        sql = "SELECT * FROM archivalobjects WHERE id = '" + id + "'";
        
        String sipId = null;
       
        sqlRowSet = getJdbcTemplate().queryForRowSet(sql);
        if(sqlRowSet.next()) {
            archivalObject = new ArchivalObject();
            archivalObject.setId(sqlRowSet.getString("id"));
            archivalObject.setFilename(sqlRowSet.getString("filename"));
            archivalObject.setPath(sqlRowSet.getString("path"));
            archivalObject.setObjectArtifacts(objectArtifacts);
            
            sipId = sqlRowSet.getString("SIPId");
            
            for(ObjectArtifact objectArtifact : objectArtifacts) {
                objectArtifact.setArchivalObject(archivalObject);
            }
        }
        
        SIP sip = new SIP();
        sip.setId(sipId);
        
        archivalObject.setSip(sip);
        
        return archivalObject;
    }

    public String saveArchivalObject(ArchivalObject archivalObject) {
        if(archivalObject.getObjectArtifacts() != null) {
            List<String> objectArtifactIds = new ArrayList<String>();
            
            String sql = "SELECT * FROM objectartifacts WHERE ArchivalObjectId = '" + archivalObject.getId() + "'";
            SqlRowSet sqlRowSet = getJdbcTemplate().queryForRowSet(sql);
            while(sqlRowSet.next()) {
                objectArtifactIds.add(sqlRowSet.getString("Id"));
            }
            
            for(ObjectArtifact objectArtifact : archivalObject.getObjectArtifacts()) {
                if(! checkIfArtifactIdExists(objectArtifactIds, objectArtifact)) {
                    sql = "INSERT INTO objectartifacts(Id, ArchivalObjectId, Type) VALUES('";
                    sql += objectArtifact.getId() + "', '" + archivalObject.getId() + "', '" + objectArtifact.getType() + "')";
                    getJdbcTemplate().update(sql);
                }
            }
        }

        String sql = "SELECT * FROM archivalobjects WHERE Id = '" + archivalObject.getId() + "'";
        SqlRowSet sqlRowSet = getJdbcTemplate().queryForRowSet(sql);
        
        if(sqlRowSet.next()) {
            sql = "UPDATE archivalobjects SET Filename = '" + archivalObject.getFilename() + "', Path = '" + archivalObject.getPath() + "'";
            sql += " WHERE Id = '" + archivalObject.getId() + "'";
            getJdbcTemplate().update(sql);
        }
        else {
            sql = "INSERT INTO archivalobjects(Id, " + ((archivalObject.getFilename() != null) ? "Filename, " : "") + "Path, SIPId) " + 
            " VALUES('" + archivalObject.getId() + "', '" + ((archivalObject.getFilename() != null) ? archivalObject.getFilename() + "', '" : "") 
            + archivalObject.getPath() + "', '" + archivalObject.getSip().getId() +  "')";
            getJdbcTemplate().update(sql);
        }
    
        return archivalObject.getId();
    }

    private boolean checkIfArtifactIdExists(List<String> objectArtifactIds, ObjectArtifact objectArtifact) {
        boolean exists = false;
        
        for(String objectArtifactId : objectArtifactIds) {
            if(String.valueOf(objectArtifact.getId()).equals(objectArtifactId)) {
                exists = true;
                break;
            }
        }
        
        return exists;
    }
}
 