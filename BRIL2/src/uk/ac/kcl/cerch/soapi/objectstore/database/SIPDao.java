/*
 * Created on 30 Aug 2007
 */
package uk.ac.kcl.cerch.soapi.objectstore.database;

import uk.ac.kcl.cerch.soapi.sip.SIP;

public interface SIPDao {
    public SIP getSIPById(String id);
    public String saveSIP(SIP sip);
}
