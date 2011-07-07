package uk.ac.kcl.cerch.bril.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Vector;

import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;

import uk.ac.kcl.cerch.bril.common.fedora.BrilObjectRepositoryException;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraHandler;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraNamespaceContext.FedoraNamespace;
import junit.framework.TestCase;

public class FedoraHandlerTest extends TestCase{

	public void testPurgeObject(){
		try {
			Vector<String> ids= new Vector<String>();
			ids.add("bril:23d71dea-f9bc-48f8-a35b-bf464d73c32a");
			ids.add("bril:dd677cf4-530c-4cb5-9a44-b14ae585d964");
			ids.add("bril:6e33f9b5-3eee-4aa7-a883-628120928a71");
			ids.add("bril:483bd1ce-b956-4dcc-a257-e9adbfa1b297");
			ids.add("bril:61da4241-348c-47bc-895d-5a53f5372e79");
			ids.add("bril:43e761a7-3af9-409c-9f5f-e88702a42a71");
			ids.add("bril:2f39ed7d-d4fa-49d9-8983-bd8c3ee5a770");
			ids.add("bril:3e1bc9f6-8c49-497f-9413-5da61f516365");
			ids.add("bril:598b1a61-813f-4ca6-a505-f155a0cb5175");
			ids.add("bril:1b775b7f-d4aa-4ad4-b50b-03fb4e1c4d83");
			
			for (int i=0;i<ids.size();i++){
			String identifier =ids.get(i);
			String logmessage ="It was just a test object";
			boolean force= false;
			FedoraHandler handler = new FedoraHandler();
			try {
				handler.purgeObject(identifier, logmessage, force);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			
		} catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void testIngest(){

	}
	
/*	public void testAddRelationship(){
		
		//Adding relationship to phenixDEF file isPartOf experiment
		String fedoraURI= FedoraNamespace.FEDORA.getURI();
		String namespaceURI_predicate = FedoraNamespace.BRILRELS.getURI();
		
		String pid =fedoraURI+"bril:7a535e8f-169f-4cec-a177-3b631ada7822";
		String predicate =namespaceURI_predicate+"isPartOf";
		String object = fedoraURI+"bril:expaa4ca950-a82f-49a7-93e9-59641de0ddb8";
	
		FedoraHandler handler;

			try {
				handler = new FedoraHandler();
				handler.addRelationship(pid, predicate, object, false, "");
			} catch (BrilObjectRepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}*/
	
}
