package uk.ac.kcl.cerch.bril.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import uk.ac.kcl.cerch.bril.common.fedora.BrilObjectRepositoryException;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraHandler;
import uk.ac.kcl.cerch.bril.service.queue.ADException;
import uk.ac.kcl.cerch.bril.service.queue.ADMessageListener;

public class ITQLQueryTest {
	public static void main(String arg[]){
		String expid="expa416b0b2-de69-471c-bd63-fcb64dc15a28";
		String path ="C:/BRIL/experiment/baa5d5/high.mtz";
		
		//locally on laptop works
	//	String expid="expaa4ca950-a82f-49a7-93e9-59641de0ddb8";
	//	String path="C:/Experiment/baa5d5/scala1.mtz";
		ADMessageListener l;
		
		try {
			l = new ADMessageListener();
			l.searchInRepositoryAndPurge(expid,path);
		} catch (ADException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("----------- Run here -----------------");
		ITQLQueryTest t = new ITQLQueryTest();
		
		String res= t.getIdentifierWithTheTitle(expid,path);
		System.out.println("---------------- result ----------------");
		System.out.println(res);
		
	}
	private String getIdentifierWithTheTitle(String expId, String title){
		System.out.println(title);
		String title1 = replaceSlashes(title);
		String result=null;
		FedoraHandler fedoraHandler;
		String risearchURL=null;
		try {
			fedoraHandler = new FedoraHandler();
		
	   	 risearchURL= fedoraHandler.getFedoraURL()+"/risearch";
	   	
		} catch (BrilObjectRepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		System.out.println(title1);

		String requestParameters1 = "query=select+%24object+from+%3C%23ri%3Ewhere+%24object+%3Cdc%3Atitle%3E+%27" +
		title1 +
		"%27and+%24object+%3Cfedora-rels-ext%3AisPartOf%3E+%3Cinfo%3Afedora%2Fbril%3A" +
		expId +
		"%3E&format=Sparql&type=tuples&lang=itql";
		
		String requestParameters = "query=select+%24object+%24title+from+%3C%23ri%"
			+"3Ewhere+%24object+%3Cdc%3Aformat%3E+%27mtzReflectionFile%27and+%24object+%3Cdc%3Atitle%3E+%"
			+"24title&format=Sparql&type=tuples&lang=itql";
		
		System.out.println(requestParameters);
  		
  		try
  		{
  		// Send data
  		
  		if (requestParameters != null && requestParameters.length () > 0)
  		{
  			risearchURL += "?" + requestParameters;
  		}
  		System.out.println(risearchURL);
  		URL url = new URL(risearchURL);
  		URLConnection conn = url.openConnection ();

  		// Get the response
  		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
  		StringBuffer sb = new StringBuffer();
  		String line;
  		while ((line = rd.readLine()) != null)
  		{
  		sb.append(line);
  		}
  		rd.close();
  		result =sb.toString();
  		//System.out.println(sb.toString());
  		} catch (Exception e)
  		{
  		e.printStackTrace();
  		}
		
		return result;
  		
	}
private String replaceSlashes(String path){
		
		char BACKSLASH_CHAR = '\\';
		
		int charCount =0;
        char lookFor='/';

		     for (int i = 0; i < path.length(); i++) {  
		         final char c = path.charAt(i);  
		         if (c == lookFor) {  
		        	 charCount++;  
		         }  
		     } 
		String nPath =path;
		if(charCount!=0){ 
		
			for (int i=0;i<charCount;i++){
				int pos =nPath.indexOf("/");
				
		       //replace with one back slash
		        StringBuffer buf = new StringBuffer(nPath);
		        buf.setCharAt( pos, BACKSLASH_CHAR );
		        String path1 = buf.toString( );
		        nPath = new StringBuffer(path1).insert(pos+1, BACKSLASH_CHAR).toString();
		        System.out.println("Way1 at add slash: "+ path1);
		        System.out.println("Way1 at add slash: "+ nPath);
		       
			}
			
		}
		  return nPath;
		}

}
