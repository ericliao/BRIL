/*
 * Created on 16 Aug 2007
 */
package uk.ac.kcl.cerch.soapi.sip.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import uk.ac.kcl.cerch.soapi.objectstore.ArchivalObject;
import uk.ac.kcl.cerch.soapi.objectstore.Checksum;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectArtifact;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectStore;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectStoreException;
import uk.ac.kcl.cerch.soapi.objectstore.database.ArchivalObjectDao;
import uk.ac.kcl.cerch.soapi.objectstore.database.SIPDao;
import uk.ac.kcl.cerch.soapi.sip.DirectorySIP;
import uk.ac.kcl.cerch.soapi.sip.SIP;
import uk.ac.kcl.cerch.soapi.temp.TempUtils;

public class SingleLayerDirectorySIPProcessor implements SIPProcessor {
    private SIPRDFBuilder sipRDFBuilder;
    private ArchivalObjectDao archivalObjectDao;
    private DirectorySIP directorySIP;
    private ObjectStore objectStore;
    private Map<String,ArchivalObject> archivalObjectPathMap;
    private SIPDao sipDao; 

    public SingleLayerDirectorySIPProcessor() {
        archivalObjectPathMap = new HashMap<String,ArchivalObject>();
    }

    public void processSIP(SIP sip)
    throws SIPProcessorException {
        this.directorySIP = (DirectorySIP) sip;
        File directory = new File(directorySIP.getDirectoryPath());

        try {
            traverseFiles(directory);
            traverseChecksums(directory);
        }
        catch(IOException e) {
            throw new SIPProcessorException("An IOException occured in the SIP Processor.", e);
        }
        catch(ObjectStoreException e) {
            throw new SIPProcessorException("An ObjectStoreException occured in the SIP Processor.", e);
        }

        SingleLayerDirectorySIPRDFBuilder directorySIPRDFBuilder = (SingleLayerDirectorySIPRDFBuilder) sipRDFBuilder;
        directorySIPRDFBuilder.setSipObjectPathMap(archivalObjectPathMap);
        directorySIPRDFBuilder.buildRDF(sip);
    }

    private void traverseFiles(File dir) {
        processFile(dir);

        if(dir.isDirectory()) {
            String[] children = dir.list();
            for(int i = 0; i < children.length; i++) {
                traverseFiles(new File(dir, children[i]));
            }
        }
    }

    private void processFile(File file)
    {
        if(file.isFile() && (! file.getName().toLowerCase().endsWith("md5"))) {
            ArchivalObject archivalObject = new ArchivalObject();
            archivalObject.setFilename(file.getName());
            archivalObject.setId("soapi:" + String.valueOf(TempUtils.getRandomId()));
            archivalObject.setPath(file.getPath().replaceAll(File.pathSeparator, "/").substring(directorySIP.getDirectoryPath().length() + 1));

            archivalObjectPathMap.put(archivalObject.getPath(), archivalObject);
        }
    }

    private void traverseChecksums(File dir)
    throws IOException, ObjectStoreException {
        processChecksum(dir);
        if(dir.isDirectory()) {
            String[] children = dir.list();
            for(int i = 0; i < children.length; i++) {
                traverseChecksums(new File(dir, children[i]));
            }
        }
    }

    private void processChecksum(File file)
    throws IOException, ObjectStoreException {
        if(file.isFile() && (file.getName().toLowerCase().endsWith("md5"))) {
            String md5ChecksumPath = file.getPath().replaceAll(File.pathSeparator, "/").substring(directorySIP.getDirectoryPath().length() + 1);

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String md5 = bufferedReader.readLine();
            bufferedReader.close();

            Checksum md5Checksum = new Checksum();
            md5Checksum.setChecksum(md5);

            String objectArtifactId = objectStore.putObjectArtifact(md5Checksum);

            ArchivalObject archivalObject = (ArchivalObject) archivalObjectPathMap.get(md5ChecksumPath.substring(0, md5ChecksumPath.length() - 4));
            archivalObject.setSip(directorySIP);
            ObjectArtifact objectArtifact = new ObjectArtifact();
            objectArtifact.setId(objectArtifactId);
            objectArtifact.setArchivalObject(archivalObject);
            objectArtifact.setType("Checksum");
            archivalObject.addObjectArtifact(objectArtifact);

            archivalObjectDao.saveArchivalObject(archivalObject);
        }
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

    public SIPRDFBuilder getSipRDFBuilder() {
        return sipRDFBuilder;
    }

    public void setSipRDFBuilder(SIPRDFBuilder sipRDFBuilder) {
        this.sipRDFBuilder = sipRDFBuilder;
    }

    public SIPDao getSipDao() {
        return sipDao;
    }

    public void setSipDao(SIPDao sipDao) {
        this.sipDao = sipDao;
    }
}

