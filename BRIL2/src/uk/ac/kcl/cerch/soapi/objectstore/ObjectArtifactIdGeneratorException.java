package uk.ac.kcl.cerch.soapi.objectstore;

import uk.ac.kcl.cerch.soapi.SOAPIException;

@SuppressWarnings("serial")
public class ObjectArtifactIdGeneratorException extends SOAPIException {
    
    /**
     *  Default constructor.
     *  
     */
    public ObjectArtifactIdGeneratorException() {
        
    }
    
    /**
     * Constructor that accepts an exception message.
     * 
     * @param message The exception message <code>String</code>.
     */
    public ObjectArtifactIdGeneratorException(String message) {
        super(message);
    }
    
    /**
     * Constructor that accepts an <code>Exception</code> object.
     *  
     * @param exception The <code>Exception</code> object to be wrapped.
     */
    public ObjectArtifactIdGeneratorException(Throwable exception) {
        super(exception);
    }
    
    /**
     * Constructor that accepts a <code>String</code> and a <code>Throwable</code> object
     * 
     * @param message The exception message <code>String</code>.
     * @param exception The <code>Throwable</code> object to be wrapped.
     */
    public ObjectArtifactIdGeneratorException(String message, Throwable exception) {
        super(message, exception);
    }
}
