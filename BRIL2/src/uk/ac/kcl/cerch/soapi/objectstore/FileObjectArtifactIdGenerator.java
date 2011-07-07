/*
 * Created on 14 Aug 2007
 */
package uk.ac.kcl.cerch.soapi.objectstore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * Implementation of <code>ObjectArtifactIdGenerator</code> that uses a file as its store.
 * The implementation generates an incremental sequence of numbers for the identifiers.
 * 
 * @author Vijay N Albuquerque
 *
 */
public class FileObjectArtifactIdGenerator implements ObjectArtifactIdGenerator {
    private String workDirectory = null;
    
    public FileObjectArtifactIdGenerator()
    throws ObjectArtifactIdGeneratorException {
        Properties properties = new Properties();
        
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("soapi.properties"));
            workDirectory = properties.getProperty("workDirectory");
            
            if(workDirectory == null) {
                throw new ObjectArtifactIdGeneratorException("Could not load workDirectory property from the properties file.");
            }
        }
        catch(IOException e) {
            throw new ObjectArtifactIdGeneratorException(e);
        }
    }
    
    public String generateId()
    throws ObjectArtifactIdGeneratorException {
        String objectArtifactId = null;
        
        try {
            File file = new File(workDirectory + File.separator + "aidgen.txt");

            if(file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                reader.close();
                
                objectArtifactId = String.valueOf(Integer.parseInt(line) + 1);
            }
            else {
                objectArtifactId = "1";
            }
            
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            writer.write(objectArtifactId);
            writer.close();
        }
        catch(FileNotFoundException e) {
            throw new ObjectArtifactIdGeneratorException(e);
        }
        catch(IOException e) {
            throw new ObjectArtifactIdGeneratorException(e);
        }
        
        return objectArtifactId;
    }
}
