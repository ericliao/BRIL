package uk.ac.kcl.cerch.soapi.sip;

import uk.ac.kcl.cerch.soapi.SOAPIException;

@SuppressWarnings("serial")
public class SIPIdGeneratorException extends SOAPIException {
    public SIPIdGeneratorException() {
        
    }
    
    public SIPIdGeneratorException(String message) {
        super(message);
    }
    
    public SIPIdGeneratorException(Throwable exception) {
        super(exception);
    }
    
    public SIPIdGeneratorException(String message, Throwable exception) {
        super(message, exception);
    }
}
