package uk.ac.kcl.cerch.bril.service.monitordir;

import static name.pachler.nio.file.StandardWatchEventKind.ENTRY_CREATE;
import static name.pachler.nio.file.StandardWatchEventKind.ENTRY_DELETE;
import static name.pachler.nio.file.StandardWatchEventKind.ENTRY_MODIFY;
import static name.pachler.nio.file.StandardWatchEventKind.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import name.pachler.nio.file.ClosedWatchServiceException;
import name.pachler.nio.file.FileSystems;
import name.pachler.nio.file.Path;
import name.pachler.nio.file.Paths;
import name.pachler.nio.file.WatchEvent;
import name.pachler.nio.file.WatchKey;
import name.pachler.nio.file.WatchService;

import javax.jms.JMSException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import uk.ac.kcl.cerch.bril.common.util.FileUtil;
import uk.ac.kcl.cerch.bril.service.queue.ADQueueWriterImpl;
import uk.ac.kcl.cerch.bril.service.upload.FileServerClient;
import uk.ac.kcl.cerch.soapi.checksum.ChecksumProcessorException;
import uk.ac.kcl.cerch.soapi.checksum.MD5ChecksumProcessor;

/**
 * @author shrijar
 * @author eric
 * 
 */

public class DirectoryMonitor {
	private final WatchService watcher;
	private final Map<WatchKey, Path> keys;
	private final boolean recursive;
	private boolean trace = false;
	private String domain;
	private String project;
	private URL fileURL;
	private byte[] fileContents;
	private String id = "";
	private String entryType = "";
	private String dateTime = "";
	private String experimentId;
	private String experimentXML;

	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>) event;
	}

	/**
	 * Register the given directory with the WatchService
	 * @throws IOException
	 */
	private void register(File dir) throws IOException {
		
		Path watchedPath = Paths.get(dir.getCanonicalPath());
		WatchKey key = watchedPath.register(watcher, ENTRY_CREATE, ENTRY_DELETE,
				ENTRY_MODIFY);		
		keys.put(key, watchedPath);
	}

		
	/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService.
	 * @throws IOException 
	 */	
	public void registerAll(final File dir) throws IOException {

		if (dir.isDirectory())
			register(dir);
               
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
            	registerAll(new File(dir, children[i]));
            }
        }
    }	
	
	/**
	 * Creates a WatchService and registers the given directory
	 * @throws IOException
	 */
	public DirectoryMonitor(File dir, boolean recursive) throws IOException {

		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<WatchKey, Path>();
		this.recursive = recursive;

		if (recursive) {			
			System.out.format("Scanning %s ...\n", dir);			
			registerAll(dir);
			System.out.println("Done.");
		} else {
			register(dir);
		}

		// enable trace after initial registration
		this.trace = true;
	}

	/**
	 * Process all events for keys queued to the watcher
	 */
	@SuppressWarnings("rawtypes")
	public void processEvents() {

		for (;;) {
		    
			// take() will block until a file has been created/deleted
		    WatchKey signalledKey;
		    try {
		        signalledKey = watcher.take();
		    } catch (InterruptedException ix){
		        // we'll ignore being interrupted
		        continue;
		    } catch (ClosedWatchServiceException cwse){
		        // other thread closed watch service
		        System.out.println("watch service closed, terminating.");
		        break;
		    }

		    // get list of events from key
		    List<WatchEvent<?>> list = signalledKey.pollEvents();

		    Path context;
		    String fileSep = System.getProperty("file.separator");
		    
		    // VERY IMPORTANT! call reset() AFTER pollEvents() to allow the
		    // key to be reported again by the watch service
		    signalledKey.reset();

		    for (WatchEvent e : list) {
		        
		    	String message = "";
		        		        
		        context = (Path)e.context();
		        
		        boolean hasCurl = hasCurl(context.toString());
		        
		        if (hasCurl == false) {

					//verify if file is not empty				
					entryType = e.kind().name();
					id = keys.get(signalledKey) + fileSep + context.toString();
					File fileSource = new File(id);					
					dateTime = new java.text.SimpleDateFormat(
							"dd/MM/yyyy HH:mm:ss").format(new java.util.Date(
							fileSource.lastModified()));					
				}
		        
		        if (e.kind() == ENTRY_CREATE) {
		        	
		        	System.out.println("ID: " + id);
					System.out.println("entry: " + entryType);
					System.out.println("datetime: " + dateTime);
		        	
		            message = "created file: " + keys.get(signalledKey) + fileSep + context.toString();		            
		            File childFile = new File(keys.get(signalledKey) + fileSep + context.toString());
		            		            
		            /*
		             * Check the size of the file: if too large then use the
					 * largeRegistryEntry method
					 */

		            // TODO: is there any way of checking the file is completed before registering entry?		            
		            // for now, add a delay before registering
                    try {
                            Thread.sleep(2000);
                    } catch (InterruptedException e1) {
                    }
		            
					registerEntryToQueue(id, entryType, dateTime);					
					
					/*
					 * file name with ~ in the front is when its being modified or
					 * used e.g.,word docs ENTRY_MODIFY:
					 * c:\Experiment\.~lock.testi.odt# ENTRY_DELETE:
					 * c:\Experiment\.~lock.testi.odt# when the file is closed
					 * DELETE is shown but its not actually deleted ENTRY_DELETE:
					 * c:\Experiment\testi.odt
					 */
		            
		            if (recursive) {
		            	try {							
							if (childFile.isDirectory()) {							
								registerAll(childFile);
								message = "now watching dir: " + keys.get(signalledKey) + fileSep + context.toString();
							}													
						} catch (IOException x) {
							// ignore to keep sample readbale
						}						
		            }		
		            System.out.println(message);		           
					
		        } else if(e.kind() == OVERFLOW) {
		            message = "OVERFLOW: more changes happened than we could retreive";
		        }
		        		        		        		        		        		        	
		    }
		}		
	}

	private boolean hasCurl(String fPath) {		
		boolean curl = false;
		char curlChar = '~';

		StringBuffer aString = new StringBuffer(fPath);

		for (int i = 0; i < aString.length(); i++) {
			char ch = aString.charAt(i);
			if (ch == curlChar) {
				curl = true;
			}
		}
		return curl;
	}

	private boolean uploadFileToServer() {
		boolean uploaded = false;
		FileServerClient client = new FileServerClient(fileURL);
	
		try {
			uploaded = client.upload(fileContents);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uploaded;
	}
	public void setExperimentClassification(String researchDomain) {
		this.domain = researchDomain;
	}

	public String getExperimentClassification() {
		return domain;
	}

	public void setExperimentProject(String projectName) {
		this.project = projectName;
	}

	public String getExperimentProject() {
		return project;
	}

	public void setExperimentId(String experimentId) {
		this.experimentId = experimentId;
	}

	public String getExperimentId() {
		return experimentId;
	}

	public void setExperimentXML(String experimentXML) {
		this.experimentXML = experimentXML;
	}
	
	public void registerEntryToQueue(String ID, String entryType,
			String dateTime) {
		ADQueueWriterImpl ADQueueWriter;
		MD5ChecksumProcessor c = new MD5ChecksumProcessor();
		Properties properties;
		String fileName;
		String checksum;

		try {
			ADQueueWriter = new ADQueueWriterImpl();
			properties = new Properties();
			properties.load(getClass().getClassLoader().getResourceAsStream(
					"bril.properties"));			
			int dotInId = ID.lastIndexOf('.');
			File file;
			if (dotInId != -1) {
				fileName = FileUtil.getFileName1(ID);
				file = new File(ID);

				System.out.println("File size: " + file.length());
				fileContents = FileUtil.getBytesFromFile(file);
				
				fileURL = new URL(properties
						.getProperty("file.server.location")
						+ fileName);

				checksum = c.generateChecksum(file);
				
				//file in the directory created first and uploaded is in use now and updated, 
				//and this version is not uploaded
				
				if(fileContents.length!=0){
					boolean uploaded = uploadFileToServer();
					System.out.println("Uploaded: " + uploaded);				
					
					ADQueueWriter.sendBlobMessage(fileURL, ID, checksum,
						getExperimentClassification(), getExperimentProject(),
						entryType, dateTime, experimentId);
				}			
			}			

		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// } catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ChecksumProcessorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String arg[]) throws XMLStreamException,
			FactoryConfigurationError {
		String re = "-r";
		String directory = "/home/eric/bril/baa5d5";
		//String directory = "C:\\BRIL\\baa5d5";
		String projectName = "BRIL2";
		String type = "Crystallography";
		String expid = "exp-test";
		boolean createNewExp = true;
		boolean runExistingExp = false;

		ExperimentCreator helper = new ExperimentCreator();
		/*
		 * TODO 1. check if old experiments are present 2. if true load existing
		 * experiments - user selects one experiment and this will return all
		 * the parameters of this experiment that is used to monitor. 3. if
		 * false - create new as below that creates, saves to config file and
		 * sends the "new experiment message"
		 */
		System.out.println("has active experiment: "
				+ helper.hasAnActiveExperiment());
		//checks if there is one experiment in the config file and if present loads it when starting the gui for example
		//doesn not checks if the experiment is active or handles if more then one experiment is present in the config file
		if (helper.hasAnActiveExperiment() == true) {
			//If user has entered creates new experiment = true
			//then it removes the loaded ones and creates the new experiment
			if (createNewExp == true) {
				//set the returned one to inactive				
				System.out.println("Only one experiment present; Set this returned to inactive? ");
				//TODO if new is to created set all active ones to inactive
				helper.setStatusToInactive();
				helper.createNewExperiment(projectName, type, directory);
				System.out.println("New experiment created, using id: " + expid);
				expid = helper.getExperimentId();
				directory = helper.getDirectoryPath();
				type = helper.getExperimentType();
				projectName = helper.getProjectName();
			}
			//has one active experiment to be loaded
			else {
				helper.loadActiveExperiment();
				expid = helper.getExperimentId();
				directory = helper.getDirectoryPath();
				type = helper.getExperimentType();
				projectName = helper.getProjectName();
				System.out.println("Active experiment present, using id: "
						+ expid);
			}
		} else {
			System.out.println("More then one experiment present");
			
			//If user has entered creates new experiment = true
			//then it removes the loaded ones and creates the new experiment
			if (createNewExp == true) {
				//set the returned one to inactive				
				System.out.println("Use want to create a new experiment: Set this others to inactive? ");
				//TODO if new is to created set all active ones to inactive
				helper.setStatusToInactive();
				helper.createNewExperiment(projectName, type, directory);
				System.out.println("New experiment created, using id: " + expid);
				expid = helper.getExperimentId();
				directory = helper.getDirectoryPath();
				type = helper.getExperimentType();
				projectName = helper.getProjectName();
			} else{
				/**
				 * Needs to check if the experiment is active
				 * and load the one that is active - i.e., the last experiment
				 */
				String identifier = helper.getActiveExperiment();
				if(identifier!=null){				
					System.out.println("Active experiment present: "+ identifier);
					//TODO: load this experiment
					helper.loadActiveExperiment(identifier);
					expid = helper.getExperimentId();
					directory = helper.getDirectoryPath();
					type = helper.getExperimentType();
					projectName = helper.getProjectName();
					System.out.println("Load Active experiment present: "+ identifier);
									
				}
				else{
					System.out.println("All the experiments are inactive");	
					System.out.println("Either create one or load the last experiment");			
				}
			}
		}

		boolean recursive = false;
		if (!re.equals(null))
			recursive = true;

		// register directory and process its events
		//Path dir = Paths.get(directory);
		File dir = new File(directory);
		try {
			DirectoryMonitor wd = new DirectoryMonitor(dir, recursive);
			wd.setExperimentClassification(type);
			wd.setExperimentProject(projectName);
			wd.setExperimentId(expid);
			wd.processEvents();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}


