package uk.ac.kcl.cerch.soapi.utils;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import uk.ac.kcl.cerch.soapi.SOAPIException;
import uk.ac.kcl.cerch.soapi.objectstore.ArchivalObject;
import uk.ac.kcl.cerch.soapi.objectstore.Checksum;
import uk.ac.kcl.cerch.soapi.objectstore.DisseminationManifestation;
import uk.ac.kcl.cerch.soapi.objectstore.FileCharacterisation;
import uk.ac.kcl.cerch.soapi.objectstore.FileFormat;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectArtifact;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectStore;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectStoreException;
import uk.ac.kcl.cerch.soapi.objectstore.OriginalContent;
import uk.ac.kcl.cerch.soapi.objectstore.PreservationManifestation;
import uk.ac.kcl.cerch.soapi.objectstore.database.ArchivalObjectDao;
import uk.ac.kcl.cerch.soapi.objectstore.database.ObjectArtifactDao;

public class ObjectStoreUtilities {

    //private MysqlConnectionPoolDataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private ArchivalObjectDao archivalObjectDao;
    private ObjectStore objectStore;
    
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

    public FileCharacterisation getRelatedFileCharacterisation(String objectArtifactId) throws ObjectStoreException
    {
        FileCharacterisation fileCharacterisation = null;
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("soapi.xml");
        ObjectStore objectStore = (ObjectStore) applicationContext.getBean("objectStore");
        ObjectArtifactDao objectArtifactDao = 
            (ObjectArtifactDao) applicationContext.getBean("objectArtifactDao");
        
        // GET FROM DAO and proceed
        try{
            ObjectArtifact objectArtifact = objectArtifactDao.getObjectArtifactById(objectArtifactId);
            ArchivalObject archivalObject = objectArtifact.getArchivalObject();
            Set<ObjectArtifact> objectArtifacts = archivalObject.getObjectArtifacts();
            for(ObjectArtifact tmpObjectArtifact: objectArtifacts)
            {
                if(tmpObjectArtifact.getType().equals("FileCharacterisation"))
                {
                    FileCharacterisation fcc =(FileCharacterisation) objectStore.getObjectArtifact(tmpObjectArtifact.getId());
                    if(fcc.getRelatedObjectArtifactId().equals(objectArtifactId))
                        fileCharacterisation = fcc;
                }
            }
        }
        catch(ObjectStoreException ose)
        {
            throw new ObjectStoreException(ose);
        }
        return fileCharacterisation;
    }
    
    // Only applicable to OriginalContent - only that has a stored FileFormat
    // TODO REVIEW IF NEEDED
    public FileFormat getRelatedFileFormat(String objectArtifactId) throws ObjectStoreException, SOAPIException
    {
        FileFormat fileFormat = null;
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("soapi.xml");
        ObjectStore objectStore = (ObjectStore) applicationContext.getBean("objectStore");
        ObjectArtifactDao objectArtifactDao = 
            (ObjectArtifactDao) applicationContext.getBean("objectArtifactDao");

        try{
            ObjectArtifact objectArtifact = objectArtifactDao.getObjectArtifactById(objectArtifactId);
            if(!objectArtifact.getType().equals("OriginalContent"))
                throw new SOAPIException("Operation getRelatedFileFormat is only applicable " +
                		"for ObjectArtifacts of type OriginalContent and not " + objectArtifact.getType());
            ArchivalObject archivalObject = objectArtifact.getArchivalObject();
            Set<ObjectArtifact> objectArtifacts = archivalObject.getObjectArtifacts();
            for(ObjectArtifact tmpObjectArtifact: objectArtifacts)
            {
                if(tmpObjectArtifact.getType().equals("FileFormat"))
                {
                    FileFormat fcc =(FileFormat) objectStore.getObjectArtifact(tmpObjectArtifact.getId());
                    System.out.println("fcc.getRelatedObjectArtifactId()"+fcc.getRelatedObjectArtifactId());
                    if(fcc.getRelatedObjectArtifactId().equals(objectArtifactId))
                    {
                        fileFormat = fcc;
                    }
                }
            }   
        }
        catch(ObjectStoreException ose)
        {
           throw new ObjectStoreException(ose);
        }
        return fileFormat;
    }
    
    public Checksum getRelatedChecksum(String objectArtifactId) throws ObjectStoreException, SOAPIException
    {
        Checksum checksum = null;
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("soapi.xml");
        ObjectStore objectStore = (ObjectStore) applicationContext.getBean("objectStore");
        ObjectArtifactDao objectArtifactDao = 
            (ObjectArtifactDao) applicationContext.getBean("objectArtifactDao");
        try{
            ObjectArtifact objectArtifact = objectArtifactDao.getObjectArtifactById(objectArtifactId);
            if(!objectArtifact.getType().equals("OriginalContent"))
                throw new SOAPIException("Operation getRelatedFileFormat is only applicable " +
                        "for ObjectArtifacts of type OriginalContent and not " + objectArtifact.getType());
            ArchivalObject archivalObject = objectArtifact.getArchivalObject();
            Set<ObjectArtifact> objectArtifacts = archivalObject.getObjectArtifactsByType("Checksum");
            for(ObjectArtifact tmpObjectArtifact: objectArtifacts)
            {
                if(tmpObjectArtifact.getType().equals("Checksum"))
                {
                    Checksum cs =(Checksum) objectStore.getObjectArtifact(tmpObjectArtifact.getId());
                    if(cs.getRelatedObjectArtifactId().equals(objectArtifactId))
                        checksum = cs;
                }
            }   
        }
        catch(ObjectStoreException ose)
        {
           throw new ObjectStoreException(ose);
        }
        return checksum;
    }
    
    public Set<ObjectArtifact> getObjectArtifactsByType(String type)
    {
        Set<ObjectArtifact> fullObjectArtifacts = null;
        Set<ObjectArtifact> objectArtifacts = null;
        
        ObjectArtifact objectArtifact = null;
        
        String sql = "SELECT * FROM objectartifacts o";
        SqlRowSet sqlRowSet = getJdbcTemplate().queryForRowSet(sql);


        if(sqlRowSet != null )
        {
            objectArtifacts = new HashSet<ObjectArtifact>(); 
            while( !sqlRowSet.isLast())
            {
                sqlRowSet.next();
                if(sqlRowSet.getString("type").equals(type))
                {
                    objectArtifact = new ObjectArtifact();
                    objectArtifact.setId(sqlRowSet.getString("id"));
                    objectArtifact.setType(sqlRowSet.getString("type"));
                    ArchivalObject archivalObject = 
                        archivalObjectDao.getArchivalObjectById(sqlRowSet.getString("archivalObjectId"));
                    objectArtifact.setArchivalObject(archivalObject);
                    objectArtifacts.add(objectArtifact);
                }
            }
        }
        
        try{
            if( objectArtifacts != null)
            {
                fullObjectArtifacts = new HashSet<ObjectArtifact>(); 
                for(ObjectArtifact oa: objectArtifacts)
                {
                    ObjectArtifact objectArtifactToAdd = objectStore.getObjectArtifact(oa.getId());
                    fullObjectArtifacts.add(objectArtifactToAdd);
                }
            }
        }
        catch(ObjectStoreException e)
        {
            e.printStackTrace();
        }
        return fullObjectArtifacts;
    }
    
    public String getMatchingObjectArtifactIdByFilePath(String filepath)
    {
        String filepathChangedFileSeparator = null;
        
        String objectArtifactId = null;
        Set<ObjectArtifact> objectArtifacts = this.getObjectArtifactsByType("OriginalContent");
        for(ObjectArtifact oa:  objectArtifacts)
        {
            // Add further checking
            if(oa.getType().equals("OriginalContent"))
            {
                OriginalContent oc = (OriginalContent) oa;
                if( oc.getFilePath().equals(filepath))
                    objectArtifactId = oc.getId();
            }
        }
        
        // Perform same check with the FileSeparator changed!
        if( objectArtifactId == null)
            filepathChangedFileSeparator = replaceFileSeparator(filepath);
        for(ObjectArtifact oa:  objectArtifacts)
        {
            if(oa.getType().equals("OriginalContent"))
            {
                OriginalContent oc = (OriginalContent) oa;
                if( oc.getFilePath().equals(filepathChangedFileSeparator))
                    objectArtifactId = oc.getId();
            }
        }
        
        // If filepath has not been found in object artifacts of type OriginalContent
        // proceed to Dissemination Manifestations
        if( objectArtifactId == null)
        {
            objectArtifacts = this.getObjectArtifactsByType("DisseminationManifestation");
            for(ObjectArtifact oa:  objectArtifacts)
            {
                // Add further checking
                if(oa.getType().equals("DisseminationManifestation"))
                {
                    DisseminationManifestation dm = (DisseminationManifestation) oa;
                    if( dm.getFilePath().equals(filepath))
                        objectArtifactId = dm.getId();
                }
            }
        }
        // Perform same check with the FileSeparator changed!
        if( objectArtifactId == null)
        {
            objectArtifacts = this.getObjectArtifactsByType("DisseminationManifestation");
            for(ObjectArtifact oa:  objectArtifacts)
            {
                // Add further checking
                if(oa.getType().equals("DisseminationManifestation"))
                {
                    DisseminationManifestation dm = (DisseminationManifestation) oa;
                    if( dm.getFilePath().equals(filepathChangedFileSeparator))
                        objectArtifactId = dm.getId();
                }
            }
        }
        
        // If filepath has not been found in object artifacts of type DisseminationManifestation
        // proceed to Preservation Manifestations and if not found there, then return null
        if( objectArtifactId == null)
        {
            objectArtifacts = this.getObjectArtifactsByType("PreservationManifestation");
            for(ObjectArtifact oa:  objectArtifacts)
            {
                // Add further checking
                if(oa.getType().equals("PreservationManifestation"))
                {
                    PreservationManifestation pm = (PreservationManifestation) oa;
                    if( pm.getFilePath().equals(filepath))
                        objectArtifactId = pm.getId();
                }
            }
        }
        // Perform same check with the FileSeparator changed!
        if( objectArtifactId == null)
        {
            objectArtifacts = this.getObjectArtifactsByType("PreservationManifestation");
            for(ObjectArtifact oa:  objectArtifacts)
            {
                // Add further checking
                if(oa.getType().equals("PreservationManifestation"))
                {
                    PreservationManifestation pm = (PreservationManifestation) oa;
                    if( pm.getFilePath().equals(filepathChangedFileSeparator))
                        objectArtifactId = pm.getId();
                }
            }
        }
        
        return objectArtifactId;
    }
    
    public String getMatchingArchivalObjectIdByDirectoryPath(String path) throws Exception
    {
        Properties properties = new Properties();
        try{
            properties.load(getClass().getClassLoader().getResourceAsStream("soapi.properties"));
        }
        catch(Exception e)
        {
            throw e;
        }
        
        String sipLocation = properties.get("sipDirectory").toString();
        
        ArchivalObject archivalObject = null;
        //Set<ArchivalObject> archivalObjects = null;
        Set<ArchivalObject> archivalObjects = new HashSet<ArchivalObject>();
        //Set<ObjectArtifact> objectArtifacts = null;
    //    boolean setInitialized = false;
        String archivalObjectId = null;
        
      //  ObjectArtifact objectArtifact = null;
        
        String sql = "SELECT * FROM archivalobjects a";
        SqlRowSet sqlRowSet = getJdbcTemplate().queryForRowSet(sql);
        
        if(sqlRowSet.next()) {
            archivalObject = new ArchivalObject();
            archivalObject.setId(sqlRowSet.getString("id"));
            archivalObject.setFilename(sqlRowSet.getString("filename"));
            archivalObject.setPath(sqlRowSet.getString("path"));
            archivalObjects.add(archivalObject);
        }
        
        String pppp = "C:/Temp/SIP/Fruits";
        if( pppp.contains(sipLocation))
            System.out.println("CORRECT");
        String replacedSipLocation = this.replaceFileSeparator(sipLocation);
        
        for(ArchivalObject ao: archivalObjects)
        {
            if(path.contains(sipLocation))
            {
                System.out.println(path.indexOf(sipLocation));
                path = path.replace(sipLocation + "/" , "");
                System.out.println("The path is:" + path + "*");
            }
            else if( path.contains(replacedSipLocation))
            {
                path = path.replace( replacedSipLocation + "\\", "");
                System.out.println("The new path is: " + path);
            }
            else if( path.startsWith("C:/") || path.startsWith("C:\\") )
            {
                System.out.println("True");
                System.out.println("Begin path=" + path);
                path = path.substring(path.indexOf("/")+1);
                System.out.println("End path=" + path);
            }
            System.out.println(ao.getId() + " " + ao.getFilename() + " " + ao.getPath());
            if( ao.getPath().equals(path))
                archivalObjectId = ao.getId();
        }

        
        /*
         *   
        ObjectArtifact objectArtifact = null;
        
        String sql = "SELECT * FROM objectartifacts o";
        SqlRowSet sqlRowSet = getJdbcTemplate().queryForRowSet(sql);


        if(sqlRowSet != null )
        {
            objectArtifacts = new HashSet<ObjectArtifact>(); 
            while( !sqlRowSet.isLast())
            {
                sqlRowSet.next();
                if(sqlRowSet.getString("type").equals(type))
                {
                    objectArtifact = new ObjectArtifact();
                    objectArtifact.setId(sqlRowSet.getString("id"));
                    objectArtifact.setType(sqlRowSet.getString("type"));
                    ArchivalObject archivalObject = 
                        archivalObjectDao.getArchivalObjectById(sqlRowSet.getString("archivalObjectId"));
                    objectArtifact.setArchivalObject(archivalObject);
                    objectArtifacts.add(objectArtifact);
                }
            }
        }
         */
        
        return archivalObjectId;
    }
    
    // Helper method used to ensure we don't miss the object artifact containing the
    // requested object due to '/' or '\' differences!
    public String replaceFileSeparator(String string)
    {
        String replaced = null;
        if( string.contains("\\"))
            replaced = string.replace('\\', '/'); 
        else if (string.contains("/"))
            replaced = string.replace('/', '\\');
        return replaced;
    }

   /* public MysqlConnectionPoolDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(MysqlConnectionPoolDataSource dataSource) {
        this.dataSource = dataSource;
    }*/

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
