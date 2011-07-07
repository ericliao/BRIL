package uk.ac.kcl.cerch.bril.ccp4.processor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DiffractionImageProcessor extends CCP4CommandRunner{
	Map<String, String> diffMetadataMap = new HashMap<String, String>();
	/**
	 * runs the diffdump binay to extract the diffraction 
	 * metadata and put it in the Map object
	 * key/value
	 * 
	 * @param fileLocation
	 *            tmp location of a diffraction image file
	 */

	public void runDiffDump(String fileLocation) {
		try {
			// CCP4CommandRunner runner = new CCP4CommandRunner();
			// runner.runCCP4Command("diffdump",fileLocation,"");
			// list = runner.getProgramResult();

			runCCP4Command("diffdump", fileLocation, null);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		processDiffDumpResults();

	}
	
	/**
	 * @return Map object containing the diffraction metadata
	 * @see void runDiffDump(,) run this to extract
	 */
	public Map<String, String> getDiffractionMetadataAsMap() {
		return diffMetadataMap;
	}
	
	// process each line in the vector
	private void processDiffDumpResults() {
		for (int i = 0; i < resultList.size(); i++) {
			String newline = resultList.elementAt(i).toString();

			// System.out.println(newline);
			setLine(newline);
		}
	}
	
	// separate each line for key value
	private void setLine(String newLine) {

		int start = 0;
		int end = 0;
		int count = 0;

		try {
			for (int i = 0; i < newLine.length(); i++) {
				if (newLine.charAt(i) == ':') {
					if (count == 0) {
						end = i;
						key = newLine.substring(start, end);

						value = newLine.substring(end + 1);

						diffMetadataMap.put(key, value);
						System.out.println(key);
						System.out.println(value);
					}
					count = count + 1;
				}
			}
		} catch (Exception e) {
			e.fillInStackTrace();
		}
	}

	

}
