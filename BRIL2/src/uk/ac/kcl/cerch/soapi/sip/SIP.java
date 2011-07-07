/*
 * Created on 16 Aug 2007
 */
package uk.ac.kcl.cerch.soapi.sip;

import java.io.Serializable;
import java.util.Set;

import uk.ac.kcl.cerch.soapi.objectstore.ArchivalObject;

/**
 * A representation of a SIP (Submission Information Package).
 *  
 * A SIP contains resources submitted to a repository for archival purposes.
 * 
 * @author Vijay N Albuquerque
 *
 */
@SuppressWarnings("serial")
public class SIP implements Serializable {
    protected String id;
    protected Set<ArchivalObject> archivalObjects;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<ArchivalObject> getArchivalObjects() {
        return archivalObjects;
    }

    public void setArchivalObjects(Set<ArchivalObject> archivalObjects) {
        this.archivalObjects = archivalObjects;
        
    }
}
