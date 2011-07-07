package uk.ac.kcl.cerch.bril.common.config;

import java.net.URL;
import org.apache.commons.lang.*;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

public class Config {
	Logger log = Logger.getLogger(Config.class);

	URL _configURL = getClass().getResource("/config.xml");
	static XMLConfiguration _config;

	/**
	 * Method providing access to the config file.
	 * @throws NestableException 
	 * 
	 *
	 */
	public Config() throws ConfigurationException{

		try
        {
            log.trace( String.format( "Creating config XMLConfiguration object from '%s'", _configURL ) );
            _config = new XMLConfiguration( _configURL );
        }
        catch ( ConfigurationException e )
        {
            log.fatal( "ConfigurationException caught in class Config:" );
            log.fatal( e.getStackTrace().toString() );
            throw e;
        }
	}
	

}
