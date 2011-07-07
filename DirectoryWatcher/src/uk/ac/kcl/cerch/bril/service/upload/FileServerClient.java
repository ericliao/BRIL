package uk.ac.kcl.cerch.bril.service.upload;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.mortbay.util.IO;

//import sun.net.www.http.HttpCaptureInputStream;
import uk.ac.kcl.cerch.bril.common.util.FileUtil;

public class FileServerClient {
	URL url;
	
	public FileServerClient(URL url) {
		this.url = url;
		

	}

	public boolean upload(byte[] fileContents) throws IOException {
		System.out.println("start upload");
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("PUT");
		
		connection.setDoOutput(true);
		connection.setChunkedStreamingMode(fileContents.length);
		System.out.println("content length:" + fileContents.length);
		OutputStream os = connection.getOutputStream();
		IO.copy(new ByteArrayInputStream(fileContents), os);
		
		os.close();
		boolean b = isSuccessfulCode(connection.getResponseCode());
		System.out.println("upload status:" + connection.getResponseCode());
		System.out.println("successful upload:" + b);
		connection.disconnect();
		return b;
	}

	public boolean remove() throws IOException {
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		
		connection.setRequestMethod("DELETE");
		InputStream is = connection.getInputStream();
		is.close();
		
		connection.disconnect();
		
		boolean b = isSuccessfulCode(connection.getResponseCode());
		System.out.println("sussessful:" + b);
		return b;
	}
	
	public URL getURL(){
		return url;
	}

	private boolean isSuccessfulCode(int responseCode) {
		return responseCode >= 200 && responseCode < 300; // 2xx => successful
	}

	public static void main(String arg[]) {
		//File file = new File("c:/Experiment/h2-2_MS_1_001.bz2");
		File file = new File("c:/Experiment/baa5d5/aa.com");
		URL url = null;
		try {
			byte[] fileContents = FileUtil.getBytesFromFile(file);
			url = new URL("http://localhost:8161/fileserver/aa.com");
			FileServerClient client = new FileServerClient(url);
			
			//boolean suc= client.upload(fileContents);
			//if(suc==false){
				
				client.remove();
				//url = new URL("http://localhost:8161/fileserver/aa.com");
				//client = new FileServerClient(url);
				//client.upload(fileContents);
			//}
			/*try{
			client.remove();
			}catch(Exception io){
				System.out.println("file does not exist: " +io);
			}*/
			
			//URL url1 = new URL("http://localhost:8161/fileserver/exp2.txt");
			//FileServerClient client1 = new FileServerClient(url1);
			
			//client1.upload(fileContents);
	        //client.remove();

		} catch (IOException e) {
			System.out.println("file does not exist: " +e);
			e.printStackTrace();
		}

	}
}
