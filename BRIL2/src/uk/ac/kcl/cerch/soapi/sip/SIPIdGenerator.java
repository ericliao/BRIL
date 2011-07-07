/*
 * Created on 14 Aug 2007
 */
package uk.ac.kcl.cerch.soapi.sip;

/**
 * Generates unique identifiers for <code>SIP</code>s.
 * The default implementation could use an incremental sequence of numbers for the identifiers. 
 *   
 */
public interface SIPIdGenerator {
    public String generateId() throws SIPIdGeneratorException;
    public String generateId(String prefix) throws SIPIdGeneratorException;
}