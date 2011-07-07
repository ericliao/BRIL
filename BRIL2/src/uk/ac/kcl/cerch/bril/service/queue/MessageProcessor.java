package uk.ac.kcl.cerch.bril.service.queue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import javax.jms.JMSException;

//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;

//import test.spring.testbean.MySpringBeanWithDependency;
//import uk.ac.kcl.cerch.bril.service.queue.ADException;
import uk.ac.kcl.cerch.bril.common.util.FileUtil;
//import uk.ac.kcl.cerch.bril.contentstructure.ExperimentTypeValidation;
//import uk.ac.kcl.cerch.bril.contentstructure.ExperimentTypeValidatorImpl;
//import uk.ac.kcl.cerch.bril.contentstructure.CrystallographyExperiment;
//import uk.ac.kcl.cerch.bril.contentstructure.Experiment;
//import uk.ac.kcl.cerch.bril.dependency.SIPDataProcessorDependencyInjection;
//import uk.ac.kcl.cerch.bril.filetype.CrystallographyFileIdentifier; //import uk.ac.kcl.cerch.bril.filetype.FileTypeIdentifierImpl;
import uk.ac.kcl.cerch.bril.service.queue.MessageMetadata;
import uk.ac.kcl.cerch.bril.service.upload.FileServerClient;
import uk.ac.kcl.cerch.bril.service.uuid.IDGenerator;
import uk.ac.kcl.cerch.bril.sip.BrilSIP;
import uk.ac.kcl.cerch.bril.sip.processor.BrilSIPProcessor;
import uk.ac.kcl.cerch.soapi.checksum.ChecksumProcessorException;
import uk.ac.kcl.cerch.soapi.checksum.MD5ChecksumProcessor;
//import uk.ac.kcl.cerch.soapi.sip.SIP;
import uk.ac.kcl.cerch.soapi.sip.processor.SIPProcessorException;

public class MessageProcessor {

	private Properties propertiesXML;
	private Properties properties;
	private Properties experimentTypesProperties;
	private String brilTmpStore;
	private InputStream inputStream;
	private MessageMetadata metadata;
	private String filePath;
	private Map <String, String> tempObjectMap;
	private String ObjectIdentifier;

	public MessageProcessor() {
		
		try {		
			propertiesXML = new Properties();
			propertiesXML.load(getClass().getClassLoader().getResourceAsStream(
					"messagexml.properties"));

			properties = new Properties();
			properties.load(getClass().getClassLoader().getResourceAsStream(
					"bril.properties"));
			

			experimentTypesProperties = new Properties();
			experimentTypesProperties.load(getClass().getClassLoader()
					.getResourceAsStream("domain.properties"));

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Saves the inputstream to a tmp path with directory name 
	 * as the experimentId. 
	 * Sets the BrilSIP with this file path and the message XML string 
	 * 
	 * @param input
	 * @param msgMetadataXML
	 */
	public boolean processMessage(String tmpPath, String msgMetadataXML, String identifier) {
		System.out.println("----------START: MessageProcessor--------- ");
		
		boolean flag =false;
		metadata= new MessageMetadata(msgMetadataXML);
		//inputStream= input;
		if(identifier!=null){
		ObjectIdentifier= identifier;
		}else{
			ObjectIdentifier="bril:" + IDGenerator.generateUUID();
		}
		
		//filePath =putFileInDirectory();
		/* Generate checksum for this file in the tmp filePath 
		 * and match this with the one in the metadata
		 */
		MD5ChecksumProcessor c = new MD5ChecksumProcessor();
		String checksum=null;
		try{
			checksum =c.generateChecksum(new File(tmpPath));
		} catch (ChecksumProcessorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("checksum file in tmp directory: "+checksum);
		//System.out.println("original checksum: "+metadata.getChecksum());
		
	if(!checksum.equals(metadata.getChecksum())){
		flag=false;
	
			System.out.println("The original checksum " +
					"'"+metadata.getChecksum()+"' does not match the file in the tmp brilstore '"+checksum+"'");
			System.out.println("The inputstream is empty or file upload/copy to temp directory incomplete??");
			
			BrilSIP brilSIP= new BrilSIP(tmpPath);
			brilSIP.setMetadataXMLString(msgMetadataXML);
			//ArchivalObject id same as fedora object id
			brilSIP.setIdentifier(ObjectIdentifier);
			//sip id
			brilSIP.setId("sip:" + System.currentTimeMillis());
			BrilSIPProcessor pro = new BrilSIPProcessor(flag);
			try 
			{
				pro.processSIP(brilSIP);				
				pro.setChecksumMatchFlag(flag);
			} catch (SIPProcessorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
	}
	if(checksum.equals(metadata.getChecksum())){
		flag=true;
		System.out.println("Original checksum matches with the uploaded file in the temp brilstore");
		BrilSIP brilSIP= new BrilSIP(tmpPath);
		brilSIP.setMetadataXMLString(msgMetadataXML);
		//ArchivalObject id same as fedora object id
		brilSIP.setIdentifier(ObjectIdentifier);
		//sip id
		brilSIP.setId("sip:" + System.currentTimeMillis());
		BrilSIPProcessor pro = new BrilSIPProcessor(flag);
		try 
		{
			pro.processSIP(brilSIP);
			flag = pro.getCompletionFlag();
		} catch (SIPProcessorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		flag=true;
	}
	System.out.println("----------END: MessageProcessor--------- ");
		/*
		 * Delete the file from the server
		 */
		//String filename = filePath.substring(filePath.lastIndexOf("\\")+1);		
		//System.out.println("filename: ----- " + filename);
		
		//deleteFromFileserver(filename);
		
		/*ExperimentTypeValidatorImpl validator = new ExperimentTypeValidatorImpl();
		ExperimentTypeValidation validation = validator
				.validateClassificationType(metadata.getExperimentType());
		System.out.println("Valid experiment type: " + validation.getResult());*/
 
		//if (validation.getResult() == true) {
			
			//  ApplicationContext context = new ClassPathXmlApplicationContext(
			 // "META-INF/beans.xml"); // BeanFactory factory = context;
			//  MySpringBeanWithDependency test = (MySpringBeanWithDependency)
			//  context .getBean("mySpringBeanWithDependency"); test.run();
			 

		/*	ApplicationContext context = new ClassPathXmlApplicationContext(
					"WEB-INF/beans.xml");*/
		    //BeanFactory factory = (BeanFactory) context;
			/*ExperimentDependencyInjection edi = (ExperimentDependencyInjection) context
					.getBean("experimentTypeDependency");
			edi.run(inputStream, msgMetadataXML);*/
			
			/*SIPDataProcessorDependencyInjection dpdi =(SIPDataProcessorDependencyInjection)context
			.getBean("sipProcessorDependencyInjection");
			dpdi.run(brilSIP);*/
		//}
	return flag;
	}

	public String getTmpFilePath(){
		return this.filePath;
	}
	/*Put the file in the directory with the name as experimentId.
	 * 
	 */
	private String putFileInDirectory() {
		String digitalObjectFileName = FileUtil.getFileName(metadata.getIdPath());
		System.out.println("---------- Write file to a tmp directory filename----- "
				+ digitalObjectFileName);
		filePath = writeStreamInExperimentDirectory(digitalObjectFileName, metadata.getExperimentId());
		
		System.out.println("tmppath: ----- " + filePath);
		
		return filePath;
		
	}
	
	private void deleteFromFileserver(String filename){
	
		String fileserverURL = properties.getProperty("file.server.url");
		try {
			String surl = fileserverURL+filename;
			URL url = new URL(surl);
			FileServerClient client = new FileServerClient(url);
			try {
				client.remove();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/* Write file to tmp directory called "brilstore/expeitmentId" 
	 * */
	private String writeStreamInExperimentDirectory(String fileName, String experimentId) {
		String fileTmpPath = "";
		brilTmpStore= properties.getProperty("tmp.file.store");
		try {
			if(experimentId.contains(":")){
				experimentId=experimentId.replace(":", "-");
			}
			String expDirectory = brilTmpStore + "\\" + experimentId;
			fileTmpPath= expDirectory+ "\\" +fileName;
		
			boolean existsDirectory = (new File(expDirectory)).exists();
			
			if (existsDirectory == false) {
				FileUtil.createDirectory(expDirectory);
			}
			OutputStream outputStream = new FileOutputStream(fileTmpPath);

			// write the file on to activeMQ
			byte[] buffer = new byte[1024];
			while (true) {

				int bytesRead = inputStream.read(buffer);
				if (bytesRead == -1) {
					break;
				}
				// System.out.println("write start");
				outputStream.write(buffer, 0, bytesRead);
			}
			// close the stream so the receiving side knows the stream is
			// finished
			outputStream.close();
		

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileTmpPath;
	}


}
