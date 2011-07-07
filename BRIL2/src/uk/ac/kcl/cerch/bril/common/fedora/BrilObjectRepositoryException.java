package uk.ac.kcl.cerch.bril.common.fedora;

import uk.ac.kcl.cerch.bril.common.types.BrilException;

public class BrilObjectRepositoryException extends BrilException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructs an instance of <code>BrilObjectRepositoryException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public BrilObjectRepositoryException(String msg) {
        super(msg);
    }

    public BrilObjectRepositoryException( String msg, Exception exception )
    {
        super( msg, exception );
    }

    public BrilObjectRepositoryException( Exception exception )
    {
        super( exception );
    }

}
