/*
 * Created on 16 Aug 2007
 */
package uk.ac.kcl.cerch.soapi.sip.processor;

import java.io.File;
import java.util.Map;

import uk.ac.kcl.cerch.soapi.objectstore.ArchivalObject;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectArtifact;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectStore;
import uk.ac.kcl.cerch.soapi.objectstore.RDFStatement;
import uk.ac.kcl.cerch.soapi.objectstore.database.ArchivalObjectDao;
import uk.ac.kcl.cerch.soapi.sip.DirectorySIP;
import uk.ac.kcl.cerch.soapi.sip.SIP;
import uk.ac.kcl.cerch.soapi.temp.TempUtils;

@SuppressWarnings("unchecked")
public class SingleLayerDirectorySIPRDFBuilder implements SIPRDFBuilder {
    private ObjectStore objectStore;
    private Map archivalObjectPathMap;
    private DirectorySIP directorySIP;
    private ArchivalObjectDao archivalObjectDao;
    
    private String collectionId;
    
    public void processMultiLayer(SIP sip) {
        
    }

    public void buildRDF(SIP sip) throws SIPProcessorException {
        
        directorySIP = (DirectorySIP) sip;
        collectionId = "soapi:" + TempUtils.getRandomId();
        
        String collectionRootDirectory = new File(directorySIP.getDirectoryPath()).list()[0];
        ArchivalObject archivalObject = new ArchivalObject();
        archivalObject.setId(collectionId);
        archivalObject.setPath(collectionRootDirectory);
        archivalObjectDao.saveArchivalObject(archivalObject);
        
        traverseFiles(new File(directorySIP.getDirectoryPath()));
    }
    
    private void traverseFiles(File dir) throws SIPProcessorException {
        processFile(dir);
        if(dir.isDirectory()) {
            String[] children = dir.list();
            for(int i = 0; i < children.length; i++) {
                traverseFiles(new File(dir, children[i]));
            }
        }
    }
    
    private void processFile(File file) throws SIPProcessorException
    {
        try {
            if(file.isFile() && (! file.getName().toLowerCase().endsWith("md5"))) {
                String path = file.getPath().replaceAll("\\\\", "/").substring(directorySIP.getDirectoryPath().length() + 1);
                String archivalObjectId = ((ArchivalObject) archivalObjectPathMap.get(path)).getId();
                
                String rdf = "<rdf:Description rdf:about=\"info:fedora/" + archivalObjectId + "\">";
                rdf += "<fedora:isMemberOfCollection rdf:resource=\"info:fedora/" + collectionId + "\"/>";
                rdf += "</rdf:Description>";
                RDFStatement rdfMetadata = new RDFStatement();
                rdfMetadata.setRdfStatement(rdf);
                
                String objectArtifactId = objectStore.putObjectArtifact(rdfMetadata);

                ObjectArtifact objectArtifact = new ObjectArtifact();
                ArchivalObject archivalObject = archivalObjectDao.getArchivalObjectById(archivalObjectId);
                objectArtifact.setId(objectArtifactId);
                objectArtifact.setArchivalObject(archivalObject);
                objectArtifact.setType("RDFStatement");
                archivalObject.addObjectArtifact(objectArtifact);
                
                archivalObjectDao.saveArchivalObject(archivalObject);
            }
        }
        catch(Exception e) {
            throw new SIPProcessorException(e);
        }
    }

    public Map getSipObjectPathMap() {
        return archivalObjectPathMap;
    }

    public void setSipObjectPathMap(Map sipObjectPathMap) {
        this.archivalObjectPathMap = sipObjectPathMap;
    }
    
    public ObjectStore getObjectStore() {
        return objectStore;
    }

    public void setObjectStore(ObjectStore objectStore) {
        this.objectStore = objectStore;
    }
    
    public ArchivalObjectDao getArchivalObjectDao() {
        return archivalObjectDao;
    }
    
    public void setArchivalObjectDao(ArchivalObjectDao archivalObjectDao) {
        this.archivalObjectDao = archivalObjectDao;
    }
}
