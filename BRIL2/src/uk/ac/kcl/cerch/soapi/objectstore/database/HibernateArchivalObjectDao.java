/*
 * Created on 20 Aug 2007
 */
package uk.ac.kcl.cerch.soapi.objectstore.database;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import uk.ac.kcl.cerch.soapi.objectstore.ArchivalObject;

public class HibernateArchivalObjectDao extends HibernateDaoSupport implements ArchivalObjectDao {
    public ArchivalObject getArchivalObjectById(String id) {
        return (ArchivalObject) getHibernateTemplate().load(ArchivalObject.class, id);
    }

    public String saveArchivalObject(ArchivalObject archivalObject) {
        return (String) getHibernateTemplate().save(archivalObject);
    }
}
