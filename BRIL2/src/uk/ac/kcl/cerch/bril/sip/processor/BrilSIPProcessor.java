package uk.ac.kcl.cerch.bril.sip.processor;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.exceptions.FitsConfigurationException;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlContent;

import uk.ac.kcl.cerch.bril.characteriser.COMScriptFileCharacterisation;
import uk.ac.kcl.cerch.bril.characteriser.COMScriptFileCharacteriserImpl;
import uk.ac.kcl.cerch.bril.characteriser.COOTScmFileCharacterisation;
import uk.ac.kcl.cerch.bril.characteriser.COOTScmFileCharacteriserImpl;
import uk.ac.kcl.cerch.bril.characteriser.DEFLogFileCharacterisation;
import uk.ac.kcl.cerch.bril.characteriser.DEFLogFileCharacteriserImpl;
import uk.ac.kcl.cerch.bril.characteriser.DiffractionImageFileCharacterisation;
import uk.ac.kcl.cerch.bril.characteriser.DiffractionImageFileCharacteriserImpl;
import uk.ac.kcl.cerch.bril.characteriser.MTZReflectionFileCharacterisation;
import uk.ac.kcl.cerch.bril.characteriser.MTZReflectionFileCharacteriserImpl;
import uk.ac.kcl.cerch.bril.characteriser.PhenixDEFFileCharacterisation;
import uk.ac.kcl.cerch.bril.characteriser.PhenixDEFFileCharacteriserImpl;
import uk.ac.kcl.cerch.bril.common.fedora.BrilObjectRepositoryException;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraAdminstrationImpl;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraHandler;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraRelsExt;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraUtils;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraNamespaceContext.FedoraNamespace;
import uk.ac.kcl.cerch.bril.common.metadata.DublinCore;
import uk.ac.kcl.cerch.bril.common.types.BrilTransformException;
import uk.ac.kcl.cerch.bril.common.types.DataStreamType;
import uk.ac.kcl.cerch.bril.common.types.DatastreamMimeType;
import uk.ac.kcl.cerch.bril.common.types.DatastreamObjectContainer;
import uk.ac.kcl.cerch.bril.fileformat.CrystallographyFileFormat;
import uk.ac.kcl.cerch.bril.fileformat.CrystallographyObjectType;
import uk.ac.kcl.cerch.bril.fileformat.identifier.CrystallographyFileFormatIdentifierImpl;
import uk.ac.kcl.cerch.bril.objectstore.RelationshipObject;
import uk.ac.kcl.cerch.bril.relationship.ObjectRelationship;
import uk.ac.kcl.cerch.bril.relationship.Relationship;
import uk.ac.kcl.cerch.bril.relationship.generator.ALNFileRelationshipGeneratorImpl;
import uk.ac.kcl.cerch.bril.relationship.generator.COMFileRelationshipGeneratorImpl;
import uk.ac.kcl.cerch.bril.relationship.generator.COOTScmFileRelationshipGeneratorImpl;
import uk.ac.kcl.cerch.bril.relationship.generator.CoordinateFileRelationshipGeneratorImpl;
import uk.ac.kcl.cerch.bril.relationship.generator.DOCFileRelationshipGeneratorImpl;
import uk.ac.kcl.cerch.bril.relationship.generator.LOGFileRelationshipGeneratorImpl;
import uk.ac.kcl.cerch.bril.relationship.generator.PhenixDEFFileRelationshipGeneratorImpl;
import uk.ac.kcl.cerch.bril.relationship.generator.DEFFileCCP4RelationshipGeneratorImpl;
import uk.ac.kcl.cerch.bril.relationship.generator.DiffractionImageFileRelationshipGeneratorImpl;
import uk.ac.kcl.cerch.bril.relationship.generator.MTZReflectionFileRelationshipGeneratorImpl;
import uk.ac.kcl.cerch.bril.relationship.generator.SEQFileRelationshipGeneratorImpl;
import uk.ac.kcl.cerch.bril.service.queue.MessageMetadata;
import uk.ac.kcl.cerch.bril.sip.BrilSIP;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacteriserException;
import uk.ac.kcl.cerch.soapi.fileformat.identifier.FileFormatIdentifierException;
import uk.ac.kcl.cerch.soapi.objectstore.ArchivalObject;
import uk.ac.kcl.cerch.soapi.objectstore.FileCharacterisation;
import uk.ac.kcl.cerch.soapi.objectstore.FileFormat;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectArtifact;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectStore;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectStoreException;
import uk.ac.kcl.cerch.soapi.objectstore.OriginalContent;
import uk.ac.kcl.cerch.soapi.objectstore.database.ArchivalObjectDao;
import uk.ac.kcl.cerch.soapi.objectstore.database.SIPDao;
import uk.ac.kcl.cerch.soapi.sip.SIP;
import uk.ac.kcl.cerch.soapi.sip.processor.SIPProcessorException;


public class BrilSIPProcessor {
	private static Logger log = Logger.getLogger(BrilSIPProcessor.class);
	ApplicationContext applicationContext;
	private BrilSIP brilSIP;
	private ObjectStore objectStore;
	private SIPDao sipsDao;
	private MessageMetadata md;
	// object that connects to the database or file system that holds all the
	// objects
	private ArchivalObjectDao archivalObjectDao;
	private boolean flag;
	private boolean checksumFlag;
    private int numberOfRelationship;
    private String fileformat;
    private String FITS_HOME = "/home/eliao/fits/";

	public BrilSIPProcessor() {
		applicationContext = new FileSystemXmlApplicationContext(
				"config/soapi.xml");
		sipsDao = (SIPDao) applicationContext.getBean("sipDao");
		archivalObjectDao = (ArchivalObjectDao) applicationContext
				.getBean("archivalObjectDao");
		/*
		 * uses FileSystemObjectStore that creates objectStoreDirectory + "/" +
		 * id + ".data"
		 */

		objectStore = (ObjectStore) applicationContext.getBean("objectStore");
	}
	
	public BrilSIPProcessor(boolean flag) {
		setChecksumMatchFlag(flag);
		
		applicationContext = new FileSystemXmlApplicationContext("config/soapi.xml");
		sipsDao = (SIPDao) applicationContext.getBean("sipDao");
		archivalObjectDao = (ArchivalObjectDao) applicationContext.getBean("archivalObjectDao");
		objectStore = (ObjectStore) applicationContext.getBean("objectStore");
	}

	public void processSIP(SIP sip) throws SIPProcessorException {

		this.brilSIP = (BrilSIP) sip;
		File file = new File(brilSIP.getFilePath());
		String metadataString = brilSIP.getMetadataXMLString();
		String identifier = brilSIP.getIdentifier();
		System.out.println("---------- SIPProcessor -----------");
		System.out.println("checksumFlag: "+getChecksumMatchFlag());
		System.out.println("filename: "+file.getName());
		System.out.println("metadata xml: "+metadataString);
		System.out.println("identifier: "+identifier);
		processFile(file, metadataString);
	}
	
	public boolean getChecksumMatchFlag(){
		return checksumFlag;
	}
	
	public void setChecksumMatchFlag(boolean flag){
		this.checksumFlag=flag;
	}
	

	private void processFile(File file, String metadataString) {
		md = new MessageMetadata(metadataString);

		// Create an Archival object
		System.out.println("----- Save SIP to sipDAO ----- ");

		sipsDao.saveSIP(brilSIP);
		System.out.println("------ Creating Archival Object ------- ");
		ArchivalObject archivalObject = new ArchivalObject();
		System.out.println("------ archival Object ------- ");
		archivalObject.setFilename(file.getName());
		
		// This id can be the UUID generated for this archival object keep the
		// same in fedora for this object

		//generate Id here if no id is passed- not an updates
		archivalObject.setId(brilSIP.getIdentifier());
		System.out.println("Path ------- " + file.getPath());
		
		
		archivalObject.setPath(file.getPath().replaceAll("\\\\", "/"));
		archivalObject.setSip(brilSIP);
		System.out.println(archivalObject.getSip());

		// Create OriginalContent object that extends ObjectArtifact
		OriginalContent originalcontent = new OriginalContent();
		originalcontent.setFilePath(file.getPath().replaceAll("\\\\", "/"));
		String g = file.getPath().replaceAll("\\\\", "/");
		System.out.println(g);
		originalcontent.setLabel("OriginalContent");

		/*
		 * Create MessageMetadata object MessageMetadata messageMetadata = new
		 * MessageMetadata(metadataString); To put this in the objectstore
		 * MessageMetadata class must extend ObjectArtifact
		 */
		/*
		 * Store the OriginalContent in the objectstore that returns the
		 * classname as an id
		 */

		String objectArtifactId_copy = "";
		try {
			objectArtifactId_copy = objectStore.putObjectArtifact(originalcontent);
		} catch (ObjectStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Create 1 object artifact for the original content or file object
		ObjectArtifact objectArtifact = new ObjectArtifact();
		objectArtifact.setId(objectArtifactId_copy);
		objectArtifact.setType("OriginalContent");
		objectArtifact.setArchivalObject(archivalObject);

		// Add the object artifacts to the the Archival object
		archivalObject.addObjectArtifact(objectArtifact);
		archivalObjectDao.saveArchivalObject(archivalObject);
		
		/*
		 * If checksumFlag is false ingest object with minimum DC to the repository
		 */
		if (checksumFlag == false){
			try {
				createDublinCore(archivalObject.getId());
				processRelationship(archivalObject.getId());
			} catch (ObjectStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Create FOXML
			createFOXML(archivalObject.getId());
		}
		
		/*
		 * If the checksum flag is true, carry out file identification, 
		 * file characterisation an then create the du and foxml
		 */
		if (checksumFlag == true){
			try {
				try {
					processIdentifyFileFormat(archivalObject.getId(), file);
				} catch (ObjectStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileFormatIdentifierException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	
			if (getFileFormat(archivalObject.getId()).getFormat().equals(
					CrystallographyObjectType.CompressedDiffractionImage.getType())) {
				log.info(String.format(
						"Ignoring object compressedDiffractionImage:  %s", file.getName()));
	
			} else {
				try {
					processCharacteriseFile(archivalObject.getId(), file);
	
				} catch (FileCharacteriserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ObjectStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					createDublinCore(archivalObject.getId());
					processRelationship(archivalObject.getId());
				} catch (ObjectStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Create FOXML
				createFOXML(archivalObject.getId());
			}
		}

	}

	private void processIdentifyFileFormat(String archivalObjectId, File file)
			throws FileFormatIdentifierException, ObjectStoreException {
		ArchivalObject archivalObject = archivalObjectDao
				.getArchivalObjectById(archivalObjectId);

		log.info("Identifying File Format");
		/*
		 * Identify the Crystallography object type
		 */
		System.out.println("Crystallography FileFormat ----- ");
		CrystallographyFileFormat fileFormat;

		fileFormat = (CrystallographyFileFormat) new CrystallographyFileFormatIdentifierImpl()
				.identifyFileFormat(file);
        fileformat =  fileFormat.getFormat();
        System.out.println("Fileformat mime:"+ fileFormat.getMimeType());
        System.out.println("Fileformat format:"+ fileformat);

		uk.ac.kcl.cerch.soapi.objectstore.FileFormat objectStoreFileFormat = new uk.ac.kcl.cerch.soapi.objectstore.FileFormat();
		objectStoreFileFormat.setFormat(fileFormat.getFormat());
		objectStoreFileFormat.setDescription(fileFormat.getDescription());
		objectStoreFileFormat.setFileSuffix(fileFormat.getFileSuffix());
		objectStoreFileFormat.setMimeType(fileFormat.getMimeType());

		// Store in object store and get the id
		String objectArtifactFileFormatId;

		objectArtifactFileFormatId = objectStore
				.putObjectArtifact(objectStoreFileFormat);

		// Create an ObjectArtifact and set archivalobject
		ObjectArtifact ObjectArtifactFileFormat = new ObjectArtifact();
		ObjectArtifactFileFormat.setId(objectArtifactFileFormatId);
		ObjectArtifactFileFormat.setType("FileFormat");
		ObjectArtifactFileFormat.setArchivalObject(archivalObject);

		// add the ObjectArtifactFileFormat to the archivalObject
		archivalObject.addObjectArtifact(ObjectArtifactFileFormat);
		// save the archivalObject to archivalObjectDao
		archivalObjectDao.saveArchivalObject(archivalObject);

	}

	private void processCharacteriseFile(String archivalObjectId, File file)
			throws FileCharacteriserException, ObjectStoreException {
		ArchivalObject archivalObject = archivalObjectDao
				.getArchivalObjectById(archivalObjectId);

		String objectArtifactFileCharacterisationId = null;
		/*
		 * Get FileFormat objectartifact from the store
		 */
		Set<ObjectArtifact> objectArtifacts = archivalObject
				.getObjectArtifactsByType("FileFormat");
		ObjectArtifact objectArtifact = null;
		FileFormat fileFormat = null;
		Iterator<ObjectArtifact> iter = objectArtifacts.iterator();
		while (iter.hasNext()) {
			objectArtifact = (ObjectArtifact) iter.next();
			String type = objectArtifact.getType();			
			String objectArtifactId = objectArtifact.getId();
			if (type.equals("FileFormat")) {
				FileFormat fileFormatTmp = (FileFormat) objectStore
						.getObjectArtifact(objectArtifactId);
				if(fileformat.equals(fileFormatTmp.getFormat())){
					fileFormat= fileFormatTmp;
				}
			}
		}
		// log.info("File Characterisation");
		/*
		 * Characterise the crystallography object type For compressed
		 * diffraction image may want to
		 */

		if (fileFormat.getFormat().equals(
				CrystallographyObjectType.DiffractionImage.getType())) {
			System.out.println("DiffractionImageFileCharacterisation ----- ");
			DiffractionImageFileCharacterisation fileCharacterisation = (DiffractionImageFileCharacterisation) new DiffractionImageFileCharacteriserImpl()
					.characteriseFile(file);

			uk.ac.kcl.cerch.soapi.objectstore.FileCharacterisation objectStoreFileCharacterisation = new uk.ac.kcl.cerch.soapi.objectstore.FileCharacterisation();
			objectStoreFileCharacterisation.setMetadata(fileCharacterisation
					.getMetadata());
			// Store in object store and get the id
			objectArtifactFileCharacterisationId = objectStore
					.putObjectArtifact(objectStoreFileCharacterisation);
		}
		if (fileFormat.getFormat().equals(
				CrystallographyObjectType.MTZReflectionFile.getType())) {
			System.out.println("MTZReflectionFileCharacterisation ----- ");
			MTZReflectionFileCharacterisation fileCharacterisation = (MTZReflectionFileCharacterisation) new MTZReflectionFileCharacteriserImpl()
					.characteriseFile(file);
			uk.ac.kcl.cerch.soapi.objectstore.FileCharacterisation objectStoreFileCharacterisation = new uk.ac.kcl.cerch.soapi.objectstore.FileCharacterisation();
			objectStoreFileCharacterisation.setMetadata(fileCharacterisation
					.getMetadata());
			// Store in object store and get the id
			objectArtifactFileCharacterisationId = objectStore
					.putObjectArtifact(objectStoreFileCharacterisation);
		}

		if (fileFormat.getFormat().equals(
				CrystallographyObjectType.COMFile.getType())) {
			System.out.println("COMScriptFileCharacterisation ----- ");
			COMScriptFileCharacterisation fileCharacterisation = (COMScriptFileCharacterisation) new COMScriptFileCharacteriserImpl()
					.characteriseFile(file);
			uk.ac.kcl.cerch.soapi.objectstore.FileCharacterisation objectStoreFileCharacterisation = new uk.ac.kcl.cerch.soapi.objectstore.FileCharacterisation();
			objectStoreFileCharacterisation.setMetadata(fileCharacterisation
					.getMetadata());

			// Store in object store and get the id
			objectArtifactFileCharacterisationId = objectStore
					.putObjectArtifact(objectStoreFileCharacterisation);

		}
		if (fileFormat.getFormat().equals(
				CrystallographyObjectType.CCP4IDefFile.getType()))
				//&& file.getName().equals("database.def")) 
		{
            //both the database.def or specific taskname.def file is processed and XML metadata is created.
			System.out.println("DEFLogFileCharacterisation ----- ");
			DEFLogFileCharacterisation fileCharacterisation = (DEFLogFileCharacterisation) new DEFLogFileCharacteriserImpl()
					.characteriseFile(file);
			uk.ac.kcl.cerch.soapi.objectstore.FileCharacterisation objectStoreFileCharacterisation = new uk.ac.kcl.cerch.soapi.objectstore.FileCharacterisation();
			objectStoreFileCharacterisation.setMetadata(fileCharacterisation
					.getMetadata());

			// Store in object store and get the id
			objectArtifactFileCharacterisationId = objectStore
					.putObjectArtifact(objectStoreFileCharacterisation);

		}
		
		if (fileFormat.getFormat().equals(
				CrystallographyObjectType.PhenixDefFile.getType()))
				
		{
            //PHENIX parameter file filaname.def is processed and XML metadata is created.
			System.out.println("PhenixDEFFileCharacterisation ----- ");
			PhenixDEFFileCharacterisation fileCharacterisation = (PhenixDEFFileCharacterisation) new PhenixDEFFileCharacteriserImpl()
					.characteriseFile(file);
			uk.ac.kcl.cerch.soapi.objectstore.FileCharacterisation objectStoreFileCharacterisation = new uk.ac.kcl.cerch.soapi.objectstore.FileCharacterisation();
			objectStoreFileCharacterisation.setMetadata(fileCharacterisation
					.getMetadata());

			// Store in object store and get the id
			objectArtifactFileCharacterisationId = objectStore
					.putObjectArtifact(objectStoreFileCharacterisation);

		}
		if (fileFormat.getFormat().equals(
				CrystallographyObjectType.CootStateExeFile.getType()))
		{
            //COOT state file coot-state.scm or coot-history.scm is processed and XML metadata is created.
			System.out.println("COOTScmFileCharacterisation ----- ");
			COOTScmFileCharacterisation fileCharacterisation = (COOTScmFileCharacterisation) new COOTScmFileCharacteriserImpl()
					.characteriseFile(file);
			uk.ac.kcl.cerch.soapi.objectstore.FileCharacterisation objectStoreFileCharacterisation = new uk.ac.kcl.cerch.soapi.objectstore.FileCharacterisation();
			objectStoreFileCharacterisation.setMetadata(fileCharacterisation
					.getMetadata());

			// Store in object store and get the id
			objectArtifactFileCharacterisationId = objectStore
					.putObjectArtifact(objectStoreFileCharacterisation);
			
		}				
		
		if (objectArtifactFileCharacterisationId != null) {
			// Create an ObjectArtifact and set archivalobject
			ObjectArtifact ObjectArtifactFileCharacterisation = new ObjectArtifact();
			ObjectArtifactFileCharacterisation
					.setId(objectArtifactFileCharacterisationId);
			ObjectArtifactFileCharacterisation.setType("FileCharacterisation");
			ObjectArtifactFileCharacterisation
					.setArchivalObject(archivalObject);
			// add the ObjectArtifactFileFormat to the archivalObject
			archivalObject
					.addObjectArtifact(ObjectArtifactFileCharacterisation);
			archivalObjectDao.saveArchivalObject(archivalObject);
		}

	}

	private void processRelationship(String archivalObjectId)
			throws ObjectStoreException {
		ArchivalObject archivalObject = archivalObjectDao
				.getArchivalObjectById(archivalObjectId);

		// System.out.println("experiment ID: " +md.getExperimentId());
		/*
		 * Check if the experiment id is present in the repository?
		 */
		/*
		 * ObjectRelationship orel = new ObjectRelationship();
		 * orel.addRelationship(archivalObjectId, "isPartOf", md
		 * .getExperimentId());
		 */

		Set<ObjectArtifact> objectArtifactsSet = archivalObject
				.getObjectArtifactsByType("FileFormat");
		ObjectArtifact objectArtifact = null;
		FileFormat fileFormat = null;
		Iterator<ObjectArtifact> iter = objectArtifactsSet.iterator();
		while (iter.hasNext()) {
			objectArtifact = (ObjectArtifact) iter.next();
			String type = objectArtifact.getType();
			String objectArtifactId = objectArtifact.getId();
			if (type.equals("FileFormat")) {
				try {
					FileFormat fileFormatTmp = (FileFormat) objectStore
							.getObjectArtifact(objectArtifactId);

					if (fileformat.equals(fileFormatTmp.getFormat())) {
						fileFormat = fileFormatTmp;
					}
				} catch (ObjectStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("-------------------------------------");

		}
		System.out.println("----- ObjectRelationship ----- ");

		// generates a static relationship 'isPartOf' for this objectId=
		// archivalObjectId
		log.info("ObjectRelationship method  ");
		ObjectRelationship objectRelationship = null;
		// even if fileformat is null at least relationship to the experiment
		// should be present
		if (fileFormat == null) {
			// if fileformat is null means file is not completely uploaded-
			// atleast add isPartOf experiment relationship-so this objectid can
			// be found
			System.out.println("adding relationship to the experiment id-------------");
			objectRelationship = new ObjectRelationship();
			objectRelationship.addRelationship(archivalObjectId, "isPartOf",md.getExperimentId());
		}
		if (fileFormat != null) {

			if (fileFormat.getFormat().equals(CrystallographyObjectType.MTZReflectionFile.getType())) {
				objectRelationship = (ObjectRelationship) new MTZReflectionFileRelationshipGeneratorImpl()
						.generateRelationships(archivalObjectId, md.getExperimentId());
				System.out.println("num of relationship: " + objectRelationship.getRelationships().size());
				// System.out.println(objectRelationship.getRelationships());
			}
			/*
			 * TODO: Compressed diffraction files are first copied in the
			 * experiment area (folder) that are then uncompressed to process
			 * them Thus, first the compressed then the uncompressed files
			 * arrives to be ingested. At present only the extracted diffraction
			 * file is uploaed and processed.
			 * 
			 * Need to capture these relationship: Identify that this is the
			 * manifestation of the same object (compressed diff image file) For
			 * compressed file: 1. make a DC and deposit and have the
			 * archivalobject in the store 2. when a uncompressed diffraction
			 * image file arrives check the store/repository for FileFormat
			 * CompressedDiffrationImage 3. Get filename of the archivalobject
			 * in the store or repository 4. Match this with the diffraction
			 * image filename. 5. Create an objectArtifact
			 * (PreservationManifestation) for the existing ArchivalObject
			 * (Compressed file) 6. Create an objectArtifact
			 * (DisseminationManifestation for jpeg converted binary image
			 * file).
			 */
			/*
			 * if (fileFormat.getFormat().equals(
			 * CrystallographyObjectType.CompressedDiffractionImage.getType()))
			 * {
			 * 
			 * objectRelationship = (ObjectRelationship) new
			 * DiffractionImageFileRelationshipGeneratorImpl()
			 * .generateRelationships(archivalObjectId, md .getExperimentId());
			 * }
			 */
			else if (fileFormat.getFormat().equals(
					CrystallographyObjectType.DiffractionImage.getType())) {
				objectRelationship = (ObjectRelationship) new DiffractionImageFileRelationshipGeneratorImpl()
						.generateRelationships(archivalObjectId,
								md.getExperimentId());
				System.out.println("Diffraction file--");
			} else if (fileFormat.getFormat().equals(
					CrystallographyObjectType.CCP4IDefFile.getType())) {
				objectRelationship = (ObjectRelationship) new DEFFileCCP4RelationshipGeneratorImpl()
						.generateRelationships(archivalObjectId,
								md.getExperimentId());
				System.out.println("DEF file--");
			} else if (fileFormat.getFormat().equals(
					CrystallographyObjectType.COMFile.getType())) {
				objectRelationship = (ObjectRelationship) new COMFileRelationshipGeneratorImpl()
						.generateRelationships(archivalObjectId,
								md.getExperimentId());
				System.out.println("COM file--");
			} else if (fileFormat.getFormat().equals(
					CrystallographyObjectType.CoordinateFile.getType())) {
				objectRelationship = (ObjectRelationship) new CoordinateFileRelationshipGeneratorImpl()
						.generateRelationships(archivalObjectId,
								md.getExperimentId());
				System.out.println(archivalObjectId);
				System.out.println(md.getExperimentId());

				System.out.println("CoordinateFile relationship generation......" + objectRelationship.getRelationships());

			} else if (fileFormat.getFormat().equals(
					CrystallographyObjectType.AlignmentFile.getType())) {
				objectRelationship = (ObjectRelationship) new ALNFileRelationshipGeneratorImpl()
						.generateRelationships(archivalObjectId,
								md.getExperimentId());
				System.out.println("AlignmentFile file--");
				
			} else if (fileFormat.getFormat().equals(
					CrystallographyObjectType.PhenixDefFile.getType())) {
				objectRelationship = (ObjectRelationship) new PhenixDEFFileRelationshipGeneratorImpl()
						.generateRelationships(archivalObjectId,
								md.getExperimentId());
				System.out.println("Phenix def file--");

			} else if (fileFormat.getFormat().equals(
					CrystallographyObjectType.CootStateExeFile.getType())) {

				objectRelationship = (ObjectRelationship) new COOTScmFileRelationshipGeneratorImpl()
						.generateRelationships(archivalObjectId,
								md.getExperimentId());
				System.out.println("CootStateExeFile file--");

			} else if (fileFormat.getFormat().equals(
					CrystallographyObjectType.SEQFile.getType())) {
				objectRelationship = (ObjectRelationship) new SEQFileRelationshipGeneratorImpl()
						.generateRelationships(archivalObjectId, md
								.getExperimentId());
				System.out.println("SEQ file--");
				
			} else if (fileFormat.getFormat().equals(
						CrystallographyObjectType.DOCFile.getType())) {
					objectRelationship = (ObjectRelationship) new DOCFileRelationshipGeneratorImpl()
							.generateRelationships(archivalObjectId, md
									.getExperimentId());
					System.out.println("DOC/DOCX/RTF file--");			
			
			} else if (fileFormat.getFormat().equals(
					CrystallographyObjectType.LOGFile.getType())) {
				objectRelationship = (ObjectRelationship) new LOGFileRelationshipGeneratorImpl()
				.generateRelationships(archivalObjectId, md
						.getExperimentId());
				System.out.println("LOGFile file--");			
					
			// the following are miscellaneous files: only an "isPart"
			// relationship is generated.
			
			} else if (fileFormat.getFormat().equals(
					CrystallographyObjectType.MosflmSavFile.getType())) {
				objectRelationship = new ObjectRelationship();
				objectRelationship.addRelationship(archivalObjectId,
						"isPartOf", md.getExperimentId());
				System.out.println("MosflmSavFile file--");
			} else if (fileFormat.getFormat().equals(
					CrystallographyObjectType.MosflmLpFile.getType())) {
				objectRelationship = new ObjectRelationship();
				objectRelationship.addRelationship(archivalObjectId,
						"isPartOf", md.getExperimentId());
				System.out.println("MosflmLpFile file--");
			} else if (fileFormat.getFormat().equals(
					CrystallographyObjectType.MosflmGenFile.getType())) {
				objectRelationship = new ObjectRelationship();
				objectRelationship.addRelationship(archivalObjectId,
						"isPartOf", md.getExperimentId());
				System.out.println("MosflmGenFile file--");			
			} else if (fileFormat.getFormat().equals(
					CrystallographyObjectType.MiscFile.getType())) {
				objectRelationship = new ObjectRelationship();
				objectRelationship.addRelationship(archivalObjectId,
						"isPartOf", md.getExperimentId());
				System.out.println("MiscFile file--");
			} else if (fileFormat.getFormat().equals("")) {
				objectRelationship = new ObjectRelationship();
				objectRelationship.addRelationship(archivalObjectId,
						"isPartOf", md.getExperimentId());
				System.out.println("format is empty file--");
			} else {
				objectRelationship = new ObjectRelationship();
				objectRelationship.addRelationship(archivalObjectId,
						"isPartOf", md.getExperimentId());
				System.out.println("none of the format matches--");
			}

		}

		if (objectRelationship != null) {
			
			numberOfRelationship = objectRelationship.getRelationships().size();
			System.out.println("Adding relations: " + numberOfRelationship);
			uk.ac.kcl.cerch.bril.objectstore.RelationshipObject objectStoreRelationshipObject 
					= new uk.ac.kcl.cerch.bril.objectstore.RelationshipObject();
			objectStoreRelationshipObject.setRelationships(objectRelationship
					.getRelationships());

			// Store in object store and get the id
			String objectArtifactRelationshipObjectId = objectStore
					.putObjectArtifact(objectStoreRelationshipObject);

			// Create an ObjectArtifact and set archivalobject
			ObjectArtifact ObjectArtifactRelationshipObject = new ObjectArtifact();
			ObjectArtifactRelationshipObject
					.setId(objectArtifactRelationshipObjectId);
			ObjectArtifactRelationshipObject.setType("RelationshipObject");
			ObjectArtifactRelationshipObject.setArchivalObject(archivalObject);
			// add the ObjectArtifactFileFormat to the archivalObject
			archivalObject.addObjectArtifact(ObjectArtifactRelationshipObject);
			archivalObjectDao.saveArchivalObject(archivalObject);
		}
	}


	private void createDublinCore(String archivalObjectId)
			throws ObjectStoreException {

		ArchivalObject archivalObject = archivalObjectDao
				.getArchivalObjectById(archivalObjectId);
		
		/*
		 * Get FileFormat objectartifact from the store
		 */
		Set<ObjectArtifact> objectArtifacts = archivalObject
				.getObjectArtifactsByType("FileFormat");
		ObjectArtifact objectArtifact = null;
		FileFormat fileFormat = null;
		Iterator<ObjectArtifact> iter = objectArtifacts.iterator();
		while (iter.hasNext()) {
			objectArtifact = (ObjectArtifact) iter.next();
			String objectArtifactId = objectArtifact.getId();
			fileFormat = (FileFormat) objectStore
					.getObjectArtifact(objectArtifactId);
		}

		System.out.println("DublinCore ----- ");		

		// log.info("Creating Dublincore");
		/*
		 * I
		 */
		uk.ac.kcl.cerch.soapi.objectstore.DublinCore objectStoreDublinCore = new uk.ac.kcl.cerch.soapi.objectstore.DublinCore();
		objectStoreDublinCore.setId(archivalObjectId);
		objectStoreDublinCore.setTitle(md.getIdPath());
		objectStoreDublinCore.setDate(md.getDateTime());
		if(fileFormat!=null){
		objectStoreDublinCore.setDescription(fileFormat.getDescription());
		objectStoreDublinCore.setFormat(fileFormat.getFormat());
		}
		// Store in object store and get the id
		String objectArtifactDublinCoreId = objectStore
				.putObjectArtifact(objectStoreDublinCore);
		// Create an ObjectArtifact
		ObjectArtifact ObjectArtifactDublinCore = new ObjectArtifact();
		ObjectArtifactDublinCore.setId(objectArtifactDublinCoreId);
		ObjectArtifactDublinCore.setType("DublinCore");
		ObjectArtifactDublinCore.setArchivalObject(archivalObject);
		// add the ObjectArtifactFileFormat to the archivalObject
		archivalObject.addObjectArtifact(ObjectArtifactDublinCore);
		archivalObjectDao.saveArchivalObject(archivalObject);
	}

	private void createFOXML(String archivalObjectId) {
		ArchivalObject archivalObject = archivalObjectDao
				.getArchivalObjectById(archivalObjectId);

		/*
		 * Get DublinCore from the objectstore.
		 */
		ObjectArtifact objectArtifact = null;
		uk.ac.kcl.cerch.soapi.objectstore.OriginalContent originalContent=null;
		uk.ac.kcl.cerch.soapi.objectstore.DublinCore dublinCore = null;
		
		FileFormat fileFormat = null;
		FileCharacterisation fileCharacterisation = null;
		Set<ObjectArtifact> objectArtifactsSet = archivalObject
				.getObjectArtifacts();

		Iterator<ObjectArtifact> iter = objectArtifactsSet.iterator();
		while (iter.hasNext()) {
			objectArtifact = (ObjectArtifact) iter.next();
			String type = objectArtifact.getType();
			String objectArtifactId = objectArtifact.getId();
			//TODO upload file etc
			if (type.equals("OriginalContent")) {
				try {
					originalContent = (uk.ac.kcl.cerch.soapi.objectstore.OriginalContent) objectStore
							.getObjectArtifact(objectArtifactId);

				} catch (ObjectStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (type.equals("DublinCore")) {
				try {
					dublinCore = (uk.ac.kcl.cerch.soapi.objectstore.DublinCore) objectStore
							.getObjectArtifact(objectArtifactId);

				} catch (ObjectStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (type.equals("FileFormat")) {
				try {
					fileFormat = (FileFormat) objectStore
							.getObjectArtifact(objectArtifactId);

				} catch (ObjectStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (type.equals("FileCharacterisation")) {
				try {
					fileCharacterisation = (FileCharacterisation) objectStore
							.getObjectArtifact(objectArtifactId);

				} catch (ObjectStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
		/*
		 * search if this is present in the repository for this experiment 
		 */
		
		/*
		 * Get the path of the OriginalContent, then Upload this file to the
		 * fedora. Fedora returns the url of the uploaded file, this is
		 * converted to byte[].
		 * 
		 * ALternatively this can be a URL
		 */		
		String mime = null;
		String briltype = null;
		String metadata = null;
		if(fileFormat!=null){
		 mime = fileFormat.getMimeType();
		 briltype = fileFormat.getFormat();
		}
		if (fileCharacterisation != null) {
			metadata = fileCharacterisation.getMetadata();
		}
		
		// byte[] urlData = uploadFileToFedora(archivalObjectId );
		//byte[] data converted from the original file
		//byte[] byteOriginalContent =getOriginalContentByte(archivalObjectId);
		// test internal url so data is not uploaded to fedora during the test
		// runs
	    //String url = "uploaded://32";
		//byte[] urlData = url.getBytes();
		
		/* Convert the file path to byte[] to use addDatastreamObject method at add original content
		 * The variable filePath can be either:
		 *  1)A local path that will added to fedora internally (returns internal uri)
		 *  2)An external url http:// that will be added as an external object in fedora
		 *  : upload the data else where and send the url as byte[] 
		 * 
		 */
		byte[] filePath= originalContent.getFilePath().getBytes();
		byte[] relsExtData = getRelationshipRDF(archivalObjectId);
		DublinCore digitalObjectDC = new DublinCore(dublinCore.getId());
		digitalObjectDC.setTitle(dublinCore.getTitle());

		if(dublinCore.getDescription()!=null){
		digitalObjectDC.setDescription(dublinCore.getDescription());
		}
		if(dublinCore.getFormat()!=null){
		digitalObjectDC.setFormat(dublinCore.getFormat());
		}
		digitalObjectDC.setDate(dublinCore.getDate(), "dd/MM/yyyy HH:mm:ss");
		System.out
				.println("--------  END: Create Dublin code metadata created --------------");

		DatastreamObjectContainer dsc = new DatastreamObjectContainer(
				dublinCore.getId());
		dsc.addMetaData(digitalObjectDC);
		
		if(relsExtData!=null){
		dsc.addDatastreamObject(DataStreamType.RelsExt,
				DatastreamMimeType.APPLICATION_RDF.getMimeType(),
				"relationship", "bril", relsExtData);
		}
		
		if (mime!=null) {
			System.out.println("mime: " + mime.trim());
			System.out.println("briltype: " + briltype);
			System.out.println("metadata: " + metadata);
		
			if (dublinCore.getFormat()!=null || !dublinCore.getFormat().equals("")) {
				briltype=dublinCore.getFormat();
				System.out.println("briltype: " + briltype);
			} else if (dublinCore.getFormat()==null || dublinCore.getFormat().equals("")) {
				briltype="misc";
				System.out.println("briltype: " + briltype);
			}
			
			if (DatastreamMimeType.validMimetype(mime.trim()) == true) {
				dsc.addDatastreamObject(DataStreamType.OriginalData, mime.trim(),
					briltype, "bril", filePath);
			} else 			
			//DatastreamObjectContainer addDatastreamObject only accepts byte stream of  the original content
			//here we put the byte objectContent of the original file as null
			{
				dsc.addDatastreamObject(DataStreamType.OriginalData, "application/octet-stream",
					briltype, "bril", filePath);
		    }
		} else {
			dsc.addDatastreamObject(DataStreamType.OriginalData, "application/octet-stream",
					"misc", "bril", filePath);
		}
		if (metadata != null) {
			dsc.addDatastreamObject(DataStreamType.ObjectMetadata,
					DatastreamMimeType.TEXT_XML.getMimeType(), briltype,
					"bril", metadata.getBytes());
		}

		// Use FITS to identify the technical metadata of the file
		// Then converts it to PREMIS format and stores it as a PREMIS datastream
		
		Fits fits;
		try {
			fits = new Fits();
			File file = new File(originalContent.getFilePath());
			FitsOutput fitsOut;			
			System.out.println("Running FITS toolkit...");
			fitsOut = fits.examine(file);			
			byte[] PREMIS = outputPREMISXml(fitsOut, dublinCore.getId(), originalContent.getFilePath());
			System.out.println(PREMIS.toString());				
			
			dsc.addDatastreamObject(DataStreamType.PremisMetadata,
					DatastreamMimeType.TEXT_XML.getMimeType(), "premis",
					"bril", PREMIS);
			System.out.println("PREMIS datastream saved...");
		} catch (FitsConfigurationException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (FitsException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}								
				
		/*
		 * Get the object identifiers with this title/original path and this experimentId
		 * 
		 */

		System.out.println("Get the existing objects with this: "+ md.getIdPath());
	

		FedoraAdminstrationImpl fedoraAdmin = null;
		FedoraUtils fu = new FedoraUtils();
		
		fu.setContentLocation("uploaded://32");
		try {
		 	fedoraAdmin = new FedoraAdminstrationImpl();
		 	purgeIfPresentInRepository(dublinCore.getId());
		    fedoraAdmin.storeObject(dsc);
		 
		} catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private byte[] outputPREMISXml(FitsOutput fitsOutput, String PID, String location) throws XMLStreamException, IOException, TransformerConfigurationException {
        		
		Document doc = fitsOutput.getFitsXml();
	    
	    //initialize transformer for PREMIS xslt	        
	    TransformerFactory tFactory = TransformerFactory.newInstance();		
	    
	    // Get a XSLT transformer
	    StreamSource xsltSource = new StreamSource(new FileInputStream(FITS_HOME + "xml/fits_to_premis_object.xsl"));
		Transformer transformer;		
		transformer = tFactory.newTransformer(xsltSource);
		    
        if (doc != null && transformer != null) {
        	
        	// Make the input sources for the XML and XSLT documents
    		org.jdom.output.DOMOutputter outputter = new org.jdom.output.DOMOutputter();
    		org.w3c.dom.Document domDocument;
			try {
				domDocument = outputter.output(doc);
			
	    		javax.xml.transform.Source xmlSource = new javax.xml.transform.dom.DOMSource(domDocument);	    		
	    	
	    		ByteArrayOutputStream xsltOutStream = new ByteArrayOutputStream();
	    		StreamResult xmlResult = new StreamResult(xsltOutStream);	    		    	
	    	
	    		// Do the transform
				transformer.transform(xmlSource, xmlResult);
				
				// TODO: insert PID into premis:objectIdentifierValue, insert location into <premis:contentLocationValue>
				
				
	    		return xsltOutStream.toString().getBytes("UTF-8");
    		
    		} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();				
    		} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}                                  
        }
        else {
                System.err.println("Error: output cannot be converted to PREMIS format");
        }
		return null;
	}
	
	private byte[] getRelationshipRDF(String archivalObjectId) {

		ArchivalObject archivalObject = archivalObjectDao
				.getArchivalObjectById(archivalObjectId);
		Set<ObjectArtifact> objectArtifactsSet = archivalObject
				.getObjectArtifactsByType("RelationshipObject");
		ObjectArtifact objectArtifact = null;
		RelationshipObject relationshipObject = null;
		List<Relationship> relsList = null;
		Iterator<ObjectArtifact> iter = objectArtifactsSet.iterator();
		while (iter.hasNext()) {
			objectArtifact = (ObjectArtifact) iter.next();
			String objectArtifactId = objectArtifact.getId();
			try {
				relationshipObject = (RelationshipObject) objectStore
						.getObjectArtifact(objectArtifactId);
				
				 List<Relationship> relsListTmp = relationshipObject.getRelationships();
				 int size =relsListTmp.size();
				 boolean result =size == numberOfRelationship;
				 if(result==true){
					 System.out.println("ObjectRelationship type: "+relationshipObject.getType());								
					 relsList=relsListTmp;
				 }
			} catch (ObjectStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("-------------------------------------");

		}

		/*
		 * Start creating relationship RDFs
		 */
		byte[] relsExtByteArray = null;
		ByteArrayOutputStream relsExt_baos = new ByteArrayOutputStream();
		System.out.println("Create FedoraRelsExt object---");
		FedoraRelsExt relsExt;
		if(relsList!=null){
		try {
			// set subject that is the archival object id
			relsExt = new FedoraRelsExt(FedoraNamespace.FEDORA.getURI()
					+ archivalObjectId);

			// System.out.println(FedoraNamespace.FEDORA.getURI());
			Iterator <Relationship> relationshipIter = relsList.iterator();
			// TODO
			/*
			 * perform a check if the subject in triple is the archival object
			 * id If not it must be a relationship between others objects in the
			 * repository?
			 */
			while (relationshipIter.hasNext()) {
				Relationship relation = (Relationship) relationshipIter.next();
				String subj = relation.getSubject();
				String pred = relation.getPredicate();
				String obj = relation.getObject();
				String namespaceURI_predicate = null;
				String brilPrefix =  FedoraNamespace.BRIL.getPrefix();
				String fedoraURI= FedoraNamespace.FEDORA.getURI();
				String prefix_predicate = null;
				System.out.println(subj);
				System.out.println(pred);
				System.out.println(obj);
				System.out.println("===================");

				// check if the predicate is fedora or bril relationship
				if (pred.equals("isPartOf") || pred.equals("hasPart")|| pred.equals("isMemberOf")) {
					prefix_predicate = FedoraNamespace.FEDORA.getPrefix();
					namespaceURI_predicate = FedoraNamespace.FEDORARELSEXT.getURI();
				} else if (pred.equals("wasGeneratedBy") || pred.equals("wasDerivedFrom") || 
								pred.equals("wasControlledBy") || pred.equals("used")) {
					prefix_predicate = FedoraNamespace.OPMV.getPrefix();
					namespaceURI_predicate = FedoraNamespace.OPMV.getURI();
				} else {					
					prefix_predicate = FedoraNamespace.BRILRELS.getPrefix();
					namespaceURI_predicate = FedoraNamespace.BRILRELS.getURI();
				}

				// check if have prefix bril: , remove the prefix
				if (obj.lastIndexOf(':') != -1) {
					obj = obj.substring(obj.lastIndexOf(':') + 1);
				}
			

				QName predicate = new QName(namespaceURI_predicate, pred, prefix_predicate);
			   //System.out.println("object: "+obj);
				QName object = new QName("", obj, fedoraURI
						+ brilPrefix);
				/**
				 * The literal object will not have any prefix
				 */
				if(pred.equals("wasControlledBy")){
					object = new QName("", obj, "");
				}

				/*
				 * check if relationship subject is the same as the
				 * archivalobject then add this to the rds statement for this
				 * archivalobject or object id
				 */
				if(subj.contains("info:fedora/")){
					subj = subj.substring(subj.indexOf("/")+1);
				}
				if (subj.equals(archivalObjectId)) {
				//	System.out.println(predicate);
				//	System.out.println(object);
					relsExt.addRelationship(predicate, object);
				}
				else{
				// If subject is not the current archival object then 
				// search for the object in the repo and add relationship to this object
					System.out.println("addRelationship To Other Fedora Object----");
					// check if have prefix bril: , remove the prefix
					if (!obj.contains("bril:")) {
						obj = "bril:"+obj;
					}	
					addRelationshipToOtherFedoraObject(subj, pred, obj, namespaceURI_predicate);
			
				}
			}
			relsExt.serialize(relsExt_baos, "");

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BrilTransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		relsExtByteArray = relsExt_baos.toByteArray();
		}
		System.out.println("Relationship (relsext) created---");
		return relsExtByteArray;
	}
	/**
	 * Adds rels-exts to other objects in the repository.
	 * 
	 * @param subj object id to which the rels-ext will be added
	 * @param pred predicate or relationship
	 * @param obj object id or literal value for the subj
	 * @param namespaceURI_predicate namespace for the predicate
	 */
	private void addRelationshipToOtherFedoraObject(String subj, String pred, String obj, String namespaceURI_predicate){
		
		String brilPrefix =  FedoraNamespace.BRIL.getPrefix();
		String fedoraURI= FedoraNamespace.FEDORA.getURI();		
		FedoraAdminstrationImpl fedoraAdmin;
		boolean isLiteral = false;

		try {
			//subj must be= info:fedora/bril:6823283273
			System.out.println("current object1: "+ obj);
			fedoraAdmin = new FedoraAdminstrationImpl();
			if(subj.contains("bril:")==false){
				subj= brilPrefix+":"+subj;
			}
			String subject = fedoraURI+subj;
			String predicate = namespaceURI_predicate+pred;
			String object=fedoraURI+obj;
			System.out.println("current object2: "+ obj);
			if(pred.equals("wasGeneratedByTask")|| pred.equals("softwareAgent")){
				isLiteral=true;	
				if(obj.contains("bril:")){
					object=obj.substring(obj.indexOf(":")+1).trim();	
				}else{
				object = obj.trim();
				}
			}
		
			if (fedoraAdmin.hasObject(subj) == true) {
				System.out.println("this id is present: "+ subject+ ", " + object);
				//if its a literal value, check if the relationship is already present
				//only if its not present add the relationship
			/*	if(isLiteral==true){
					//id should be-- bril:847856b0-c065-4da7-a092-a10c9fb04c94
					boolean res = fedoraAdmin.hasRelationship(subj, predicate, object);
					System.out.println(res);
					if (res==false){
						fedoraAdmin.addObjectRelation(subject, predicate, object, isLiteral);
						System.out.println("Relationship added: "+ subject+ " -> " + predicate +" -> "+object);
					}
				}else{*/
				fedoraAdmin.addObjectRelation(subject, predicate, object, isLiteral);
				System.out.println("Relationship added: "+ subject+ " -> " + predicate +" -> "+object);
				//}
				
			}else{
				System.out.println("Cant find this Id: "+ subject);
			}
		} catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private FileFormat getFileFormat(String archivalObjectId) {

		ArchivalObject archivalObject = archivalObjectDao
				.getArchivalObjectById(archivalObjectId);
		/*
		 * Get FileFormat objectartifact from the store
		 */
		Set<ObjectArtifact> objectArtifacts = archivalObject
				.getObjectArtifactsByType("FileFormat");
		ObjectArtifact objectArtifact = null;
		FileFormat fileFormat = null;
		Iterator<ObjectArtifact> iter = objectArtifacts.iterator();
		while (iter.hasNext()) {
			objectArtifact = (ObjectArtifact) iter.next();
			String objectArtifactId = objectArtifact.getId();
			try {
				fileFormat = (FileFormat) objectStore
						.getObjectArtifact(objectArtifactId);
			} catch (ObjectStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return fileFormat;

	}
	
	private void purgeIfPresentInRepository(String identifier){
		
		FedoraHandler handler;
		boolean result =false;

		try {
			handler = new FedoraHandler();
			try {
				result = handler.hasObject(identifier);
				
				System.out.println("purge:" +result);
				if (result==true){
					handler.purgeObject(identifier, "purge to update", false);	
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (BrilObjectRepositoryException e) {
		}
	}
	
	public boolean getCompletionFlag(){
		return flag;
	}
}