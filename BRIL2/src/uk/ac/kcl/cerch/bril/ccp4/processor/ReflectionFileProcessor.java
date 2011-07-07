package uk.ac.kcl.cerch.bril.ccp4.processor;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class ReflectionFileProcessor extends CCP4CommandRunner{

	
	/**
	 * Runs the mtzdmp -e binary to extract the header dump of 
	 * mtz file and put it in the Map object
	 * key/value
	 * @param fileLocation
	 */
	public void runMTZdmpHeader(String fileLocation) {
		try {
			runCCP4Command("mtzdmp", fileLocation, "-e");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		processMTZDmpResults();
	}
	
	/*	*//**
	 * @return Map object containing the diffraction metadata
	 * @see void runDiffDump(,) run this to extract
	 *//*
	public Map<String, String> getDiffractionMetadata() {
		return diffMetadataMap;
	}*/

	public Map<String, String> getMTZMetadata() {
		return mtzMetadataMap;
	}

	
	// process each line in the vector
	private void processMTZDmpResults() {
		for (int i = 0; i < resultList.size(); i++) {
			String newline = resultList.elementAt(i).toString();
			if (newline.isEmpty() == false) {
				readLine(newline);
			}
			System.out.println(newline);

		}
	}
	
	/**
	 * @param line
	 */
	private void readLine(String line) {
		char star = '*';
		char colon = ':';
		char equal = '=';
		String comma = ",";
		int titleCount =0;
		if (hasToken(line, "*") == true) {

			for (int i = 0; i < line.length(); i++) {
				if (line.charAt(i) == star) {
					String newS = line.substring(i + 1);

					for (int j = 0; j < newS.length(); j++) {
						//if the line has a colon or ':' characher
						if (newS.charAt(j) == colon) {

							String key = null;
							String value = null;
							Scanner s = new Scanner(newS);
							System.out.println("String:******: " + newS);
							s.useDelimiter("\\s*:\\s*");

							// while (s.hasNext()) {
							// System.out.println("The key: "+s.next());
							key = s.next().trim();
							// mtzMetadataMap.put(s.next(), value);
							// }

							// mtzMetadataMap.put(key, value);
							System.out.println("***" + hasToken(key, comma));
							// if(hasToken(key, comma)==true){
							Scanner s1 = new Scanner(key);
							s1.useDelimiter("\\s*,\\s*");
							while (s1.hasNext()) {
								// System.out.println("The comma
								// key:"+s1.next());
								mtzMetadataMap.put(s1.next(), value);
							}
							// }
							System.out.println("The key: " + key);
							System.out.println("The value: " + value);
							mtzMetadataMap.put(key, value);

						}//if the line has a equal or '=' characher 
						else if (newS.charAt(j) == equal) {
							Scanner s = new Scanner(newS);
							String key = "";
							String value = "";
							s.useDelimiter("\\s*=\\s*");
							while (s.hasNext()) {
								// System.out.println("The key: " + s.next());
								key = s.next().trim();

								Scanner s1 = new Scanner(s.next());
								while (s1.hasNext()) {
									value = value + s1.next();

								}
							}
							System.out.println("The key: " + key);
							System.out.println("The value: " + value);
							mtzMetadataMap.put(key, value);
						}
					}
				}
			}
		} else {
			/*
			 *System.out.println("Token * is= false: " + line);
			 *These colon separated data are the key that are already inserted in the MTZMap with null value.
			 *e.g line1 is processed that has colon char, it is added to the MTZmap, then the line after that comes to be processed
			 *that will reach here...
			 *The line that comes after this key insert is its value
			 *Thus by checking if the value for a particular key is null, the the next line is added as its value
			 *We assume that the header output from mtzdmp has this particular sequence of information- 
			 *based on the test on a set of mtz files.
			*/
			String datasetId = "Dataset ID, project/crystal/dataset names, cell dimensions, wavelength";
			char space = ' ';
			
			if (mtzMetadataMap.size() != 0) {

				if (isValueEmpty("Title") == true  && line.isEmpty()==false && hasKey("Base dataset")==false) {
		
					System.out.println("title is: " + line);
					if(line.contains(".")){
						System.out.println("Line after 'title' is empty.... \n adding chamgeme to the 'Title' value ");
						mtzMetadataMap.put("Title", "changeme");
					}
					//mtzMetadataMap.put("Title", line);
				} else if (isValueEmpty("HISTORY for current MTZ file") == true && line.isEmpty()==false) {
					mtzMetadataMap.put("HISTORY for current MTZ file", line.trim());
				}else if (isValueEmpty("Column Labels") == true && line.isEmpty()==false) {
					mtzMetadataMap.put("Column Labels", line.trim());
				} else if (isValueEmpty("Column Types") == true && line.isEmpty()==false) {
					mtzMetadataMap.put("Column Types", line.trim());
				}else if (isValueEmpty("Associated datasets") == true && line.isEmpty()==false) {
					mtzMetadataMap.put("Associated datasets", line.trim());
				}else if (isValueEmpty("Sort Order") == true && line.isEmpty()==false) {
					mtzMetadataMap.put("Sort Order", line.trim());
				}else if (isValueEmpty("Cell Dimensions") == true && line.isEmpty()==false) {
					mtzMetadataMap.put("Cell Dimensions", line.trim());
				} /*else if (isValueEmpty("Number of Reflections") == false) {
					mtzMetadataMap.put("Number of Reflections", line.trim());
				}*/ else if (isValueEmpty("Resolution Range") == true && line.isEmpty()==false) {
					mtzMetadataMap.put("Resolution Range", line.trim());
				}else if(hasToken(line,"FREERFLAG")==true){
					System.out.println("FreeFlag:!! "+line); //From FREERFLAG 16/ 6/2008 13:54:22 with fraction 0.050 
					if(hasToken(line,"fraction")==true){
						int spaceIndex = line.trim().lastIndexOf(' ');
						String tmp = line.trim().substring(spaceIndex);
						System.out.println("FreeFlag:!! "+tmp);
						mtzMetadataMap.put("R-Free", tmp);
					}
				}else if (isValueEmpty(datasetId) == true) {
					/*
					 * After the datasetIs key is put in the map with null value
					 * Next line is either a blank line or the first value
					 * for key Dataset ID
					 */
					System.out.println("Dataset ID, project/crystal/ key, value 'changeme' has been added to the map: ");
					mtzMetadataMap.put(datasetId, "changeme1");
					System.out.println("Current line: "+line);
					if(!line.equals(null)){
						String tmp = line.trim();
						System.out.println("Adding Dataset ID and project name ");
						mtzMetadataMap.put("Dataset ID", tmp.substring(0, tmp
								.lastIndexOf(' ')));
						//key 'project name' added in the Map with its value
						mtzMetadataMap.put("project name", tmp.substring(tmp
								.lastIndexOf(' '), tmp.length()));
					}
				}
				else if (hasKey(datasetId) ==true){
				
				/*else if (mtzMetadataMap.containsKey("Dataset ID")==false
						&& isValueEmpty(datasetId) == false) {*/
					//System.out.println("Dataset id project:- "+ mtzMetadataMap.get(datasetId));
					
					if(isValueEmpty(datasetId) == false && hasKey("Dataset ID")==false){
					System.out.println("Create Dataset ID key");
					String tmp = line.trim(); // 1 Unspecified
					System.out.println("DatasetID: " + tmp);
					//key 'Dataset ID' added in the Map with its value
					mtzMetadataMap.put("Dataset ID", tmp.substring(0, tmp
							.lastIndexOf(' ')));
					//key 'project name' added in the Map with its value
					mtzMetadataMap.put("project name", tmp.substring(tmp
							.lastIndexOf(' '), tmp.length()));
					}
					
				 else if(hasKey("project name")==true && hasKey("crystal name")==false){
					System.out.println("Adding crystal name ");
					mtzMetadataMap.put("crystal name", line.trim());
					//key 'dataset name' is NOT PRESENT in the Map
				} else if(hasKey("crystal name")==true && hasKey("dataset name")==false){
					System.out.println("Adding dataset name ");
					mtzMetadataMap.put("dataset name", line.trim());
					System.out.println("has key cell dimension: "+hasKey("cell dimensions"));
					System.out.println("has key wavelength: "+hasKey("wavelength"));
					System.out.println(mtzMetadataMap.get("cell dimensions"));
					System.out.println(mtzMetadataMap.get("wavelength"));
					//key 'cell dimensions' is already PRESENT in the Map
				}else if (hasKey("dataset name")==true && isValueEmpty("cell dimensions")==true){
					System.out.println("Adding cell dimensions: "+line.trim());
					mtzMetadataMap.put("cell dimensions", line.trim());
					//key 'wavelength' is already PRESENT in the Map
				}else if(isValueEmpty("cell dimensions")==false && isValueEmpty("wavelength")==true){
					System.out.println("Adding wavelength: "+line.trim());
					mtzMetadataMap.put("wavelength", line.trim());
				}
				}
			}
		}
	}
}
