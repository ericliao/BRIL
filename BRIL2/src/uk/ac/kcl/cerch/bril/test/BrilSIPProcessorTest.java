package uk.ac.kcl.cerch.bril.test;

import junit.framework.TestCase;

import uk.ac.kcl.cerch.bril.sip.BrilSIP;
import uk.ac.kcl.cerch.bril.sip.processor.BrilSIPProcessor;

//import org.springframework.orm.hibernate3.HibernateTemplate;
//import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
//import org.hibernate.dialect.MySQLDialect;
public class BrilSIPProcessorTest extends TestCase {

	public void testProcessSIP() throws Exception{
		
		String diffImage="<?xml version="+"\"1.0\""+"?>"+
		"<message_parameter>"
		        +"<id>C:"+"'/Experiment"+"'/baa5d5"+"'/images"+"'/h2-2_MS_3_162.img</id>"+
		        "<experimentId>expaa4ca950-a82f-49a7-93e9-59641de0ddb8</experimentId>" +
		        "<checksum>6e32cef44369e7ba455c765655ccac</checksum>"+
		        "<domain>Crystallography</domain>" +
				"<projectName>baa5d5</projectName>"+
		        "<entryType>ENTRY_CREATE</entryType>" +
		        "<dateTime>13/06/2008 14:50:08</dateTime>"
		+"</message_parameter>";
		String high="<?xml version="+"\"1.0\""+"?>"+
		"<message_parameter>"
		        +"<id>C:/Experiment/baa5d5/high.mtz</id>"+
		        "<experimentId>expaa4ca950-a82f-49a7-93e9-59641de0ddb8</experimentId>" +
		        "<checksum>6e32cef44369e7ba455c765655ccac</checksum>"+
		        "<domain>Crystallography</domain>" +
				"<projectName>baa5d5</projectName>"+
		        "<entryType>ENTRY_CREATE</entryType>" +
		        "<dateTime>13/06/2008 14:50:08</dateTime>"
		+"</message_parameter>";
		String sorted="<?xml version="+"\"1.0\""+"?>"+
		"<message_parameter>"
		        +"<id>C:"+"'/Experiment"+"'/baa5d5"+"'/sorted-high.mtz</id>"+
		        "<experimentId>expaa4ca950-a82f-49a7-93e9-59641de0ddb8</experimentId>" +
		        "<checksum>6e32cef44369e7ba455c765655ccac</checksum>"+
		        "<domain>Crystallography</domain>" +
				"<projectName>baa5d5</projectName>"+
		        "<entryType>ENTRY_CREATE</entryType>" +
		        "<dateTime>16/06/2008 12:18:35</dateTime>"
		+"</message_parameter>";
		
		String scala1="<?xml version="+"\"1.0\""+"?>"+
		"<message_parameter>"
		        +"<id>C:"+"'/Experiment"+"'/baa5d5"+"'/scala1.mtz</id>"+
		        "<experimentId>expaa4ca950-a82f-49a7-93e9-59641de0ddb8</experimentId>" +
		        "<checksum>6e32cef44369e7ba455c765655ccac</checksum>"+
		        "<domain>Crystallography</domain>" +
				"<projectName>baa5d5</projectName>"+
		        "<entryType>ENTRY_CREATE</entryType>" +
		        "<dateTime>16/06/2008 12:49:50</dateTime>"
		+"</message_parameter>";
		
		String fivedfive="<?xml version="+"\"1.0\""+"?>"+
		"<message_parameter>"
		        +"<id>C:"+"/Experiment"+"/baa5d5"+"/5d5.mtz</id>"+
		        "<experimentId>expaa4ca950-a82f-49a7-93e9-59641de0ddb8</experimentId>" +
		        "<checksum>6e32cef44369e7ba455c765655ccac</checksum>"+
		        "<domain>Crystallography</domain>" +
				"<projectName>baa5d5</projectName>"+
		        "<entryType>ENTRY_CREATE</entryType>" +
		        "<dateTime>16/06/2008 13:54:36</dateTime>"
		+"</message_parameter>";
		
		String metadataDatabaseDef="<?xml version="+"\"1.0\""+"?>"+
		"<message_parameter>"
		        +"<id>C:"+"'/Experiment"+"'/baa5d5"+"'/CCP4_DATABASE"+"'/database.def</id>"+
		        "<experimentId>expaa4ca950-a82f-49a7-93e9-59641de0ddb8</experimentId>" +
		        "<checksum>6e32cef465659e7ba455c765655ccac</checksum>"+
		        "<domain>Crystallography</domain>" +
				"<projectName>baa5d5</projectName>"+
		        "<entryType>ENTRY_CREATE</entryType>" +
		        "<dateTime>27/06/2008 12:14:37</dateTime>"
		+"</message_parameter>";
		
		String metadataDEF1="<?xml version="+"\"1.0\""+"?>"+
		"<message_parameter>"
		        +"<id>C:"+"'/Experiment"+"'/baa5d5"+"'/CCP4_DATABASE"+"'/3_chainsaw.def</id>"+
		        "<experimentId>expaa4ca950-a82f-49a7-93e9-59641de0ddb8</experimentId>" +
		        "<checksum>6e32cefdsdsdsba455c765655ccac</checksum>"+
		        "<domain>Crystallography</domain>" +
				"<projectName>baa5d5</projectName>"+
		        "<entryType>ENTRY_CREATE</entryType>" +
		        "<dateTime>17/06/2008 09:29:03</dateTime>"
		+"</message_parameter>";
	
		String metadataDiff="<?xml version="+"\"1.0\""+"?>"+
		"<message_parameter>"
		        +"<id>C:"+"'/Experiment"+"'/baa5d5"+"'/h2-2_MS_3_162.img.bz2</id>"+
		        "<experimentId>expaa4ca950-a82f-49a7-93e9-59641de0ddb8</experimentId>" +
		        "<checksum>6e32cef44369e7ba455c765655ccac</checksum>"+
		        "<domain>Crystallography</domain>" +
				"<projectName>baa5d5</projectName>"+
		        "<entryType>ENTRY_CREATE</entryType>" +
		        "<dateTime>16/06/2008 12:18:27</dateTime>"
		+"</message_parameter>";
		
		String freeMetadata="<?xml version="+"\"1.0\""+"?>"+
		"<message_parameter>"
		        +"<id>C:"+"'/Experiment"+"'/baa5d5"+"'/free.com</id>"+
		        "<experimentId>expaa4ca950-a82f-49a7-93e9-59641de0ddb8</experimentId>" +
		        "<checksum>6e32ce322269e7ba455c765655ccac</checksum>"+
		        "<domain>Crystallography</domain>" +
				"<projectName>baa5d5</projectName>"+
		        "<entryType>ENTRY_CREATE</entryType>" +
		        "<dateTime>16/06/2008 13:48:36</dateTime>"
		+"</message_parameter>";
		
		String sortedMetadata="<?xml version="+"\"1.0\""+"?>"+
		"<message_parameter>"
		        +"<id>C:"+"'/Experiment"+"'/baa5d5"+"'/sort.com</id>"+
		        "<experimentId>expaa4ca950-a82f-49a7-93e9-59641de0ddb8</experimentId>" +
		        "<checksum>6e32ce322269e7ba455c765655ccac</checksum>"+
		        "<domain>Crystallography</domain>" +
				"<projectName>baa5d5</projectName>"+
		        "<entryType>ENTRY_CREATE</entryType>" +
		        "<dateTime>16/06/2008 12:18:27</dateTime>"
		+"</message_parameter>";
		
		String scala1Metadata="<?xml version="+"\"1.0\""+"?>"+
		"<message_parameter>"
		        +"<id>C:"+"'/Experiment"+"'/baa5d5"+"'/scala1.com</id>"+
		        "<experimentId>expaa4ca950-a82f-49a7-93e9-59641de0ddb8</experimentId>" +
		        "<checksum>6e32yuytyuy7ba455c765655ccac</checksum>"+
		        "<domain>Crystallography</domain>" +
				"<projectName>baa5d5</projectName>"+
		        "<entryType>ENTRY_CREATE</entryType>" +
		        "<dateTime>16/06/2008 12:49:04</dateTime>"
		+"</message_parameter>";
		
		String pdb1Metadata="<?xml version="+"\"1.0\""+"?>"+
		"<message_parameter>"
		        +"<id>C:"+"'/Experiment"+"'/baa5d5"+"'/1YY8b.pdb</id>"+
		        "<experimentId>expaa4ca950-a82f-49a7-93e9-59641de0ddb8</experimentId>" +
		        "<checksum>6e32ce32227uj67367ba455c765655ccac</checksum>"+
		        "<domain>Crystallography</domain>" +
				"<projectName>baa5d5</projectName>"+
		        "<entryType>ENTRY_CREATE</entryType>" +
		        "<dateTime>17/06/2008 09:22:46</dateTime>"
		+"</message_parameter>";
		String pdb2Metadata="<?xml version="+"\"1.0\""+"?>"+
		"<message_parameter>"
		        +"<id>C:"+"'/Experiment"+"'/baa5d5"+"'/1YY8b_chainsaw1.pdb</id>"+
		        "<experimentId>expaa4ca950-a82f-49a7-93e9-59641de0ddb8</experimentId>" +
		        "<checksum>6e32ce32227uj67367ba455c765655ccac</checksum>"+
		        "<domain>Crystallography</domain>" +
				"<projectName>baa5d5</projectName>"+
		        "<entryType>ENTRY_CREATE</entryType>" +
		        "<dateTime>17/06/2008 09:29:03</dateTime>"
		+"</message_parameter>";
		
		String pdb3Metadata="<?xml version="+"\"1.0\""+"?>"+
		"<message_parameter>"
		        +"<id>C:"+"'/Experiment"+"/baa5d5"+"/phaser" + "/phaser-solution-new_chain_ids.pdb</id>"+
		        "<experimentId>expaa4ca950-a82f-49a7-93e9-59641de0ddb8</experimentId>" +
		        "<checksum>6e32ce32227uj67367ba455c765655ccac</checksum>"+
		        "<domain>Crystallography</domain>" +
				"<projectName>baa5d5</projectName>"+
		        "<entryType>ENTRY_CREATE</entryType>" +
		        "<dateTime>19/07/2008 15:31:50</dateTime>"
		+"</message_parameter>";
		
		String aln1Metadata="<?xml version="+"\"1.0\""+"?>"+
		"<message_parameter>"
		        +"<id>C:"+"'/Experiment"+"'/baa5d5"+"'/clustalw2-5d5heavy_1yy8.aln</id>"+
		        "<experimentId>expaa4ca950-a82f-49a7-93e9-59641de0ddb8</experimentId>" +
		        "<checksum>6e3ewuyew6ew667367ba455c765655ccac</checksum>"+
		        "<domain>Crystallography</domain>" +
				"<projectName>baa5d5</projectName>"+
		        "<entryType>ENTRY_CREATE</entryType>" +
		        "<dateTime>16/06/2008 15:38:50</dateTime>"
		+"</message_parameter>";
		String aln2Metadata="<?xml version="+"\"1.0\""+"?>"+
		"<message_parameter>"
		        +"<id>C:"+"'/Experiment"+"'/baa5d5"+"'/clustalw2-5d5light-2mcg.aln</id>"+
		        "<experimentId>expaa4ca950-a82f-49a7-93e9-59641de0ddb8</experimentId>" +
		        "<checksum>6e32ce32227uj67367ba4552255ccac</checksum>"+
		        "<domain>Crystallography</domain>" +
				"<projectName>baa5d5</projectName>"+
		        "<entryType>ENTRY_CREATE</entryType>" +
		        "<dateTime>16/06/2008 15:43:07</dateTime>"
		+"</message_parameter>";
		
		String phenixDEFMetadata="<?xml version="+"\"1.0\""+"?>"+
		"<message_parameter>"
		        +"<id>C:"+"'/Experiment"+"'/baa5d5"+"'/1"+"'/1ref.def</id>"+
		        "<experimentId>expaa4ca950-a82f-49a7-93e9-59641de0ddb8</experimentId>" +
		        "<checksum>6e32ce32227uj67367ba4552255ccac</checksum>"+
		        "<domain>Crystallography</domain>" +
				"<projectName>baa5d5</projectName>"+
		        "<entryType>ENTRY_CREATE</entryType>" +
		        "<dateTime>20/07/2008 10:37:10</dateTime>"
		+"</message_parameter>";
		
		String phenixMTZMetadata="<?xml version="+"\"1.0\""+"?>"+
		"<message_parameter>"
		        +"<id>C:"+"'/Experiment"+"'/baa5d5"+"'/1"+"'/1ref_001_map_coeffs.mtz</id>"+
		        "<experimentId>expaa4ca950-a82f-49a7-93e9-59641de0ddb8</experimentId>" +
		        "<checksum>6e32ce32227uj67367ba4552255ccac</checksum>"+
		        "<domain>Crystallography</domain>" +
				"<projectName>baa5d5</projectName>"+
		        "<entryType>ENTRY_CREATE</entryType>" +
		        "<dateTime>20/07/2008 12:00:06</dateTime>"
		+"</message_parameter>";
		
		String phenixPDBMetadata="<?xml version="+"\"1.0\""+"?>"+
		"<message_parameter>"
		        +"<id>C:"+"'/Experiment"+"'/baa5d5"+"'/1"+"'/1ref_001.pdb</id>"+
		        "<experimentId>expaa4ca950-a82f-49a7-93e9-59641de0ddb8</experimentId>" +
		        "<checksum>6e32ce32227uj67367ba4552255ccac</checksum>"+
		        "<domain>Crystallography</domain>" +
				"<projectName>baa5d5</projectName>"+
		        "<entryType>ENTRY_CREATE</entryType>" +
		        "<dateTime>20/07/2008 12:00:06</dateTime>"
		+"</message_parameter>";
		//correct dates for mtz and pdb from phenic are:  20/07/2008 11:00:06
		
		
		/*ApplicationContext applicationContext = new ClassPathXmlApplicationContext("bril.xml");
	    System.out.println("bril.xml");    
		SIPProcessor sipProcessor = (SIPProcessor) applicationContext.getBean("sipProcessor");
		*/
		
		String path1 ="C:\\brilstore\\00EXPT123\\h2-2_MS_1_005.img.bz2";
		String path2 ="C:\\brilstore\\00EXPT123\\images\\h2-2_MS_3_162.img";
		
		//ingest this in fedora
		String path3 ="C:\\brilstore\\00EXPT123\\mosflm1.mtz";
	
		//ingest this in fedora
		String path4 ="C:\\brilstore\\00EXPT123\\high.mtz";
	
		//ingested in fedora
		String path5 ="C:\\brilstore\\00EXPT123\\sorted-high.mtz";
	
		//ingested in fedora
		String path6 ="C:\\brilstore\\00EXPT123\\scala1.mtz";
	
		//This is already ingested fedora
		String path7 ="C:\\brilstore\\00EXPT123\\truncate.mtz";
		
		//Making MTZ relationship check for this
		String path8 ="C:\\brilstore\\00EXPT123\\5d5.mtz";
	
		//ingested
		String pathdef ="C:\\brilstore\\00EXPT123\\CCP4_DATABASE\\database.def";
		
		//ingested
		String pathdatabasedef ="C:\\brilstore\\00EXPT123\\CCP4_DATABASE\\database.def";
		
		//ingested
		String pathdef1 ="C:\\brilstore\\00EXPT123\\CCP4_DATABASE\\3_chainsaw.def";
		
		
		//ingest com file that produced 5d5.mtz
		String freeCom ="C:\\brilstore\\00EXPT123\\free.com";
		
		//ingest com file that produce sort-high.mtz
		String sortCom ="C:\\brilstore\\00EXPT123\\sort.com";
		
		//ingest com file that produce scala1.mtz
		String scala1Com ="C:\\brilstore\\00EXPT123\\scala1.com";
		
		//ingested pdb file that produced 1YY8b_chainsaw1.pdb
		String pdb1 ="C:\\brilstore\\00EXPT123\\1YY8b.pdb";
	
		//ingest in object store to be tested in relationshipgenerator of pdb
	    String pdb2 ="C:\\brilstore\\00EXPT123\\1YY8b_chainsaw1.pdb";
	    
	    String pdb3 ="C:\\brilstore\\00EXPT123\\phaser\\phaser-solution-new_chain_ids.pdb";
	    
	    String aln1 ="C:\\brilstore\\00EXPT123\\clustalw2-5d5heavy_1yy8.aln";
	    
	    String aln2 ="C:\\brilstore\\00EXPT123\\clustalw2-5d5light-2mcg.aln";
	    
	    String phenixdef ="C:\\brilstore\\00EXPT123\\1\\1ref.def";
	  
	    String phenixmtz = "C:\\brilstore\\00EXPT123\\1\\1ref_001_map_coeffs.mtz";
	    
	    String phenix_pdb ="C:\\brilstore\\00EXPT123\\1\\1ref_001.pdb";
		String testUploadedServer ="C:\\brilstore\\expe10d07ab-263a-4964-ba51-90ec1bca72d0\\5d5.mtz";
		String highmtz ="C:\\brilstore\\expaa4ca950-a82f-49a7-93e9-59641de0ddb8\\high.mtz";
	    BrilSIP sip = new BrilSIP(highmtz);
		    sip.setFilePath(highmtz);
	        sip.setId("sip:" + System.currentTimeMillis());
	        sip.setMetadataXMLString(high);
	        //sip.setMetadataXMLString(metadataDiff);
	     
	     BrilSIPProcessor sipProcessor = new  BrilSIPProcessor();   
	     sipProcessor.processSIP(sip);
	 	}
	/*public void test(){
		  BrilSIPProcessor sip = new BrilSIPProcessor();
		//path comes as C:/brilstore/expaa4ca950-a82f-49a7-93e9-59641de0ddb8/sorted-high.mtz
		//need to convert this into C:\\\\brilstore\\\\expaa4ca950-a82f-49a7-93e9-59641de0ddb8\\\\sorted-high.mtz  
		  String path1 ="C:/Experiment/baa5d5/sorted-high.mtz";
	
		//  String p =sip.replaceSlashes(path1);
		
		  //"C:\\\\Experiment\\\\baa5d5\\\\sorted-high.mtz"
	//	  String res= sip.getIdentifierWithTheTitle("expaa4ca950-a82f-49a7-93e9-59641de0ddb8",path1);
		
		 // if identifier is present then use this.
	//	  System.out.println(res);
	}*/

}
