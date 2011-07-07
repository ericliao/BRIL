/*
 * Created on 20 Aug 2007
 */
package uk.ac.kcl.cerch.soapi.objectstore.database;

import uk.ac.kcl.cerch.soapi.objectstore.ArchivalObject;

public interface ArchivalObjectDao {
    public ArchivalObject getArchivalObjectById(String id);
    public String saveArchivalObject(ArchivalObject archivalObject);
}
