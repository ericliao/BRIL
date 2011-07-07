/*
 * Created on 14 Aug 2007
 */
package uk.ac.kcl.cerch.soapi.objectstore;

/**
 * Generates unique identifiers for <code>ObjectArtifact</code>s.
 * The default implementation could use an incremental sequence of numbers for the identifiers. 
 *   
 */
public interface ObjectArtifactIdGenerator {
    public String generateId() throws ObjectArtifactIdGeneratorException;
}