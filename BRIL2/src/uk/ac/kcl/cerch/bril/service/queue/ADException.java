package uk.ac.kcl.cerch.bril.service.queue;

public class ADException extends Exception{
	private static final long serialVersionUID = -7559129949031119879L;

    public ADException(String message) {
        super(message);
    }
    
    public ADException(Throwable cause) {
        super(cause);
    }
    
    public ADException(String message, Throwable cause) {
        super(message, cause);
   }
}
