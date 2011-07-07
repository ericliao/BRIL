/*
 * Created on 3 Sep 2007
 */
package uk.ac.kcl.cerch.soapi.sip.processor;

import uk.ac.kcl.cerch.soapi.SOAPIException;

/**
 * Represents an exception occurred during a SIP Processing operation
 * 
 * @author Vijay N Albuquerque
 *
 */

@SuppressWarnings("serial")
public class SIPProcessorException extends SOAPIException {
    
    /**
     * Constructor that accepts an exception message.
     * 
     * @param message The exception message <code>String</code>.
     */
    public SIPProcessorException(String message) {
        super(message);
    }
    
    /**
     * Constructor that accepts an <code>Exception</code> object.
     *  
     * @param exception The <code>Exception</code> object to be wrapped.
     */
    public SIPProcessorException(Throwable exception) {
        super(exception);
    }
    
    /**
     * Constructor that accepts a <code>String</code> and a <code>Throwable</code> object
     * 
     * @param message The exception message <code>String</code>.
     * @param exception The <code>Throwable</code> object to be wrapped.
     */
    public SIPProcessorException(String message, Throwable exception) {
        super(message, exception);
    }
}
