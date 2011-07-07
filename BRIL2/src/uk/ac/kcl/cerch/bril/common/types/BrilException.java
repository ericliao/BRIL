package uk.ac.kcl.cerch.bril.common.types;

public abstract class BrilException extends Exception{

	    private Exception e;
	    private String msg;

	   
	    /**
	     * Constructor wrapping the original exception.
	     *
	     * Constructs the Exception with null as the message.
	     *
	     * @param e
	     *            The originating exception
	     */
	    public BrilException( Exception e )
	    {
	        this.e = e;
	        this.msg = null;
	    }

	   
	    /**
	     * Constructor with wrapped exception and message explaining the cause from
	     * the plugin point of view.
	     *
	     * @param msg
	     *            the Exception e
	     * @param e
	     *            The original Exception that was caught
	     */
	    public BrilException( String msg, Exception e )
	    {
	        this.msg = msg;
	        this.e = e;
	    }

	   
	    /**
	     * Constructor for creating an Exception that origins from a plugin
	     *
	     * @param msg
	     *            The reason for the throwing of the Exception
	     */
	    public BrilException( String msg )
	    {
	        this.msg = msg;
	        this.e = null;
	    }

	   
	    /**
	     * Returns the wrapped (original) exception that was caught inside the
	     * plugin. Returns null if the BrilException is the originating exception
	     *
	     * @return Exception the wrapped exception
	     */
	    public Exception getException()
	    {
	        return e;
	    }

	   
	    /**
	     * Returns the message that was given from the plugin at the cathing of the
	     * original exception. Returns null if no message was given at the time of
	     * the catch.
	     */
	    @Override
	    public String getMessage()
	    {
	        return msg;
	    }
	
}
