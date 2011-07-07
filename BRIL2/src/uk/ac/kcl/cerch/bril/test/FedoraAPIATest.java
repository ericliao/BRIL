package uk.ac.kcl.cerch.bril.test;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.xml.rpc.ServiceException;

import org.fcrepo.client.FedoraClient;
import org.fcrepo.server.access.FedoraAPIA;
import org.fcrepo.server.management.FedoraAPIM;

public class FedoraAPIATest {
	public static void main(String arg[]) throws IOException{
		//String pid = "bril:d84ccdca-22cb-4e0f-ac97-40cde62878ce";
		 String pid =  "bril:expa416b0b2-de69-471c-bd63-fcb64dc15a28";
		 FedoraAPIA _fapia;
		 FedoraAPIM _fapim;
		 FedoraClient _fc=null;
		 String host ="bril-dev.cerch.kcl.ac.uk";
		 String port="8080";
		 String user="fedoraAdmin";
		 String pass="fedoraAdmin";
		String fedora_base_url = String.format("http://%s:%s/fedora", host, port);
		try {
			_fc = new FedoraClient(fedora_base_url, user, pass);
		} catch (MalformedURLException ex) {
			String error = String.format(
					"Failed to obtain connection to fedora repository: %s", ex
							.getMessage());

	}
		try {
			_fapia = _fc.getAPIA();
			_fapim =_fc.getAPIM();
			byte[] res =_fapim.getObjectXML(pid);
			System.out.println(res.toString());
			
		} catch (ServiceException ex) {
			String error = String.format(
					"Failed to obtain connection to fedora repository: %s", ex
							.getMessage());

		}	
	}

}
