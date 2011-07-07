/*
 * Created on 15 Aug 2007
 */
package uk.ac.kcl.cerch.soapi.objectstore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;

/**
 * <code>FileSystemObjectStore</code> acts as a store for <code>ObjectArtifact</code>s. It is an implementation
 * of <code>ObjectStore</code> that uses the underlying operating system's file system as the storage provider. 
 * 
 * @author Vijay N Albuquerque
 *
 */

public class FileSystemObjectStore implements ObjectStore {
    
    private ObjectArtifactIdGenerator objectArtifactIdGenerator;
    private Properties properties;
    
    public FileSystemObjectStore()
    {
        properties = new Properties();
    }
    
    /**
     * Gets the <code>ObjectArtifact</code> with the relevent identifier from the store.
     * 
     * @param id Identifier of the <code>ObjectArtifact</code> to retrieve.
     * @return The <code>ObjectArtifact</code> represented by the identifier.
     * @throws ObjectStoreException
     */
    public ObjectArtifact getObjectArtifact(String id)
    throws ObjectStoreException {
        ObjectArtifact objectArtifact = null;

        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("soapi.properties"));
            String objectStoreDirectory = properties.getProperty("objectStoreDirectory");
           
            String location = objectStoreDirectory + "/" + id + ".data";
            FileInputStream in = new FileInputStream(location);
            
            ObjectInputStream objectIn = new ObjectInputStream(in);

            objectArtifact = (ObjectArtifact) objectIn.readObject();
        }
        catch(ClassNotFoundException e) {
            throw new ObjectStoreException(e);
        }
        catch(FileNotFoundException e) {
            throw new ObjectStoreException(e);
        }
        catch(IOException e) {
            throw new ObjectStoreException(e);
        }

        return objectArtifact;
    }

    /**
     * Puts the <code>ObjectArtifact</code> in the store and returns a unique identifier
     * for the stored object.
     * 
     * @param objectArtifact <code>ObjectArtifact</code> to be stored.
     * @return Unique identifier for the stored <code>ObjectArtifact</code>.
     * @throws ObjectStoreException
     */
    public String putObjectArtifact(ObjectArtifact objectArtifact)
    throws ObjectStoreException {
        String objectArtifactId = null;
        try 
        {
            properties.load(getClass().getClassLoader().getResourceAsStream("soapi.properties"));
            String objectStoreDirectory = properties.getProperty("objectStoreDirectory");
            if( objectArtifact.getId() != null)
            {
                objectArtifactId = objectArtifact.getId();
                File file = new File(objectStoreDirectory + "/" + objectArtifactId + ".data");
                file.delete();
            }
            else
            {
                objectArtifactId = objectArtifactIdGenerator.generateId();
            }

            FileOutputStream out = new FileOutputStream(objectStoreDirectory + "/" + objectArtifactId + ".data");

            ObjectOutputStream objectOut = new ObjectOutputStream(out);
            // Set the id of the ObjectArtifact before storing it in the Object Store
            objectArtifact.setId(objectArtifactId);
            objectOut.writeObject(objectArtifact);
            out.close();
        } 
        catch(ObjectArtifactIdGeneratorException e) {
            throw new ObjectStoreException(e);
        }
        catch(FileNotFoundException e) {
            throw new ObjectStoreException(e);
        } 
        catch(IOException e) {
            throw new ObjectStoreException(e);
        }
        return objectArtifactId;
    }

    /**
     * Sets the <code>ObjectArtifactIdGenerator</code> to be used while generating identifiers
     * on saving <code>ObjectArtifact</code>s. 
     * 
     * @param objectArtifactIdGenerator <code>ObjectArtifactIdGenerator</code> to be used by the <code>ObjectStore</code>.
     */
    public void setObjectArtifactIdGenerator(ObjectArtifactIdGenerator objectArtifactIdGenerator) {
        this.objectArtifactIdGenerator = objectArtifactIdGenerator;
    }

    public ObjectArtifactIdGenerator getObjectArtifactIdGenerator() {
        return objectArtifactIdGenerator;
    }
}
