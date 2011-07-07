package uk.ac.kcl.cerch.bril.service.queue;

import uk.ac.kcl.cerch.bril.service.queue.ADException;



public interface ADQueueReader {
	
	//public void connect()throws ADException;
	public void startService()throws ADException;
	public void stopService() throws ADException;

}
