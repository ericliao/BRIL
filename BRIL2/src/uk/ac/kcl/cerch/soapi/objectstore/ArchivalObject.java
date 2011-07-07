/*
 * Created on 17 Aug 2007
 */
package uk.ac.kcl.cerch.soapi.objectstore;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import uk.ac.kcl.cerch.soapi.sip.SIP;

/**
 * An individual archival unit contained within a SIP package.<br/>
 * 
 * Usually a file or directory contained within a SIP. Only files of value for archiving in a SIP 
 * (e.g. images, text documents etc.) would be considered as an <code>ArchivalObject</code>. Miscellaneous files 
 * (e.g. checksums, readme files etc.) within a SIP would not be <code>ArchivalObject</code>s.  
 * 
 * @author Vijay N Albuquerque
 *
 */
@SuppressWarnings("serial")
public class ArchivalObject implements Serializable {
    private String id;
    private String filename; // Original filename of the archival unit.
    private String path; // Original path of the archival unit relative to the SIP path.
    private SIP sip;
    private Set<ObjectArtifact> objectArtifacts;
    
    /**
     * Default constructor.
     * 
     */
    public ArchivalObject() {
        objectArtifacts = new HashSet<ObjectArtifact>();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public Set<ObjectArtifact> getObjectArtifacts() {
        return objectArtifacts;
    }
    
    public void setObjectArtifacts(Set<ObjectArtifact> objectArtifacts) {
        this.objectArtifacts = objectArtifacts;
    }

    public void addObjectArtifact(ObjectArtifact objectArtifact) {
        objectArtifacts.add(objectArtifact);
    }

    /**
     * Utility method that returns <code>ObjectArtifact</code>s filtered by class
     * type.
     * 
     * @param className Class name of <code>ObjectArtifact</code>s to filter.
     * @return <code>ObjectArtifact</code>s filtered by type.
     */
    public Set<ObjectArtifact> getObjectArtifactsByType(String className) {
        Set<ObjectArtifact> objectArtifactsByTypeSet = null;
        
        // Remove package name if present.
        if(className.contains(".")) {
            className = className.substring(className.lastIndexOf(".") + 1);
        }
        
        if(objectArtifacts != null && objectArtifacts.size() > 0) {
            objectArtifactsByTypeSet = new HashSet<ObjectArtifact>();
            
            for(ObjectArtifact objectArtifact : objectArtifacts) {
                if(objectArtifact.getType().equals(className)) {
                    objectArtifactsByTypeSet.add(objectArtifact);
                }
            }
        }
        
        return objectArtifactsByTypeSet;
    }
    
    public SIP getSip() {
        return sip;
    }
    
    public void setSip(SIP sip) {
        this.sip = sip;
    }

    public boolean isDirectory() {
        return (new File(path)).isDirectory();
    }
}
