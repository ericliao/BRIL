package uk.ac.kcl.cerch.bril.ccp4.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Runs the CCP4 binaries 
 * @author shrijar
 *
 */

public class CCP4CommandRunner {
	Vector<String> resultList = new Vector<String>();
	Map<String, String> diffMetadataMap = new HashMap<String, String>();
	Map<String, String> mtzMetadataMap = new HashMap<String, String>();
	String key;
	String value;
	boolean title = false;
	boolean colLabels = false;
	boolean lineHasToken = false;

	public CCP4CommandRunner() {
	}

	/**
	 * @param cmd CCP4 executable binary such as diffdump and mtzdmp
	 * @param tmpLocation location of the file passed to the binary
	 * @param opt any option to run the binary
	 * @return
	 * @throws java.io.IOException
	 * @throws java.lang.InterruptedException
	 */
	public int runCCP4Command(String cmd, String tmpLocation, String opt)
			throws java.io.IOException, java.lang.InterruptedException {
		byte iobuf[] = new byte[1024];
		int bytes;
		int returnCode = -1;

		String[] asCmd;
		if (opt != null) {
			System.out.println("mtz command");
			asCmd = new String[3];

			asCmd[0] = cmd;
			asCmd[1] = tmpLocation;
			asCmd[2] = opt;
		} else {
			System.out.println("diff command");
			asCmd = new String[2];
			asCmd[0] = cmd;
			asCmd[1] = tmpLocation;
		}
		if(tmpLocation == null && opt==null){
			System.out.println("none");
			asCmd = new String[1];
			asCmd[0] = cmd;
		}
		Process pr = null;

		Runtime r = Runtime.getRuntime();
		try {
			// Runtime.getRuntime().exec(exportPath);

			pr = r.exec(asCmd);
			InputStream errStream, inStream;
			BufferedReader input = new BufferedReader(new InputStreamReader(pr
					.getInputStream()));
			String line = null;
			String allLines = "";

			int count = 0;
			while ((line = input.readLine()) != null) {

				allLines = allLines.concat(line);
				if (count != 0) {
					resultList.add(line);
				}
				// System.out.println("line not null:"+allLines);
				count = count + 1;
			}

			//System.out.println("all data: "+allLines);
			inStream = pr.getInputStream();
			while ((bytes = inStream.read(iobuf)) > 0)
				System.out.write(iobuf, 0, bytes);

			errStream = pr.getErrorStream();
			while ((bytes = errStream.read(iobuf)) > 0)
				System.err.write(iobuf, 0, bytes);
			returnCode = pr.waitFor();
			errStream.close();
			inStream.close();
			pr.getOutputStream().close();
		} catch (Exception e) {
			System.out.println("Something went wrong. Continue anyway!," + e);
			// Make sure we continue even if exception.
			// logger.warn("Something went wrong. Continue anyway!",e);
		}
		//
		return returnCode;

	}

	/**
	 * @return list of all the output lines from the program/command run
	 */
	public Vector<String> getProgramResult() {
		return resultList;
	}

/*	*//**
	 * @return Map object containing the diffraction metadata
	 * @see void runDiffDump(,) run this to extract
	 *//*
	public Map<String, String> getDiffractionMetadata() {
		return diffMetadataMap;
	}

	public Map<String, String> getMTZMetadata() {
		return mtzMetadataMap;
	}

	*//**
	 * runs the diffdump binay to extract the diffraction 
	 * metadata and put it in the Map object
	 * key/value
	 * 
	 * @param fileLocation
	 *            tmp location of a diffraction image file
	 *//*

	public void runDiffDump(String fileLocation) {
		try {
			// CCP4CommandRunner runner = new CCP4CommandRunner();
			// runner.runCCP4Command("diffdump",fileLocation,"");
			// list = runner.getProgramResult();

			runCCP4Command("diffdump", fileLocation, "");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		processDiffDumpResults();

	}*/



/*	// process each line in the vector
	private void processDiffDumpResults() {
		for (int i = 0; i < resultList.size(); i++) {
			String newline = resultList.elementAt(i).toString();

			// System.out.println(newline);
			setLine(newline);
		}
	}*/


	// separate each line for key value
	/*private void setLine(String newLine) {

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
	}*/

	/**
	 * @param line
	 */
	private void readLine1(String line) {
		char star = '*';
		char colon = ':';
		char equal = '=';
		String comma = ",";
		if (hasToken(line, "*") == true) {

			for (int i = 0; i < line.length(); i++) {
				if (line.charAt(i) == star) {
					String newS = line.substring(i + 1);

					for (int j = 0; j < newS.length(); j++) {

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

						} else if (newS.charAt(j) == equal) {
							Scanner s = new Scanner(newS);
							String key = "";
							String value = "";
							s.useDelimiter("\\s*=\\s*");
							while (s.hasNext()) {
								// System.out.println("The key: " + s.next());
								key = s.next();

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
			System.out.println("Token * is= false: " + line);
			//these comma separated data are the key that are already inserted in the MTZMap.
			String datasetId = "Dataset ID, project/crystal/dataset names, cell dimensions, wavelength";
			char space = ' ';

			if (mtzMetadataMap.size() != 0) {

				if (isValueEmpty("Title") == true  && line.isEmpty()==false && hasKey("Base dataset")==false) {
					System.out.println("title is: " + line);
					mtzMetadataMap.put("Title", line);
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
				} else if (isValueEmpty("Number of Reflections") == true) {
					mtzMetadataMap.put("Number of Reflections", line.trim());
				} else if (isValueEmpty("Resolution Range") == true && line.isEmpty()==false) {
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
					System.out.println("DatasetID key has been added to the map: ");
					mtzMetadataMap.put(datasetId, "changeme");
				} else if (isValueEmpty("Dataset ID") == true
						&& isValueEmpty(datasetId) == false) {
					System.out.println("DatasetID value = null");
					String tmp = line.trim(); // 1 Unspecified
					System.out.println("DatasetID: " + tmp);
					//key 'Dataset ID' added in the Map with its value
					mtzMetadataMap.put("Dataset ID", tmp.substring(0, tmp
							.lastIndexOf(' ')));
					//key 'project name' added in the Map with its value
					mtzMetadataMap.put("project name", tmp.substring(tmp
							.lastIndexOf(' '), tmp.length()));
					
				} else if(hasKey("project name")==true && hasKey("crystal name")==false){
					mtzMetadataMap.put("crystal name", line.trim());
					//key 'dataset name' is NOT PRESENT in the Map
				} else if(hasKey("dataset name")==true && hasKey("dataset name")==false){
					mtzMetadataMap.put("dataset name", line.trim());
					//key 'cell dimensions' is PRESENT in the Map
				}else if (hasKey("dataset name")==true && isValueEmpty("cell dimensions")==true){
					mtzMetadataMap.put("cell dimensions", line.trim());
					//key 'wavelength' is PRESENT in the Map
				}else if(isValueEmpty("cell dimensions")==false && isValueEmpty("wavelength")==true){
					mtzMetadataMap.put("wavelength", line.trim());
				}
			
			}
		}
	}
	public boolean hasToken(String line, String token) {
		boolean lineHasToken = false;
		StringTokenizer st = new StringTokenizer(line);
		while (st.hasMoreTokens()) {
			// System.out.println();
			String tok = st.nextToken();
			if (tok.equals(token)) {
				lineHasToken = true;
				System.out.println("line has token:" + line);
			}
		}
		return lineHasToken;
	}

	public boolean isValueEmpty(String key) {
		boolean valueisNull = false;
		if (mtzMetadataMap.containsKey(key)) {
			//System.out.println("contains key: " + key + " =true");
			if (mtzMetadataMap.get(key) == null) {
				valueisNull = true;
				System.out.println(key+" key is null =true");
			}
		}
		return valueisNull;
	}

	public boolean hasKey(String key) {
		boolean keyPresent = false;
		if (mtzMetadataMap.containsKey(key)) {
			keyPresent = true;
		}
		return keyPresent;
	}

	public static void main(String arg[]) {
		// String tmpLocation = "/media/windows-share/h2-2_MS_3_360.img";
		String mtzfile = "/media/windows-share/5d5.mtz";
		CCP4CommandRunner run = new CCP4CommandRunner();

		// run.runDiffDump(tmpLocation);
		// run.runMTZdmpHeader(mtzfile);
		// run.readLine(" * Title:");
		// run.readLine(" From Truncate on the 16/ 6/08");
		run.readLine1(" * Title:");
		run.readLine1("  From Truncate on the 16/ 6/08");
		run.readLine1(" * Number of Datasets = 1");
		run.readLine1(" * Base dataset:");
		run
				.readLine1(" * Dataset ID, project/crystal/dataset names, cell dimensions, wavelength:");
		run.readLine1(" ");
		System.out.println("-------------------------------------");
		run.readLine1("        1 Unspecified");
		System.out.println("-------------------------------------");
		run.readLine1("           h2-2_MS_3");
		System.out.println("-------------------------------------");
		run.readLine1("           Unspecified");
		System.out.println("-------------------------------------");
		run
				.readLine1("              76.6975   79.1178   89.1120   90.0000  111.3815   90.0000");
		System.out.println("-------------------------------------");
		run.readLine1("             0.96980");
		System.out.println("-------------------------------------");
		run.readLine1(" * HISTORY for current MTZ file :");
		run.readLine1(" * Column Labels :");
		run.readLine1(" H K L FreeR_flag IMEAN SIGIMEAN F5d5 SIGF5d5");
		run.readLine1(" * Space group = 'C2' (number     5)");
		run
				.readLine1("* Cell Dimensions : (obsolete - refer to dataset cell dimensions above)");
		System.out.println("-------------------------------------");
		run
				.readLine1("   76.6975   79.1178   89.1120   90.0000  111.3815   90.0000 ");
		run.readLine1(" * Number of Reflections = 40081");
		//System.out.println(run.getMTZMetadata());

	}
}
