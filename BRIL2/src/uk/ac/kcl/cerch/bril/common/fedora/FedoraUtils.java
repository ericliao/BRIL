package uk.ac.kcl.cerch.bril.common.fedora;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.configuration.ConfigurationException;
import org.xml.sax.SAXException;

import uk.ac.kcl.cerch.bril.common.fedora.FedoraFoxmlDocument.LocationType;
import uk.ac.kcl.cerch.bril.common.types.*;
//import uk.ac.kcl.cerch.bril.common.util.FileUtil;
import uk.ac.kcl.cerch.bril.common.metadata.DublinCore;

/**
 * This class handles the construction of Fedora Digital Object XML (fedora like
 * mets) from BrilDigitalObject
 */
public class FedoraUtils {
	private static FedoraHandler fedoraHandler;
	private String contentLocation;

	/**
	 * 
	 * @param datastreamObject
	 * @throws IOException
	 * @throws ServiceException
	 * @throws ConfigurationException
	 * @throws BrilTransformException
	 * @throws BrilObjectRepositoryException
	 * @throws SAXException
	 * @throws XPathExpressionException
	 */

	public byte[] DataStreamObjectToFoxml(
			DatastreamObjectContainer datastreamObject)
			throws BrilTransformException, BrilObjectRepositoryException {
		List<String> pid = new ArrayList<String>(1);

		fedoraHandler = new FedoraHandler();

		if (null == datastreamObject.getIdentifier()
				|| datastreamObject.getIdentifier().equals("")) {
			// log.warn(
			// "Could not find identifier for DatastreamObjectContainer" );
			// log.info( "Obtaining new pid for DatastreamObjectContainer" );

			String prefix = datastreamObject.getDatastreamObject(
					DataStreamType.OriginalData).getSubmitter();
			try {

				pid = Arrays.asList(fedoraHandler.getNextPID(1, prefix));

			}

			catch (IOException ex) {
				String error = String.format(
						"Could not retrieve new pid from namespace %s: %s",
						prefix, ex.getMessage());
				// log.error( error );
				throw new BrilObjectRepositoryException(error, ex);
			} catch (IllegalStateException ex) {
				String error = String.format(
						"Could not retrieve new pid from namespace %s: %s",
						prefix, ex.getMessage());
				// log.error( error );
				throw new BrilObjectRepositoryException(error, ex);
			}
			if (null == pid && 1 != pid.size()) {
				// log.warn( String.format(
				// "pid is empty for namespace '%s', but no exception was caught.",
				// prefix ) );
				return null;
			}

			datastreamObject.setIdentifier(pid.get(0));
		} else {
			pid.add(datastreamObject.getIdentifier());
		}

		System.out.println("pid: " + pid.get(0));
		FedoraFoxmlDocument foxml = null;
		try {
			// object properties
			// state,pid,label,owner,timestamp
			foxml = new FedoraFoxmlDocument(FedoraFoxmlDocument.State.I, pid
					.get(0), datastreamObject.getDatastreamObject(
					DataStreamType.OriginalData).getFormat(), datastreamObject
					.getDatastreamObject(DataStreamType.OriginalData)
					.getSubmitter(),

			System.currentTimeMillis());
		} catch (ParserConfigurationException ex) {
			String error = String.format(
					"Failed to construct fedora xml with pid %s", pid.get(0));
			// log.error( error );
			System.out.println(ex + ": error");
			// throw new ParserConfigurationException( error, ex );
		}
		// get normal data from objectcontainer
		/*
		 * int datastream_count = datastreamObject.getDatastreamObjectCount();
		 * // List< ? extends Pair< Integer, String > > ordering =
		 * getOrderedMapping( cargo ); for( int i = 0; i < datastream_count; i++
		 * ) { DatastreamObject c = datastreamObject.getDatastreamObjects().get(
		 * i ); try { foxml.addBinaryContent( ordering.get( i ).getSecond(),
		 * c.getDataBytes(), c.getFormat(), c.getMimetype(), c.getTimestamp() );
		 * } catch( IOException ex ) { String error = String.format(
		 * "Failed to add binary data to foxml from cargoobject %s",
		 * c.getDataStreamType().getName() ); // log.error( error , ex); throw
		 * new ObjectRepositoryException( error, ex ); } catch(
		 * XPathExpressionException ex ) { String error = String.format(
		 * "Failed to add binary data to foxml from cargoobject %s",
		 * c.getDataStreamType().getName() ); // log.error( error , ex); throw
		 * new ObjectRepositoryException( error, ex ); } }
		 */

		// get DublinCore from DatastreamObjectContainer
		if (datastreamObject.hasDublinCoreMetadata(DataStreamType.DublinCore)) {
			DublinCore meta = datastreamObject.getDublinCoreMetaData();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			meta.serialize(baos, "");
			int len = baos.toByteArray().length;
			String dcString = new String(baos.toByteArray());

			System.out.println("length of dc xml content: " + len);
			//System.out.println("String of dc xml content: " + dcString);
			try {
				// log.trace( "DublinCore output from serialization: "+new
				// String( baos.toByteArray() ) );
				foxml.addDublinCoreDatastream(new String(baos.toByteArray()),
						System.currentTimeMillis());
			} catch (XPathExpressionException ex) {
				String error = String
						.format(
								"With id %s; Failed to add metadata to foxml from MetaData\" %s\"",
								meta.getIdentifier(), new String(baos
										.toByteArray()));
				// log.error( error , ex);
				throw new BrilObjectRepositoryException(error, ex);
			} catch (SAXException ex) {
				String error = String
						.format(
								"With id %s; Failed to add metadata to foxml from MetaData\" %s\"",
								meta.getIdentifier(), new String(baos
										.toByteArray()));
				// log.error( error , ex);
				throw new BrilObjectRepositoryException(error, ex);
			} catch (IOException ex) {
				String error = String
						.format(
								"With id %s; Failed to add metadata to foxml from MetaData\" %s\"",
								meta.getIdentifier(), new String(baos
										.toByteArray()));
				// log.error( error , ex);
				throw new BrilObjectRepositoryException(error, ex);
			}
		} 	// get RelsExt datastream from DatastreamObjectContainer
			if (datastreamObject.hasDatastream(DataStreamType.RelsExt)) {
				DatastreamObject dso = datastreamObject
						.getDatastreamObject(DataStreamType.RelsExt);

				String relsextData = new String(dso.getDataBytes());
				System.out.println("id: " + dso.getId());
				System.out.println("relsext: " + relsextData);
				try {
					// log.trace( "DublinCore output from serialization: "+new
					// String( baos.toByteArray() ) );
					foxml.addRelsExtDatastream(relsextData, System
							.currentTimeMillis());
				} catch (XPathExpressionException ex) {
					String error = String
							.format(
									"With id %s; Failed to add relsext metadata to foxml from DatastreamObject\" %s\"",
									dso.getId(), relsextData);
					// log.error( error , ex);
					throw new BrilObjectRepositoryException(error, ex);
				} catch (SAXException ex) {
					String error = String
							.format(
									"With id %s; Failed to add relsext metadata to foxml from DatastreamObject\" %s\"",
									dso.getId(), relsextData);
					// log.error( error , ex);
					throw new BrilObjectRepositoryException(error, ex);
				} catch (IOException ex) {
					String error = String
							.format(
									"With id %s; Failed to add relsext metadata to foxml from DatastreamObject\" %s\"",
									dso.getId(), relsextData);
					// log.error( error , ex);
					throw new BrilObjectRepositoryException(error, ex);
				}

			}
			//Get ObjectMetadata from DatastreamObjectContainer
			if (datastreamObject.hasDatastream(DataStreamType.ObjectMetadata)) {
				DatastreamObject dso = datastreamObject
						.getDatastreamObject(DataStreamType.ObjectMetadata);

				byte[] objectmetdata = dso.getDataBytes();
				try {
					System.out.println("format of metadata:  -----"
							+ dso.getFormat());
					//(String datastreamId, String xmlContent, String label, long timenow, boolean versionable)
					foxml.addXmlContent("BRILMETA", new String(objectmetdata),
							String.format("BRIL Object Type %s Metadata Record", dso.getFormat()),
							System.currentTimeMillis(), true);
				} catch (XPathExpressionException ex) {
					String error = String
							.format(
									"With id %s; Failed to add bril object metadata to foxml from DatastreamObject\" %s\"",
									dso.getId(), new String(dso.getDataBytes()));
					// log.error( error , ex);
					throw new BrilObjectRepositoryException(error, ex);
				} catch (SAXException ex) {
					String error = String
							.format(
									"With id %s; Failed to add bril object metadata to foxml from DatastreamObject\" %s\"",
									dso.getId(), new String(dso.getDataBytes()));
					// log.error( error , ex);
					throw new BrilObjectRepositoryException(error, ex);
				} catch (IOException ex) {
					String error = String
							.format(
									"With id %s; Failed to add bril object metadata to foxml from DatastreamObject\" %s\"",
									dso.getId(), new String(dso.getDataBytes()));
					// log.error( error , ex);
					throw new BrilObjectRepositoryException(error, ex);
				}
			}
			
			// Get PREMIS metadata from DatastreamObjectContainer
			if (datastreamObject.hasDatastream(DataStreamType.PremisMetadata)) {
				DatastreamObject dso = datastreamObject
						.getDatastreamObject(DataStreamType.PremisMetadata);

				byte[] PREMISmetadata = dso.getDataBytes();
				try {
					System.out.println("format of metadata:  -----"
							+ dso.getFormat());
					//(String datastreamId, String xmlContent, String label, long timenow, boolean versionable)
					foxml.addXmlContent("PREMIS", new String(PREMISmetadata),
							String.format("PREMIS Object Metadata Record"),
							System.currentTimeMillis(), true);
				} catch (XPathExpressionException ex) {
					String error = String
							.format(
									"With id %s; Failed to add PREMIS object metadata to foxml from DatastreamObject\" %s\"",
									dso.getId(), new String(dso.getDataBytes()));
					// log.error( error , ex);
					throw new BrilObjectRepositoryException(error, ex);
				} catch (SAXException ex) {
					String error = String
							.format(
									"With id %s; Failed to add PREMIS object metadata to foxml from DatastreamObject\" %s\"",
									dso.getId(), new String(dso.getDataBytes()));
					// log.error( error , ex);
					throw new BrilObjectRepositoryException(error, ex);
				} catch (IOException ex) {
					String error = String
							.format(
									"With id %s; Failed to add PREMIS object metadata to foxml from DatastreamObject\" %s\"",
									dso.getId(), new String(dso.getDataBytes()));
					// log.error( error , ex);
					throw new BrilObjectRepositoryException(error, ex);
				}
			}
			
			if (datastreamObject.hasDatastream(DataStreamType.OriginalData)) {
				DatastreamObject dso = datastreamObject
						.getDatastreamObject(DataStreamType.OriginalData);

				byte[] byteArray = dso.getDataBytes();
				//if(byteArray!=null){
				//if the location is set at fedoraAdministration
				//and thus not null then the location is added to foxml
				if(getContentLocation()!=null){
				//check if getContentLocation is an external URL or INTERNAL_ID.
				 String refValue= getContentLocation();
				 LocationType locationType = null;
				 
			      if (refValue.indexOf("http://")<0){
			    	  
			    	  locationType=LocationType.INTERNAL_ID;
			      }else{
			    	  locationType=LocationType.URL;
			      }
				// put data in a url location?
				 System.out.println("going to ADDCONTENT LOCATION---"+ refValue+": "+locationType);
				if (byteArray != null) {
					try {
						// addContentLocation(String datastreamId, String ref,
						// String label, String mimetype, LocationType type,
						// long timenow)
						// String.format( "Metadata: %s", "BRILMETA")
						foxml.addContentLocation("MYDS", refValue, String
								.format("Original data: %s", dso.getFormat()),
								dso.getMimetype(),locationType, System
										.currentTimeMillis());
					//	 System.out.println("CONTENT LOCATION ADDED ---");

					} catch (XPathExpressionException ex) {
						String error = String
								.format(
										"With id %s; Failed to add bril object to foxml from DatastreamObject\" %s\"",
										dso.getId(), new String(dso
												.getDataBytes()));
						// log.error( error , ex);
						throw new BrilObjectRepositoryException(error, ex);
					} catch (SAXException ex) {
						String error = String
								.format(
										"With id %s; Failed to add bril object to foxml from DatastreamObject\" %s\"",
										dso.getId(), new String(dso
												.getDataBytes()));
						// log.error( error , ex);
						throw new BrilObjectRepositoryException(error, ex);
					} catch (IOException ex) {
						String error = String
								.format(
										"With id %s; Failed to add bril object to foxml from DatastreamObject\" %s\"",
										dso.getId(), new String(dso
												.getDataBytes()));
						// log.error( error , ex);
						throw new BrilObjectRepositoryException(error, ex);
					}
				}
				}
			}

		

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			foxml.serializeDocument(baos, null);
		} catch (TransformerConfigurationException ex) {
			String error = String.format(
					"Failed to construct foxml XML Document: %s", ex
							.getMessage());
			// log.error( error );
			throw new BrilTransformException(error, ex);
		} catch (TransformerException ex) {
			String error = String.format(
					"Failed to construct foxml XML Document: %s", ex
							.getMessage());
			// log.error( error );
			throw new BrilTransformException(error, ex);
		} catch (SAXException ex) {
			String error = String.format(
					"Failed to construct foxml XML Document: %s", ex
							.getMessage());
			// / log.error( error );
			throw new BrilTransformException(error, ex);
		} catch (IOException ex) {
			String error = String.format(
					"Failed to construct foxml XML Document: %s", ex
							.getMessage());
			// log.error( error );
			throw new BrilTransformException(error, ex);
		}

		return baos.toByteArray();
	}
	
	public void setContentLocation(String contentURL){
		this.contentLocation = contentURL;
	}
	
	public String getContentLocation(){
		return contentLocation;
	}

}