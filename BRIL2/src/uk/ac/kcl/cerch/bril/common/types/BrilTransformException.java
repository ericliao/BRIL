package uk.ac.kcl.cerch.bril.common.types;

public class BrilTransformException extends BrilException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructs an instance of <code>OpenSearchTransformException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public BrilTransformException(String msg) {
        super(msg);
    }

    public BrilTransformException( String msg, Exception exception )
    {
        super( msg, exception );
    }

    public BrilTransformException( Exception exception )
    {
        super( exception );
    }

}
