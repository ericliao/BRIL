package uk.ac.kcl.cerch.bril.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Properties;
import java.util.StringTokenizer;

//import org.apache.commons.codec.binary.Base64;

//import uk.ac.kcl.cerch.bril.MessageFilter;
//import uk.ac.kcl.cerch.bril.common.util.DateTime;

/**
 * @author shrijar
 *
 */

public class FileUtil {

	static boolean isDirectory = false;
	static boolean isFile = false;
	static String localFileName;
	static String localFileNameNoExtention;
	static String fileNameExtention;
	static String rootDirName;
	static String childOfRootDirName;
	static String stringAfterRootDir;
	static String childOfChildDirName;
	static boolean hasToken;

	/** Fast & simple file copy. */
	public static void copy(File source, File dest) throws IOException {
		FileChannel in = null, out = null;
		try {
			in = new FileInputStream(source).getChannel();
			out = new FileOutputStream(dest).getChannel();

			long size = in.size();
			MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0,
					size);

			out.write(buf);

		} finally {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		}
	}

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	/*
	 * public static byte[] getBytesFromFile(File file) throws IOException{ //
	 * Get the local file system as a File object and return the binary data
	 * byte[] binaryData = new byte[(int) file.length()]; byte[] encodedDataIn =
	 * Base64.encodeBase64(binaryData); return encodedDataIn; }
	 */

	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		if (length > Integer.MAX_VALUE) {
			// File is too large
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}

	/**
	 * @param entryId
	 *            is the path for new/modify/delete directory or file
	 * @return boolean value
	 */
	public static boolean isDirectory(String entryId) {

		setEntryId(entryId);
		return isDirectory;

	}

	/**
	 * @param entryId
	 *            is the path for new/modify/delete directory or file
	 * @return boolean value
	 */
	public static boolean isFile(String entryId) {
		setEntryId(entryId);
		return isFile;
	}

	/**
	 * @param entryId
	 *            is the path for new/modify/delete directory or file
	 */

	private static void setEntryId(String entryId) {
		// if path =ientryId does not have file extention in the id then its a
		// directory
		int dotIndex = entryId.lastIndexOf('.');
		if (dotIndex == -1) {
			isDirectory = true;
		} else {
			isFile = true;
		}
	}

	public static String getRootDirectoryName(String entryId) {
		int firstslash = 0;
		int secondslash = 0;
		int lastSlash = 0;
		int count = 0;
		StringBuffer sb = new StringBuffer(entryId);
		for (int k = 0; k < sb.length(); k++) {
			char s = sb.charAt(k);
			if (s == '/' || s == '\\') {

				// System.out.println(s);
				count = count + 1;
				;

				if (count == 1) {
					firstslash = k;
				}
				if (count == 2) {
					secondslash = k;
				}
				lastSlash = k;
			}
			// localFileName=count;
		}
		// System.out.println(firstslash);
		System.out.println("index of last slash: " + lastSlash);
		System.out.println("Filename:"
				+ entryId.substring(lastSlash + 1, entryId.length()));
		rootDirName = entryId.substring(firstslash + 1, secondslash);
		return rootDirName;
	}

	public static boolean getChildOfRootDirectory(String entryId) {
		boolean childOfRoot = false;

		String stringAfterRootDir = getStringAfterRootDir(entryId);
		// String childOfChildDir="";
		int slash1 = stringAfterRootDir.lastIndexOf("/");
		int slash2 = stringAfterRootDir.lastIndexOf("//");
		int dot = stringAfterRootDir.lastIndexOf('.');

		if (stringAfterRootDir != null) {
			if (slash1 != -1) {
				childOfRootDirName = stringAfterRootDir.substring(0, slash1);
				if (childOfRootDirName.lastIndexOf("/") != -1) {
					System.out.println(childOfRootDirName);
					String str = childOfRootDirName;
					childOfRootDirName = childOfRootDirName.substring(0,
							childOfRootDirName.lastIndexOf("/"));
					System.out.println(childOfRootDirName);
					childOfChildDirName = str.substring(
							str.lastIndexOf("/") + 1, str.length());

				}
				childOfRoot = true;

			} else if (slash2 != -1) {
				childOfRootDirName = stringAfterRootDir.substring(0, slash2);
				if (childOfRootDirName.lastIndexOf("//") != -1) {
					childOfRootDirName = childOfRootDirName.substring(0,
							childOfRootDirName.lastIndexOf("//"));
				}
				childOfRoot = true;
			}

		}

		return childOfRoot;
	}

	public static String getChildDirectoryName() {
		return childOfRootDirName;
	}

	public static String getChildOfChildDirectoryName() {
		return childOfChildDirName;
	}

	public static boolean hasChildOfChildDirectoryName() {
		boolean presentCofC = false;

		return presentCofC;
	}

	private static String getStringAfterRootDir(String entryId) {
		String stringAfterRootDir;
		int secondslash = 0;
		int count = 0;

		StringBuffer sb = new StringBuffer(entryId);
		for (int k = 0; k < sb.length(); k++) {
			char s = sb.charAt(k);
			if (s == '/' || s == '\\') {

				// System.out.println(s);
				count = count + 1;
				;

				if (count == 2) {
					secondslash = k;
				}
			}
		}
		stringAfterRootDir = entryId.substring(secondslash + 1);
		return stringAfterRootDir;

	}

	/**
	 * @param fPath
	 * @return filename
	 */
	public static String getFileName(String fPath) {
		// if(isFile(fPath)==true){
		setFileName(fPath);
		// }
		return localFileName;
	}

	/**
	 * @return extension of the entered file
	 */
	public static String getFileNameExtension() {
		return fileNameExtention;
	}

	/**
	 * @param fPath
	 */
	public static void setFileName(String fPath) {

		int lastSlashIndex = fPath.lastIndexOf('/');
		int lastSlashIndexAlt = fPath.lastIndexOf('\\');
		int lastDocIndex = fPath.lastIndexOf('.');

		if (lastSlashIndex >= 0 && lastSlashIndex < fPath.length() - 1) {
			localFileName = fPath.substring(lastSlashIndex + 1);
			localFileNameNoExtention = fPath.substring(lastSlashIndex + 1,
					lastDocIndex);
			fileNameExtention = fPath.substring(lastDocIndex + 1);
			// filePath = fPath.substring(start, lastSlashIndex);
		} else if (lastSlashIndexAlt >= 0
				&& lastSlashIndexAlt < fPath.length() - 1) {
			localFileName = fPath.substring(lastSlashIndexAlt + 1);
			localFileNameNoExtention = fPath.substring(lastSlashIndexAlt + 1,
					lastDocIndex);
			// System.out.println(localFileName);
			fileNameExtention = fPath.substring(lastDocIndex + 1);
			// filePath = fPath.substring('0', lastSlashIndex);

		} else if (lastDocIndex >= 0 && lastDocIndex < fPath.length() - 1) {

			localFileNameNoExtention = localFileName
					.substring(lastDocIndex - 1);
			fileNameExtention = localFileName.substring(lastDocIndex + 1);
		}

	}

	public static String getFileName1(String fPath) {
		StringBuffer sb = new StringBuffer(fPath);
		int lastDocIndex = 0;
		int count = 0;
		int lastSlashIndex1 = 0;
		for (int k = 0; k < sb.length(); k++) {
			char s = sb.charAt(k);
			if (s == '/' || s == '\\') {
				count = count + 1;
				lastSlashIndex1 = k;
			}
			if (s == '.') {
				lastDocIndex = k;
			}
		}

		// System.out.println("index of dot: "+ lastDocIndex +"  length:"+
		// fPath.length());
		// System.out.println("Filename:"+fPath.subSequence(lastSlashIndex1+1,
		// fPath.length()));

		localFileName = fPath.substring(lastSlashIndex1 + 1, fPath.length());
		fileNameExtention = fPath.substring(lastDocIndex + 1);
		localFileNameNoExtention = fPath.substring(lastSlashIndex1 + 1,
				lastDocIndex);
		// System.out.println("filename: "+localFileNameNoExtention);
		return localFileName;
	}

	public static void writeToFile(String filePath, String data) {
		File newf = new File(filePath);
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(newf));
			out.write(data);
			out.close();
		} catch (IOException e) {
			System.out.println("Exception: file writer");

		}
	}

	public static void writeByteArrayToFile(String strFilePath, byte[] data) {
		try {
			FileOutputStream fos = new FileOutputStream(strFilePath);
			fos.write(data);
			fos.close();

		} catch (FileNotFoundException ex)

		{
			System.out.println("FileNotFoundException : " + ex);

		}

		catch (IOException ioe)

		{

			System.out.println("IOException : " + ioe);
		}
	}

	public static String writeFileToString(String strFilePath) {
		String data = null;
		FileInputStream mets;
		try {
			mets = new FileInputStream(strFilePath);

			InputStreamReader isr;

			isr = new InputStreamReader(mets, "UTF8");

			StringBuffer buffer = new StringBuffer();
			Reader in = new BufferedReader(isr);
			int ch;

			while ((ch = in.read()) > -1) {
				buffer.append((char) ch);
			}

			in.close();
			data = buffer.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;

	}
	public static byte[] writeStringToByteArray(String datacontent) {
	byte[] arry = new byte[1024];
	
	return arry;
	}

	private boolean hasToken(String line, String token) {
		StringTokenizer st = new StringTokenizer(line);
		while (st.hasMoreTokens()) {
			// System.out.println();
			String tok = st.nextToken();
			if (tok.equals(token)) {
				hasToken = true;
				// System.out.println("title line:" +line);
			}
		}
		return hasToken;
	}

	public static String createDirectory(String directoryName) {
		String directoryPathName = null;

		// check if its multiple dirs
		if (directoryName.lastIndexOf('/') != -1 || directoryName.lastIndexOf('\\') != -1) {
			boolean success = (new File(directoryName)).mkdirs();

			if (success) {
				System.out.println("Directories: " + directoryName + "created.");
			}
		} else {
			boolean success = (new File(directoryName)).mkdir();

			if (success) {
				System.out.println("Directory: " + directoryName + " "+ "created.");
			}
		}
		return directoryPathName;
	}

	public static void unzipFileType(String fTmpPath) {

	}

	public static void main(String[] args) {
		// System.out.println(FileUtil.isFile("c:/Experiment/testfolder/test.txt"));
		// System.out.println(FileUtil.isDirectory("c:/Experiment/testfolder/test.txt"));
		// System.out.println(DateTime.getDateTime("17 Jun 2008 09:29:03"));
		// System.out.println(FileUtil.getRootDirectoryName("u:/Experiment/tes/test.txt"));
		// System.out.println(FileUtil.getChildOfRootDirectory("c:/Experiment/testfolder/test.txt"));
		// System.out.println(FileUtil.getChildDirectoryName());
		// System.out.println(FileUtil.getChildOfChildDirectoryName());
		// System.out.println(FileUtil.getFileName("c:/Experiment/mysql - Copy (4).txt"));
		FileUtil.setFileName("c:/Experiment/DC.txt");
		System.out.println(FileUtil.getFileNameExtension());
		
	
	}

}
