/*
 * Created on 30 Aug 2007
 */
package uk.ac.kcl.cerch.soapi.objectstore.database;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

import uk.ac.kcl.cerch.soapi.objectstore.ArchivalObject;
import uk.ac.kcl.cerch.soapi.sip.SIP;

public class JdbcSIPDao extends JdbcDaoSupport implements SIPDao {
    private ArchivalObjectDao archivalObjectDao;

    public ArchivalObjectDao getArchivalObjectDao() {
        return archivalObjectDao;
    }

    public void setArchivalObjectDao(ArchivalObjectDao archivalObjectDao) {
        this.archivalObjectDao = archivalObjectDao;
    }

    @SuppressWarnings("unchecked")
    public SIP getSIPById(String id) {
        SIP sip = new SIP();
        sip.setId(id);
        
        String sql = "SELECT Id FROM archivalobjects WHERE SIPID = '" + sip.getId() + "'";
        List<String> listArchivalObjectIds = getJdbcTemplate().queryForList(sql, String.class);
        
        Set<ArchivalObject> setArchivalObjects = new HashSet<ArchivalObject>();

        for(String archivalObjectId : listArchivalObjectIds) {
            ArchivalObject archivalObject = archivalObjectDao.getArchivalObjectById(archivalObjectId);
            setArchivalObjects.add(archivalObject);
            archivalObject.setSip(sip);
        }
        
        sip.setArchivalObjects(setArchivalObjects);
            
        return sip;
    }

    public String saveSIP(SIP sip) {
    	System.out.println(sip.getId());
        String sql = "INSERT INTO sips(Id) VALUES('" + sip.getId() + "')";
        getJdbcTemplate().update(sql);
        
        if((sip.getArchivalObjects() != null) && (sip.getArchivalObjects().size() > 0)) {
           for(ArchivalObject archivalObject : sip.getArchivalObjects()) {
               archivalObjectDao.saveArchivalObject(archivalObject);
           }
        }
        
        return sip.getId();
    }
}
