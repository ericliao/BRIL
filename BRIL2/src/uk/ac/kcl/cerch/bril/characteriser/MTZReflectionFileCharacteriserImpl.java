package uk.ac.kcl.cerch.bril.characteriser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import uk.ac.kcl.cerch.bril.ccp4.processor.ReflectionFileProcessor;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacterisation;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacteriserException;

public class MTZReflectionFileCharacteriserImpl implements
		MTZReflectionFileCharacteriser {

	@Override
	public FileCharacterisation characteriseFile(File file)
			throws FileCharacteriserException {
		FileCharacterisation fileCharacterisation = null;
		fileCharacterisation = new MTZReflectionFileCharacterisation();
		// TODO
		ReflectionFileProcessor processor = new ReflectionFileProcessor();
		processor.runMTZdmpHeader(file.getPath());
		Map<String, String> metadata = processor.getMTZMetadata();
		System.out.println("--------------**-----------------------");
		System.out.println(metadata);
		System.out.println("--------------**-----------------------");
		Map<MTZReflectionFileElement, String> mtzReflectionMetadata = new HashMap<MTZReflectionFileElement, String>();
		for (Map.Entry<String, String> entry : metadata.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			

			if (key.equals("dataset name")) {
				mtzReflectionMetadata.put(
						MTZReflectionFileElement.MTZ_DATASET_NAME, value);

			}
			if (key.toLowerCase().equals("history for current mtz file")) {
				mtzReflectionMetadata.put(MTZReflectionFileElement.MTZ_HISTORY,
						value);

				// if the value has string fraction get the characters after
				// this string.
				StringTokenizer st = new StringTokenizer(value);
				boolean frac = false;
				int count = 0;
				String fraction = "";
				String program = "";
				while (st.hasMoreTokens()) {
					String word = st.nextToken();
					if (count == 1) {
						program = word;
					}
					if (word.equals("fraction")) {
						frac = true;
					}
					if (frac == true) {
						fraction = word;
					}
					count++;
				}

				if (fraction != null && fraction.lastIndexOf('.') != -1) {
					System.out.println(fraction);
					mtzReflectionMetadata
							.put(
									MTZReflectionFileElement.MTZ_R_FREE_FLAG_PERCENTAGE,
									fraction);
				}
				if (program != null) {
					mtzReflectionMetadata.put(
							MTZReflectionFileElement.MTZ_FROM_PROGRAM, program);
				}
			}
			if (key.equals("crystal name")) {
				mtzReflectionMetadata.put(
						MTZReflectionFileElement.MTZ_CRYSTAL_NAME, value);
			}

			if (key.equals("Number of Batches")) {
				mtzReflectionMetadata.put(
						MTZReflectionFileElement.MTZ_NUMBER_OF_BATCHES, value);
				mtzReflectionMetadata.put(
						MTZReflectionFileElement.MTZ_REFLECTION_TYPE,
						"unmerged");

			}
			if (key.equals("wavelength")) {
				mtzReflectionMetadata.put(
						MTZReflectionFileElement.MTZ_WAVELENGTH, value);
			}
			if (key.equals("Number of Reflections")) {
				mtzReflectionMetadata.put(
						MTZReflectionFileElement.MTZ_NUMBER_OF_REFLECTION,
						value);
			}

			if (key.equals("Column Labels")) {
				mtzReflectionMetadata.put(
						MTZReflectionFileElement.MTZ_COLUMN_LABELS, value);

			}
			if (key.equals("Resolution Range")) {
				mtzReflectionMetadata.put(
						MTZReflectionFileElement.MTZ_RESOLUTION_RANGE, value);

			}
			if (key.equals("Cell Dimensions")) {
				mtzReflectionMetadata.put(
						MTZReflectionFileElement.MTZ_CELL_DIMENSION, value);

			}
			if (key.equals("Space group")) {
				mtzReflectionMetadata.put(
						MTZReflectionFileElement.MTZ_SPACE_GROUP, value);
			}
			
				
		}
		//If 'Number of Batches' key is not present in the map then it must be a merged reflection file
    	if(mtzReflectionMetadata.containsKey(MTZReflectionFileElement.MTZ_NUMBER_OF_BATCHES)==false){
			mtzReflectionMetadata.put(
					MTZReflectionFileElement.MTZ_REFLECTION_TYPE, "merged");
		}
		try {

			String data = runXMLCreation(mtzReflectionMetadata);
			fileCharacterisation.setMetadata(data);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fileCharacterisation;
	}

	private String runXMLCreation(
			Map<MTZReflectionFileElement, String> mtzReflectionFileElement)
			throws Exception {
		OutputStream out = new ByteArrayOutputStream();
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = outputFactory.createXMLStreamWriter(out);
		String reflection_dataURI = "http://cerch.kcl.ac.uk/bril/schema/reflection_data";
		String reflection_dataPrefix = "mtzref";
		writer.setDefaultNamespace(reflection_dataURI);
		writer.writeStartDocument();

		// start element <mtz_reflection>
		writer.writeStartElement(reflection_dataURI, reflection_dataPrefix+ ":" + "mtz_reflection");
		writer.writeNamespace( reflection_dataPrefix, reflection_dataURI);
		writer.writeStartElement(reflection_dataURI,
				MTZReflectionFileElement.MTZ_CRYSTAL_NAME.localName());
		writer.writeCharacters(mtzReflectionFileElement
				.get(MTZReflectionFileElement.MTZ_CRYSTAL_NAME));
		writer.writeEndElement();

		writer.writeStartElement(reflection_dataURI,
				MTZReflectionFileElement.MTZ_DATASET_NAME.localName());
		writer.writeCharacters(mtzReflectionFileElement
				.get(MTZReflectionFileElement.MTZ_DATASET_NAME));
		writer.writeEndElement();

		writer.writeStartElement(reflection_dataURI,
				MTZReflectionFileElement.MTZ_WAVELENGTH.localName());
		writer.writeCharacters(mtzReflectionFileElement
				.get(MTZReflectionFileElement.MTZ_WAVELENGTH));
		writer.writeEndElement();

		writer.writeStartElement(reflection_dataURI,
				MTZReflectionFileElement.MTZ_HISTORY.localName());
		writer.writeCharacters(mtzReflectionFileElement
				.get(MTZReflectionFileElement.MTZ_HISTORY));
		writer.writeEndElement();

		writer.writeStartElement(reflection_dataURI,
				MTZReflectionFileElement.MTZ_FROM_PROGRAM.localName());
		writer.writeCharacters(mtzReflectionFileElement
				.get(MTZReflectionFileElement.MTZ_FROM_PROGRAM));
		writer.writeEndElement();
		String noOfBatches = mtzReflectionFileElement
				.get(MTZReflectionFileElement.MTZ_NUMBER_OF_BATCHES);
		if (noOfBatches != null) {

			writer.writeStartElement(reflection_dataURI,
					MTZReflectionFileElement.MTZ_NUMBER_OF_BATCHES.localName());
			writer.writeCharacters(mtzReflectionFileElement
					.get(MTZReflectionFileElement.MTZ_NUMBER_OF_BATCHES));
			writer.writeEndElement();

		}

		writer.writeStartElement(reflection_dataURI,
				MTZReflectionFileElement.MTZ_REFLECTION_TYPE.localName());
		writer.writeCharacters(mtzReflectionFileElement
				.get(MTZReflectionFileElement.MTZ_REFLECTION_TYPE));
		writer.writeEndElement();

		writer.writeStartElement(reflection_dataURI,
				MTZReflectionFileElement.MTZ_NUMBER_OF_REFLECTION.localName());
		writer.writeCharacters(mtzReflectionFileElement
				.get(MTZReflectionFileElement.MTZ_NUMBER_OF_REFLECTION));
		writer.writeEndElement();

		String freeR = mtzReflectionFileElement
				.get(MTZReflectionFileElement.MTZ_R_FREE_FLAG_PERCENTAGE);

		if (freeR != null) {
			writer.writeStartElement(reflection_dataURI,
					MTZReflectionFileElement.MTZ_R_FREE_FLAG_PERCENTAGE
							.localName());
			writer.writeCharacters(mtzReflectionFileElement
					.get(MTZReflectionFileElement.MTZ_R_FREE_FLAG_PERCENTAGE));
			writer.writeEndElement();
		}

		writer.writeStartElement(reflection_dataURI,
				MTZReflectionFileElement.MTZ_COLUMN_LABELS.localName());
		writer.writeCharacters(mtzReflectionFileElement
				.get(MTZReflectionFileElement.MTZ_COLUMN_LABELS));
		writer.writeEndElement();

		writer.writeStartElement(reflection_dataURI,
				MTZReflectionFileElement.MTZ_CELL_DIMENSION.localName());
		writer.writeCharacters(mtzReflectionFileElement
				.get(MTZReflectionFileElement.MTZ_CELL_DIMENSION));
		writer.writeEndElement();

		writer.writeStartElement(reflection_dataURI,
				MTZReflectionFileElement.MTZ_RESOLUTION_RANGE.localName());
		writer.writeCharacters(mtzReflectionFileElement
				.get(MTZReflectionFileElement.MTZ_RESOLUTION_RANGE));
		writer.writeEndElement();

		writer.writeStartElement(reflection_dataURI,
				MTZReflectionFileElement.MTZ_SPACE_GROUP.localName());
		writer.writeCharacters(mtzReflectionFileElement
				.get(MTZReflectionFileElement.MTZ_SPACE_GROUP));
		writer.writeEndElement();

		// close element </mtz_reflection>
		writer.writeEndElement();
		// flush and close
		writer.flush();
		writer.close();
		return out.toString();
	}

}
