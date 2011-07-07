package uk.ac.kcl.cerch.bril.test;

import java.io.IOException;
import java.rmi.RemoteException;


import uk.ac.kcl.cerch.bril.common.fedora.BrilObjectRepositoryException;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraHandler;


public class PurgeFedoraObjectTest{
	public static void main(String args[])throws IOException, BrilObjectRepositoryException{
		String errorMSG=" Pass a pid of an object to be purged. \n " +
				"For example:- bril:f5097062-4b67-4bc7-a856-cad75f35c5ad";
		
		if (args.length > 0 && args.length< 2) {
			String identifier = args[0];
			String logmessage ="It was just a test object";
			boolean force= false;
			FedoraHandler handler = new FedoraHandler();
			try {
				handler.purgeObject(identifier, logmessage, force);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			System.out.println(errorMSG);
			te();
		}
		
			
}
	private static void te(){
		String s="C:\\BRIL\\experiment\\baa5d5\\h2-2_MS_3_002.img.bz2";
		boolean compressedFile=false;
		int pos =s.lastIndexOf('.');
		
		if(s.substring(pos+1).equals("bz2")){
			compressedFile=true;

		}
		System.out.println(compressedFile);
	}

}
