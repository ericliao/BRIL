/*
 * Created on 14 Aug 2007
 */
package uk.ac.kcl.cerch.soapi.objectstore;

/**
 * <code>ObjectStore</code> acts as a store for <code>ObjectArtifact</code>s. The store would
 * be implementation specific e.g. File system, RDBMS etc.
 * 
 * @author Vijay N Albuquerque
 *
 */
public interface ObjectStore {
    /**
     * Puts the <code>ObjectArtifact</code> in the store and returns a unique identifier
     * for the stored object.
     * 
     * @param objectArtifact <code>ObjectArtifact</code> to be stored.
     * @return Unique identifier for the stored <code>ObjectArtifact</code>.
     * @throws ObjectStoreException
     */
    public String putObjectArtifact(ObjectArtifact objectArtifact) throws ObjectStoreException;
    
    /**
     * Gets the <code>ObjectArtifact</code> with the relevent identifier from the store.
     * 
     * @param id Identifier of the <code>ObjectArtifact</code> to retrieve.
     * @return The <code>ObjectArtifact</code> represented by the identifier.
     * @throws ObjectStoreException
     */
    public ObjectArtifact getObjectArtifact(String id) throws ObjectStoreException;
}
