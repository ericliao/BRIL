package uk.ac.kcl.cerch.bril.service.uuid;

import java.util.UUID;

public class IDGenerator {

	public IDGenerator(){
		
	}
	
	public static String generateUUID(){
		String pid =null;
		pid = UUID.randomUUID().toString();
		return pid;
	}
}
