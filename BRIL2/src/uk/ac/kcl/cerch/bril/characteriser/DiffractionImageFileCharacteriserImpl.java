package uk.ac.kcl.cerch.bril.characteriser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

//import org.xml.sax.InputSource;

import uk.ac.kcl.cerch.bril.ccp4.processor.DiffractionImageProcessor;
//import uk.ac.kcl.cerch.bril.fileformat.CrystallographyFileFormat;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacterisation;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacteriserException;

public class DiffractionImageFileCharacteriserImpl implements
		DiffractionImageFileCharacteriser {

	@Override
	public FileCharacterisation characteriseFile(File file)
			throws FileCharacteriserException {

		FileCharacterisation fileCharacterisation = null;
		fileCharacterisation = new DiffractionImageFileCharacterisation();
		// TODO
		DiffractionImageProcessor processor = new DiffractionImageProcessor();
		processor.runDiffDump(file.getPath());
		String diffSetIdentifier = getDiffSetIdentifier(file.getName());
		String diffImageIdentifier = getDiffImgIdentifier(file.getName());
	   //Set the diff image set identifier and image id for this set.
		((DiffractionImageFileCharacterisation)fileCharacterisation).setDiffImageSetIdentifier(diffSetIdentifier);		
		((DiffractionImageFileCharacterisation)fileCharacterisation).setDiffImageIdentifier(diffImageIdentifier);
		
		Map<String, String> metadata = processor.getDiffractionMetadataAsMap();
		Map<DiffractionImageSetElement, String> newDiffSetMetadata = new HashMap<DiffractionImageSetElement, String> ();
		Map<DiffractionImageElement, String> diffImageMetadata = new HashMap<DiffractionImageElement, String> ();
		System.out.println(metadata);
		newDiffSetMetadata.put(DiffractionImageSetElement.DIFF_ATTRI_ID, diffSetIdentifier);
		diffImageMetadata.put(DiffractionImageElement.DIFF_IMG_ID, diffImageIdentifier);
		for (Map.Entry<String, String> entry : metadata.entrySet()) {
			//System.out.println(entry.getKey());
			//System.out.println(entry.getValue());
			String key = entry.getKey().trim();
			String value =entry.getValue().trim();
			
			if (key.equals("Collection date")){
				//System.out.println(value);
				newDiffSetMetadata.put(DiffractionImageSetElement.DETECTOR_TYPE_COLLECTION_DATE, value);
				diffImageMetadata.put(DiffractionImageElement.DIFF_DATE_TIME, value);
			}
			/*Filter 
			 * key =Oscillation (phi) 
             * value = 206.000000 -> 207.000000 deg
             * */
			if(key.equals("Oscillation (phi)")){
				int dash = value.lastIndexOf('-');
				int greaterthen = value.lastIndexOf('>');
				int deg = value.lastIndexOf("deg");
				String range1 = value.substring(0, dash-1);
				String range2 = value.substring(greaterthen+1,deg-1);
				float deg1= Float.valueOf(range1).floatValue();
				float deg2= Float.valueOf(range2).floatValue();
				float range = deg2-deg1; 
				//System.out.println(value);
				newDiffSetMetadata.put(DiffractionImageSetElement.MEASUREMENT_OSC_RANGE, Float.toString(range)+" deg");
				diffImageMetadata.put(DiffractionImageElement.DIFF_OSC_START,  Float.toString(deg1).trim());
			}
			
			if(key.equals("Wavelength".trim())){
				//System.out.println(value);
				newDiffSetMetadata.put(DiffractionImageSetElement.SOURCE_WAVELENGTH, value);
				newDiffSetMetadata.put(DiffractionImageSetElement.SOURCE_SOURCE, "");
			}
			/*Filter:
			 * key= Beam center 
			 * value= (157.354004 mm,157.591003 mm)
			 * */
			if(key.equals("Beam center")){
				int openbrac = value.lastIndexOf('(');
				int closedbrac = value.lastIndexOf(')');
				int comma = value.lastIndexOf(',');
				String x = value.substring(openbrac+1, comma);
				String y = value.substring(comma+1, closedbrac);
				
				newDiffSetMetadata.put(DiffractionImageSetElement.MEASUREMENT_BEAM_CENTRE_X, x);
				newDiffSetMetadata.put(DiffractionImageSetElement.MEASUREMENT_BEAM_CENTRE_Y, y);
			}
			if(key.equals("Distance to detector")){
				newDiffSetMetadata.put(DiffractionImageSetElement.MEASUREMENT_DETECTOR_DISTANCE, value);
			}

			if(key.equals("Manufacturer")){
				newDiffSetMetadata.put(DiffractionImageSetElement.DETECTOR_TYPE_DETECTOR, value);
			}
			if(key.equals("Detector S/N")){
				newDiffSetMetadata.put(DiffractionImageSetElement.DETECTOR_TYPE_TYPE, value);
			}
			//put all the elements for parent element <diffraction_data> as null;
			newDiffSetMetadata.put(DiffractionImageSetElement.DIFF_TYPE_CRYSTAL_ID, "");
			newDiffSetMetadata.put(DiffractionImageSetElement.DIFF_TYPE_CRYSTAL_SUPPORT, "");
			newDiffSetMetadata.put(DiffractionImageSetElement.DIFF_TYPE_DETAILS,"");
			newDiffSetMetadata.put(DiffractionImageSetElement.DIFF_TYPE_AMBIENT_TEMP, "");
			newDiffSetMetadata.put(DiffractionImageSetElement.DIFF_TYPE_AMBIENT_TEMP_DETAILS, "");
			
			//put the remaining elements to diff image metadata
			
			if(key.equals("Pixel Size")){
				int openbrac = value.lastIndexOf('(');
				int comma = value.lastIndexOf(',');
				String pixel = value.substring(openbrac+1, comma);
				diffImageMetadata.put(DiffractionImageElement.DIFF_PIXEL_SIZE, pixel);
			}
			if(key.equals("Image Size")){
				int openbrac = value.lastIndexOf('(');
				int comma = value.lastIndexOf(',');
				String size = value.substring(openbrac+1, comma);
				diffImageMetadata.put(DiffractionImageElement.DIFF_IMAGE_SIZE, size);
			}
			if(key.equals("Exposure time")){
				diffImageMetadata.put(DiffractionImageElement.DIFF_EXPOSER_TIME, value);
			}
		
		}
		try {
			
			String metadataXML= runXMLCreation(newDiffSetMetadata, diffImageMetadata);
			fileCharacterisation.setMetadata(metadataXML);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return fileCharacterisation;
	}

	public String runXMLCreation(Map<DiffractionImageSetElement,String> diffImageSetElement, Map<DiffractionImageElement,String> diffImageElement) throws Exception {
		OutputStream out =new ByteArrayOutputStream();
		String diffSetId = diffImageSetElement.get(DiffractionImageSetElement.DIFF_ATTRI_ID);
		String detector = diffImageSetElement.get(DiffractionImageSetElement.DETECTOR_TYPE_DETECTOR); 
		String type = diffImageSetElement.get(DiffractionImageSetElement.DETECTOR_TYPE_TYPE); 
		String collectiondate = diffImageSetElement.get(DiffractionImageSetElement.DETECTOR_TYPE_COLLECTION_DATE); 
		String distance = diffImageSetElement.get(DiffractionImageSetElement.MEASUREMENT_DETECTOR_DISTANCE); 
		String beamx = diffImageSetElement.get(DiffractionImageSetElement.MEASUREMENT_BEAM_CENTRE_X); 
		String beamy = diffImageSetElement.get(DiffractionImageSetElement.MEASUREMENT_BEAM_CENTRE_Y); 
		String oscrange = diffImageSetElement.get(DiffractionImageSetElement.MEASUREMENT_OSC_RANGE); 
		String source =diffImageSetElement.get(DiffractionImageSetElement.SOURCE_SOURCE); 
		String wavelength =diffImageSetElement.get(DiffractionImageSetElement.SOURCE_WAVELENGTH); 
		
		String diffImgId =diffImageElement.get(DiffractionImageElement.DIFF_IMG_ID);
		String exposure =diffImageElement.get(DiffractionImageElement.DIFF_EXPOSER_TIME);
		String datetime =diffImageElement.get(DiffractionImageElement.DIFF_DATE_TIME);
		String oscstart=diffImageElement.get(DiffractionImageElement.DIFF_OSC_START);
		String imgsize=diffImageElement.get(DiffractionImageElement.DIFF_IMAGE_SIZE);
		String pixelsize=diffImageElement.get(DiffractionImageElement.DIFF_PIXEL_SIZE);
		String theta= diffImageElement.get(DiffractionImageElement.DIFF_TWO_THETA_VALUE);
		
		//System.out.println(diffSetId+ detector +", "+ type+ ", "+ collectiondate);
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = outputFactory
				.createXMLStreamWriter(out);
		String data_collectionURI = "http://cerch.kcl.ac.uk/bril/schema/data_collection";
		String data_collectionPrefix= "diffset";
		writer.setDefaultNamespace( data_collectionURI );
		writer.writeStartDocument();
		
		writer.writeStartElement( data_collectionURI, data_collectionPrefix+":"+"diffraction_dataset" );
		
		writer.writeNamespace( data_collectionPrefix, data_collectionURI);
        //Start element  </diffraction_data>
		writer.writeStartElement(data_collectionURI, "diffraction_data");
        // Write the id attribute for <diffraction_data>
        writer.writeAttribute("id", diffSetId);
        writer.writeStartElement(data_collectionURI,DiffractionImageSetElement.DIFF_TYPE_CRYSTAL_ID.localName());
        //writer.writeCharacters("001");
        writer.writeEndElement();       
        
        writer.writeStartElement(data_collectionURI,DiffractionImageSetElement.DIFF_TYPE_CRYSTAL_SUPPORT.localName());
        //writer.writeCharacters("");
        writer.writeEndElement();
        
        writer.writeStartElement(data_collectionURI,DiffractionImageSetElement.DIFF_TYPE_DETAILS.localName());
        //writer.writeCharacters("");
        writer.writeEndElement();
        //Close element </diffraction_data>
        writer.writeEndElement();
        
     // Start element <diffraction_detector> with diffrn_id attribute
        writer.writeStartElement(data_collectionURI, "diffraction_detector");
        writer.writeAttribute("diffrn_id", diffSetId);
        //add child element detector to <diffraction_detector>
        writer.writeStartElement(data_collectionURI, DiffractionImageSetElement.DETECTOR_TYPE_DETECTOR.localName());
        writer.writeCharacters( detector);
        writer.writeEndElement();  
      //add child element <type> to <diffraction_detector>
        writer.writeStartElement(data_collectionURI, DiffractionImageSetElement.DETECTOR_TYPE_TYPE.localName());
        writer.writeCharacters(type);
        writer.writeEndElement(); 
        //add child element collection_date to <diffraction_detector>
        writer.writeStartElement(data_collectionURI, DiffractionImageSetElement.DETECTOR_TYPE_COLLECTION_DATE.localName());
        writer.writeCharacters(collectiondate);
        writer.writeEndElement();
        //Close element </diffraction_detector>
        writer.writeEndElement();
        
        // Start element <diffraction_source> with diffrn_id attribute
        writer.writeStartElement(data_collectionURI, "diffraction_source");
        writer.writeAttribute("diffrn_id", diffSetId);
        //add child element detector to <diffraction_detector>
        writer.writeStartElement(data_collectionURI, DiffractionImageSetElement.SOURCE_SOURCE.localName());
        writer.writeCharacters(source);
        writer.writeEndElement();  
      //add child element <type> to <diffraction_detector>
        writer.writeStartElement(data_collectionURI, DiffractionImageSetElement.SOURCE_WAVELENGTH.localName());
        writer.writeCharacters(wavelength);
        writer.writeEndElement(); 
        //Close element </diffraction_source>
        writer.writeEndElement();
       
        // Start element <diffraction_measurement>
        writer.writeStartElement(data_collectionURI, "diffraction_measurement");
        writer.writeAttribute("diffrn_id", diffSetId);
        //add child element beam_centre_X to <diffraction_measurement>
        writer.writeStartElement(data_collectionURI, DiffractionImageSetElement.MEASUREMENT_DETECTOR_DISTANCE.localName());
        writer.writeCharacters(distance);
        writer.writeEndElement();  
      //add child element beam_centre_X to <diffraction_measurement>
        writer.writeStartElement(data_collectionURI,DiffractionImageSetElement.MEASUREMENT_BEAM_CENTRE_X.localName() );
        writer.writeCharacters(beamx);
        writer.writeEndElement();  
      //add child element beam_centre_Y to <diffraction_measurement>
        writer.writeStartElement(data_collectionURI, DiffractionImageSetElement.MEASUREMENT_BEAM_CENTRE_Y.localName());
        writer.writeCharacters(beamy);
        writer.writeEndElement();  
      //add child element OSC_range to <diffraction_measurement>
        writer.writeStartElement(data_collectionURI,DiffractionImageSetElement.MEASUREMENT_OSC_RANGE.localName());
        writer.writeCharacters(oscrange);
        writer.writeEndElement();
        //close element </diffraction_measurement>
        writer.writeEndElement(); 
        
        
        // Start element <diffraction_Image>
        writer.writeStartElement(data_collectionURI, "diffraction_image");
        writer.writeAttribute(DiffractionImageElement.DIFF_IMG_ID.localName(), diffImgId);
      //add child element diffrnImageSet_id to <diffraction_Image>
        writer.writeStartElement(data_collectionURI, "diffrnset_id");
        writer.writeCharacters(diffSetId);
        writer.writeEndElement();
        //add child element date_time to <diffraction_Image>
        writer.writeStartElement(data_collectionURI, DiffractionImageElement.DIFF_DATE_TIME.localName());
        writer.writeCharacters(datetime);
        writer.writeEndElement();  
      //add child element exposure_time to <diffraction_Image>
        writer.writeStartElement(data_collectionURI,DiffractionImageElement.DIFF_EXPOSER_TIME.localName() );
        writer.writeCharacters(exposure);
        writer.writeEndElement();  
      //add child element osc_start to <diffraction_Image>
        writer.writeStartElement(data_collectionURI, DiffractionImageElement.DIFF_OSC_START.localName());
        writer.writeCharacters(oscstart);
        writer.writeEndElement();  
      //add child element image_size to <diffraction_Image>
        writer.writeStartElement(data_collectionURI,DiffractionImageElement.DIFF_IMAGE_SIZE.localName());
        writer.writeCharacters(imgsize);
        writer.writeEndElement();
        //add child element pixel_size to <diffraction_Image>
        writer.writeStartElement(data_collectionURI,DiffractionImageElement.DIFF_PIXEL_SIZE.localName());
        writer.writeCharacters(pixelsize);
        writer.writeEndElement();
        //add child element theta to <diffraction_Image>
        writer.writeStartElement(data_collectionURI,DiffractionImageElement.DIFF_TWO_THETA_VALUE.localName());
        writer.writeCharacters(theta);
        writer.writeEndElement();
        //close element </diffraction_measurement>
        writer.writeEndElement(); 
        
        //close element </diffraction_dataset>
        writer.writeEndElement();
        // flush and close
        writer.flush();
        writer.close();
        return out.toString();
      //System.out.println(out.toString());
    
	}
	/*
	 * It is assumed in Mosflm that the image conform to a naming convention where
	 * the image name is made up of three parts, an identifier, a three digit number and 
	 * an extension.
	 * Identifier can be 40 characters long 
	 * and should be separated from the digit number by a hyphen(-) or underscore(_) 
	 * example of valid image filenames are:
	 * 
	 * lysozoyme_cryst1_021.image
	 * catx1_001.img
	 * f1_tray42_wellb6_001.osc
	 * h2-2MS_1_001.img
	 * 
	 * note: identifier refers to a crystal of a component for a particular experiment.
	 * One or more crystal of same component can be used to get a set of images for the experiment.
	 * 
	 * storing this info would enable to separate  
	 * sets of diffraction images produced for the experiment.
	 * 
	 * 
	 * */
	private String getDiffSetIdentifier(String fileName){
		int lastHyphen=fileName.lastIndexOf('-');
		int lastUnderscore=fileName.lastIndexOf('_');
		
		String identifier=null;
	
		if(lastUnderscore!=-1 && lastHyphen<lastUnderscore){
			identifier= fileName.substring(0,lastUnderscore);
			//System.out.println(identifier);
		}
		if (lastHyphen!=-1 && lastUnderscore<lastHyphen){
			identifier= fileName.substring(0,lastHyphen-1);
			//System.out.println(identifier);
		}
		return identifier;
		//System.out.println (identifier+" &&&&&& "+imageNumber);
		
	}
	private String getDiffImgIdentifier(String fileName){
		int dot = fileName.lastIndexOf('.');
		int lastHyphen=fileName.lastIndexOf('-');
		int lastUnderscore=fileName.lastIndexOf('_');
		String imageNumber=null;
	
		if(lastUnderscore!=-1 && lastHyphen<lastUnderscore){
			
			imageNumber = fileName.substring(lastUnderscore+1, dot);	
		}
		if (lastHyphen!=-1 && lastUnderscore<lastHyphen){
			
			imageNumber = fileName.substring(lastHyphen+1, dot);
		}
		return imageNumber;
		//System.out.println (identifier+" &&&&&& "+imageNumber);
		
	}

}
