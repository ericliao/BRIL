package uk.ac.kcl.cerch.soapi.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import uk.ac.kcl.cerch.soapi.SOAPIException;

/* NOTE: This code was taken from koders.com
 * http://www.koders.com/java/fid95A0C758979D3684DBF8D770A12F4F2BDBBA7B25.aspx?s=cdef%3Atree
 * and modified by Andreas Mavrides
 */
/**
 *  Create unique filenames and/or files.  User can supply a name base and/or
 *  a prefix, and also a directory.  The generated filename will be 
 *            dir[\]base_[randomNumbers]suffix
 *  If dir is not specified, it will be c:\temp if that is a valid directory, 
 *  otherwise it'll be an empty string, causing java to use the current directory.
 *  So a possible filename, given dir="c:\temp", base="blah", and suffix=".xxx"
 *  would be something like "c:\temp\blah_345953829.xxx".  
 * 
 *    If the given dir name doesn't end with the local platform's file separator 
 *  character, then the separator char will be appended.  Thus after a call 
 *  fileGen.setDirectory("c:"), fileGen.getDirectory() will return "c:\".
 */
public class TempFileGenerator {

    private static Random random = new Random((new Date()).getTime());
    private String nameBase;
    private String nameSuffix;
    private String dir = "";
    private Properties properties;
    
    /**
     *  Create a new TempFileGenerator having base nameBase and suffix nameSuffix.
     */
    public TempFileGenerator(String nameBase, String nameSuffix) throws SOAPIException
    {
        try{
            properties = new Properties(); 
            properties.load(getClass().getClassLoader().getResourceAsStream("soapi.properties"));
            dir =  properties.getProperty("tempDirectory");           
        }
        catch(Exception e)
        {
            throw new SOAPIException(e);
        }
        
        if (!(new File(dir)).isDirectory()) 
            dir = "";
        if (dir == null)
            dir = "";
        init(nameBase, nameSuffix);
    }
    /**
     *  Create a new TempFileGenerator having base nameBase, suffix nameSuffix, and
     *  directory dir.
     */
    public TempFileGenerator(String dir, String nameBase, String nameSuffix)
    {
        init(nameBase, nameSuffix);
    }
    /**
     *  Create a unique filename, create a file, and return the File object.
     */
    public File generateFile()
    {
        return generateFile(null);
    }
    /**
     *  Create a filename, create a file, and return the File object.
     *  Note that explicitly passing midName bypasses the random-name-generation
     *  mechanism, so the returned filename is fuly predictable 
     * (dir+base+mid+suffix).
     */
    public File generateFile(String midName)
    {
        File file;
        String name = (midName==null) ? generateFileName() 
                : generateFileName(midName);
        if ((file = new File(name)) == null)
            return null;
        
        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            fos.close();
        }
        catch (IOException e) { return null; }
        return file;
    }
    /**
     *  Return a filename that's guaranteed not to exist.
     */
    public String generateFileName()
    {
        for (;;)
        {
            String name = generateFileName("_" + Math.abs(random.nextInt()));
            if (!(new File(name)).exists())
                return name;
        }
    }
    /**
     * Generate a non-random filename (dir+base+mid+suffix)
     */
    public String generateFileName(String midName)
    {
        return dir + nameBase + midName + nameSuffix;
    }
    /**
     */
    public String getDirectory()  { return dir; }
    /**
     */
    public String getNameBase()   { return nameBase; }
    /**
     */
    public String getNameSuffix() { return nameSuffix; }
    /**
     */
    private void init(String nameBase, String nameSuffix)
    {
        setDirectory(dir);
        setNameBase(nameBase==null ? "" : nameBase);
        setNameSuffix(nameSuffix==null ? "" : nameSuffix);
    }
    /**
     */
    public void setDirectory(String dir)
    {
        if (dir.length() > 0 && dir.charAt(dir.length()-1) != File.separatorChar)
            this.dir = dir + File.separatorChar;
    }
    /**
     */
    public void setNameBase(String nameBase)   { this.nameBase = nameBase; }
    /**
     */
    public void setNameSuffix(String nameSuff) { this.nameSuffix = nameSuff; }
}
