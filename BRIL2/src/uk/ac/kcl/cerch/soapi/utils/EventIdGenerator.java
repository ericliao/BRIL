package uk.ac.kcl.cerch.soapi.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import uk.ac.kcl.cerch.soapi.objectstore.ObjectArtifactIdGenerator;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectArtifactIdGeneratorException;

public class EventIdGenerator implements ObjectArtifactIdGenerator {
    private String workDirectory = null;
    
    public EventIdGenerator() throws ObjectArtifactIdGeneratorException 
    {
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
    
    public String generateId() throws ObjectArtifactIdGeneratorException {
        String objectArtifactId = null;
        
        try {
            File file = new File(workDirectory + File.separator + "eventAidgen.txt");

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
