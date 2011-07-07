package uk.ac.kcl.cerch.bril.fileformat;

import uk.ac.kcl.cerch.bril.common.types.BrilException;

public class BrilFileFormatValidatorException extends BrilException{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
	     * Constructs an instance of <code></code> with the specified detail message.
	     * @param msg the detail message.
	     */
	    public BrilFileFormatValidatorException(String msg) {
	        super(msg);
	    }

	    public BrilFileFormatValidatorException( String msg, Exception exception )
	    {
	        super( msg, exception );
	    }

	    public BrilFileFormatValidatorException( Exception exception )
	    {
	        super( exception );
	    }

	
}
