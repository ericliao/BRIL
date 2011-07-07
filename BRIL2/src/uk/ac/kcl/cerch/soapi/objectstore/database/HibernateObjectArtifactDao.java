/*
 * Created on 21 Aug 2007
 */
package uk.ac.kcl.cerch.soapi.objectstore.database;

import java.util.Vector;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import uk.ac.kcl.cerch.soapi.objectstore.ObjectArtifact;

public class HibernateObjectArtifactDao extends HibernateDaoSupport implements ObjectArtifactDao {

    // TODO To be implemented in the future
    public ObjectArtifact getObjectArtifactById(String id) {
        return null;
    }

    public long saveObjectArtifact(ObjectArtifact objectArtifact) {
        return ((Long) getHibernateTemplate().save(objectArtifact)).longValue();
    }

	@Override
	public Vector<ObjectArtifact> getObjectArtifactByType(String type) {
		// TODO Auto-generated method stub
		return null;
	}

}
