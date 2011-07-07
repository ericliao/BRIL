/*
 * Created on 17 Aug 2007
 */
package uk.ac.kcl.cerch.soapi.objectstore;

import java.io.Serializable;

/**
 * The database record for an <code>ObjectArtifact</code>.
 * 
 * @author Vijay N Albuquerque
 *
 */
@SuppressWarnings("serial")
public class ObjectArtifact implements Serializable {
    private String id;
    private String type;
    private ArchivalObject archivalObject;

    public ObjectArtifact()
    {
        this.setType(this.getClass().getSimpleName());
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArchivalObject getArchivalObject() {
        return archivalObject;
    }

    public void setArchivalObject(ArchivalObject archivalObject) {
        this.archivalObject = archivalObject;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}