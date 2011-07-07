/*
 * Created on 22 Aug 2007
 */
package uk.ac.kcl.cerch.soapi.sip;

/**
 * A <code>SIP</code> that is implemented using the file system directory structure.
 * 
 * @author Vijay N Albuquerque
 *
 */
@SuppressWarnings("serial")
public class DirectorySIP extends SIP {
    private String directoryPath;
    
    public DirectorySIP(String directoryPath) {
        this.directoryPath = directoryPath;
    }
    
    public String getDirectoryPath() {
        return directoryPath;
    }
    
    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }
}