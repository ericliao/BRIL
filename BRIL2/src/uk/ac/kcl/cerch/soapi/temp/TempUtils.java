/*
 * Created on 24 Aug 2007
 */
package uk.ac.kcl.cerch.soapi.temp;

import info.fedora.www.definitions._1._0.client.FedoraServiceWrapperAPI;

/**
 * Temporary method that should not be included in the release
 * 
 */
public class TempUtils {
    // TODO: DELETE THE METHOD BELOW WHEN THE IDS ARE COMING FROM FEDORA...
    private static long increment = 1;
    
    public static long getRandomId() {
        return System.currentTimeMillis() + increment++;
    }
    
    public static String getNextFedoraID() {
    	FedoraServiceWrapperAPI  fs = new FedoraServiceWrapperAPI();
		String pid="";
		try {
			pid = fs.getNextFedoraPid("fedoraAdmin", "fedoraAdmin");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return pid;
    }

}
