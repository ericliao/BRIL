/*
 * Created on 24 Aug 2007
 */
package uk.ac.kcl.cerch.soapi.objectstore;

/**
 * An <code>ObjectArtifact</code> that represents a RDF Statement. 
 *  
 * @author Vijay N Albuquerque
 *
 */
@SuppressWarnings("serial")
public class RDFStatement extends ObjectArtifact {
    private String rdfStatement;

    /**
     * Gets the RDF Statement <code>String</code>.
     * 
     * @return RDF Statement <code>String</code>.
     */
    public String getRdfStatement() {
        return rdfStatement;
    }

    /**
     * Sets the RDF Statement <code>String</code>.
     * 
     * @param RDF Statement <code>String</code>.
     */
    public void setRdfStatement(String rdfStatement) {
        this.rdfStatement = rdfStatement;
    }
}
