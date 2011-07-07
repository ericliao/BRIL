/*
 * Created on 14 Aug 2007
 */
package uk.ac.kcl.cerch.soapi.sip;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * Implementation of <code>SIPIdGenerator</code> that uses a file as its store.
 * The implementation generates an incremental sequence of numbers for the identifiers.
 * 
 * @author Vijay N Albuquerque
 *
 */
public class FileSIPIdGenerator implements SIPIdGenerator {
    private String workDirectory = null;
    
    public FileSIPIdGenerator()
    throws SIPIdGeneratorException {
        Properties properties = new Properties();
        
        try {
            properties.load(this.getClass().getResourceAsStream("soapi.properties"));
            workDirectory = properties.getProperty("workDirectory");
            
            if(workDirectory == null) {
                throw new SIPIdGeneratorException("Could not load workDirectory property from the properties file.");
            }
        }
        catch(IOException e) {
            throw new SIPIdGeneratorException(e);
        }
    }
    
    public String generateId()
    throws SIPIdGeneratorException {
        String sipId = null;
        
        try {
            File file = new File(workDirectory + File.separator + "sipidgen.txt");

            if(file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                reader.close();
                
                sipId = String.valueOf(Integer.parseInt(line) + 1);
            }
            else {
                sipId = "1";
            }
            
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            writer.write(sipId);
            writer.close();
        }
        catch(FileNotFoundException e) {
            throw new SIPIdGeneratorException(e);
        }
        catch(IOException e) {
            throw new SIPIdGeneratorException(e);
        }
        
        return sipId;
    }
    
    public String generateId(String prefix)
    throws SIPIdGeneratorException {
        String sipId = null;
        
        try {
            File file = new File(workDirectory + File.separator + "sipidgen-" + prefix + ".txt");
            
            if(file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                reader.close();
                
                sipId = String.valueOf(Integer.parseInt(line) + 1);
            }
            else {
                sipId = "1";
            }
            
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            writer.write(sipId);
            writer.close();
        }
        catch(FileNotFoundException e) {
            throw new SIPIdGeneratorException(e);
        }
        catch(IOException e) {
            throw new SIPIdGeneratorException(e);
        }
        
        sipId = prefix + ":" + sipId;
        
        return sipId;
    }
}
