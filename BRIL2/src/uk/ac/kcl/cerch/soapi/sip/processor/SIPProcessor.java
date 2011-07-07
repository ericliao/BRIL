/*
 * Created on 16 Aug 2007
 */
package uk.ac.kcl.cerch.soapi.sip.processor;

import uk.ac.kcl.cerch.soapi.sip.SIP;

public interface SIPProcessor {
    public void processSIP(SIP sip) throws SIPProcessorException;
}
