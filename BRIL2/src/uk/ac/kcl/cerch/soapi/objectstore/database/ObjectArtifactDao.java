/*
 * Created on 20 Aug 2007
 */
package uk.ac.kcl.cerch.soapi.objectstore.database;

import java.util.Vector;

import uk.ac.kcl.cerch.soapi.objectstore.ObjectArtifact;

public interface ObjectArtifactDao {
    public ObjectArtifact getObjectArtifactById(String id);
    public Vector<ObjectArtifact> getObjectArtifactByType(String type);
    public long saveObjectArtifact(ObjectArtifact objectArtifact);
}
