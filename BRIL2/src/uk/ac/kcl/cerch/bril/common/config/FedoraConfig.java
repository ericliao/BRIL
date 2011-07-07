package uk.ac.kcl.cerch.bril.common.config;

import org.apache.commons.configuration.ConfigurationException;


/**
 * Sub class of Config providing access to the configuration file.
 */
public class FedoraConfig extends uk.ac.kcl.cerch.bril.common.config.Config {

	public FedoraConfig() throws ConfigurationException{
		super();
	}

	public static String getHost() throws ConfigurationException{
		FedoraConfig fc = new FedoraConfig();          
	    return fc.getFedoraHost();
	
	}
	
	/**
	 * HOST
	 */
	private String getFedoraHost() {
		String ret = _config.getString( "fedora.host" );
        return ret;
	}


	public static String getPort() throws ConfigurationException{
		FedoraConfig fc = new  FedoraConfig();
        return fc.getFedoraPort();
	}

	/**
	 * PORT
	 */
	private String getFedoraPort() {
		  String ret = _config.getString( "fedora.port" );
		 
	        return ret;
	}


	public static String getUser() throws ConfigurationException{
		FedoraConfig fc = new  FedoraConfig();
        return fc.getFedoraUser();
	}

	/**
	 * USER
	 */
	private String getFedoraUser() {
		  String ret = _config.getString( "fedora.user" );
	        return ret;
	}

	
	public static String getPassPhrase() throws ConfigurationException{
		   FedoraConfig fc = new FedoraConfig();
	        return fc.getFedoraPassPhrase();
     }

	/**
	 * PASS PHRASE
	 */
	private String getFedoraPassPhrase() {

        String ret = _config.getString( "fedora.passphrase" );
        return ret;
	}
	
public static void main(String arg[]){
	try {
		System.out.println(FedoraConfig.getHost());
		System.out.println(FedoraConfig.getPort());
		System.out.println(FedoraConfig.getUser());
		System.out.println(FedoraConfig.getPassPhrase());
		
	} catch (ConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}
}