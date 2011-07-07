/*
 * Created on 16 Aug 2007
 */
package uk.ac.kcl.cerch.soapi.sip.processor;

import uk.ac.kcl.cerch.soapi.sip.SIP;

public interface SIPRDFBuilder {
    public void buildRDF(SIP sip) throws SIPProcessorException;
}
