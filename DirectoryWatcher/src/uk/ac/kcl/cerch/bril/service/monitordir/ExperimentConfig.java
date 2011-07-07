package uk.ac.kcl.cerch.bril.service.monitordir;

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;


public class ExperimentConfig {
	Logger log = Logger.getLogger(ExperimentConfig.class);

	//URL _configURL = getClass().getResource("experiments.xml");
	static XMLConfiguration _config;
	private int numberOfExperiment;
	Collection experimentIdCollection;
	
	public ExperimentConfig() throws ConfigurationException{

		try
        {			
            //log.trace( String.format( "Creating config XMLConfiguration object from '%s'", _configURL ) );
            _config = new XMLConfiguration("config/experiments.xml");
       //     _config.setAutoSave(true);
        }
        catch ( ConfigurationException e )
        {
            log.fatal( "ConfigurationException caught in class Config:" );
            log.fatal( e.getStackTrace().toString() );
            throw e;
        }
	}
	
	
	/**
	 * Return true if one or more experiments are present in the config file
	 * False if no experiment is present.
	 * 
	 * @return 
	 */
	public boolean hasSavedExperiments(){
		Boolean hasExpt=false;
		Object ret = _config.getProperty("experiments.message_parameter.experimentId");
		if(ret!=null){
			hasExpt=true;
		}
		return hasExpt;
	}
	
	/**
	 * @return  true if more then one experiments are present in the xml file
	 */
	public boolean hasCollectionOfExperiments() {
		Boolean isCollection=false;
		Object prop = _config.getProperty("experiments.message_parameter.experimentId");
		
		//more then one experiment is present
		if(prop instanceof Collection){
			System.out.println("Number of experiment: " + ((Collection) prop).size());

			isCollection=true;
			numberOfExperiment  =((Collection)prop).size();
			experimentIdCollection =((Collection)prop);
		}
	
        return isCollection;
	}
	
	/**
	 * 
	 * @return true if one only experiment is present in the xml file else false
	 */
	public boolean hasOneExperiment(){
		Boolean hasOneExperiment=false;
		Object prop = _config.getProperty("experiments.message_parameter.experimentId");
		
		if(prop instanceof String){
			System.out.println("Has one experiment:"+ prop);
			hasOneExperiment=true;
		}
		
		return hasOneExperiment;
		
	}

	private Collection getAllStatus(){
		Collection statusCollection=null;
		Object ret = _config.getProperty("experiments.message_parameter.status");
		if(ret instanceof Collection){
			statusCollection =((Collection)ret);
		}
		return statusCollection;
		
	}
	
	public int getNumberOfExperiment(){
		//hasCollectionOfExperiments();
		return numberOfExperiment;
	}
	
	public String getExperimentParameter(String experimentElement){
		String ret = _config.getString( "experiments.message_parameter."+experimentElement );
		return ret;
	}
	
	public String getExperimentId(){
		String ret = _config.getString( "experiments.message_parameter.experimentId" );
		return ret;
	}
	
	public String getStatus(){
		String ret = _config.getString( "experiments.message_parameter.status" );
		return ret;
	}

	
	public void addExperiment(String key, String value){
		_config.addProperty(key, value);
		try {
			_config.save();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * The last saved experiment whose status is active will be returned 
	 * @return experiment Id
	 */
	public String getActiveExperimentId(){
		String retId=null;
		Collection statusCollection=null;
		Object ret = _config.getProperty("experiments.message_parameter.status");
		if(ret instanceof Collection ){
			
			statusCollection =((Collection)ret);
		}
	
		//System.out.println(ret);
		if(hasCollectionOfExperiments()==true && statusCollection!=null){
			// statusCollection = getAllStatus();
			//Iterator iter = statusCollection.iterator();
			int countStatus =0;
			for(Iterator iter = statusCollection.iterator(); iter.hasNext();){
				
				String status = (String) iter.next();
				System.out.println(status);	
				if (status.equals("active")){
				//	
					retId = (String) _config.getProperty( "experiments.message_parameter("+countStatus+").experimentId");
					/*Object []eid = experimentIdCollection.toArray();
					System.out.println("id.: "+eid[countStatus].toString());
					retId =eid[countStatus].toString();
*/
				}
				countStatus++;
			}
		
		}else 
			if(hasOneExperiment()==true){
			if(getStatus().equals("active")){
				retId=getExperimentId();
			}else{
				log.info("The status of experimentid: "+getExperimentId()+ "is set to 'inactive'");
			}
		}
		
		return retId;
	}
	//TODO
	//to change the status of the existing experimetn to inactive when a new experiment is created.
	public void changeAllStatusToInactive(){
		Collection statusCollection= getAllStatus();
		for(Iterator iter = statusCollection.iterator(); iter.hasNext();){
			String status = (String) iter.next();
			
		}
	}
	
	public void setExperimentStatus(String experimentId,String status) throws ConfigurationException{
		//search for the experiment of this is
		if(hasCollectionOfExperiments()==true){
			Collection statusCollection = getAllStatus();
			//Iterator iter = statusCollection.iterator();
			
			int countId=0;
			for(Iterator iter = experimentIdCollection.iterator(); iter.hasNext();){
				
				String id = (String) iter.next();
				if (id.equals(experimentId)){
				//change the status of this id	
				String currentStatus =(String) _config.getProperty( "experiments.message_parameter("+countId+").status");
				//	System.out.println("idstatus: "+_config.getProperty( "experiments.message_parameter("+countId+").status"));
				_config.setProperty("experiments.message_parameter("+countId+").status", status);
				_config.save();
				System.out.println("status setproperty to: "+ status);
				}
				countId++;
			}
		
		}
		else if(hasOneExperiment()==true){
			
			//Object prop = _config.getProperty("experiments.message_parameter.experimentId");
			String currentStatus =(String) _config.getProperty( "experiments.message_parameter.status");
			System.out.println("current status : "+ currentStatus);
			
			_config.setProperty("experiments.message_parameter.status", status);
			_config.save();
			System.out.println("new status: "+ status);
			
					
		}
		
	}
	public void loadAnExperiment(String experimentId){
		if(hasCollectionOfExperiments()==true){
			int countId=0;
			for(Iterator iter = experimentIdCollection.iterator(); iter.hasNext();){
				String id = (String) iter.next();
				if (id.equals(experimentId)){
					String status =(String) _config.getProperty( "experiments.message_parameter("+countId+").status");
				}
			
			}
		}
	}
	public String getElementValue(String elementName,int countId){
		String elementvalue =(String) _config.getProperty( "experiments.message_parameter("+countId+")."+elementName);
		return elementvalue;
	}

	
	
}
