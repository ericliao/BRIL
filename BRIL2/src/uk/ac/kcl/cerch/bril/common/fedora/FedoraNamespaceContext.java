package uk.ac.kcl.cerch.bril.common.fedora;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;


/**
 *
 */
public final class FedoraNamespaceContext implements NamespaceContext
{

    private static Logger log = Logger.getLogger( FedoraNamespaceContext.class );

   public enum FedoraNamespace
    {

        XML( XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI ),
        RDF( "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"),
        RDFS( "rdfs", "http://www.w3.org/2000/01/rdf-schema#" ),
        DC( "dc", "http://purl.org/dc/elements/1.1/" ),
        OAI_DC( "oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/"),
        FEDORA( "fedora", "info:fedora/" ),
        FEDORARELSEXT( "rel", "info:fedora/fedora-system:def/relations-external#" ),
        FEDORAMODEL( "fedora-model", "info:fedora/fedora-system:def/model#" ),
        FEDORAVIEW( "fedora-view", "info:fedora/fedora-system:def/view#" ),
        FOXML( "foxml", "info:fedora/fedora-system:def/foxml#" ),
        METS("METS","http://www.loc.gov/METS/"),
        BRILRELS( "bril_rel", "http://bril-dev.cerch.kcl.ac.uk/relationship#" ),
        BRIL( "bril", "http://bril-dev.cerch.kcl.ac.uk/#" ),
        OPMV( "opmv", "http://purl.org/net/opmv/ns#" ),
        CRYST("cryst","http://bril-dev.cerch.kcl.ac.uk/crystallography#");
       
        private String prefix;
        private String uri;

        FedoraNamespace( String prefix, String URI )
        {
            this.prefix = prefix;
            this.uri = URI;
        }


        /**
         *
         * @return prefix of the enum
         */
        public String getPrefix()
        {
            return this.prefix;
        }


        /**
         *
         * @return URI of the enum
         */
        public String getURI()
        {
            return this.uri;
        }


        public String getElementURI( String element )
        {
            return this.uri + element;
        }


    }

    /**
     * finds an {@link FedoraNamespace} given a prefix
     * @param prefix the prefix to look in the enums for
     * @return FedoraNamespace type if found, null otherwise
     */
    public FedoraNamespace getNamespace( String prefix )
    {
        FedoraNamespace ns = null;
        for( FedoraNamespace osns : FedoraNamespace.values() )
        {
            if( osns.prefix.equals( prefix ) )
            {
                ns = osns;
            }
        }
        return ns;
    }


    /**
     * Empty constructor
     */
    public FedoraNamespaceContext()
    {
    }


    /**
     * @param prefix a String giving the prefix of the namespace for which to search
     * @return the uri of the namespace that has the given prefix
     */
    @Override
    public String getNamespaceURI( String prefix )
    {
        FedoraNamespace namespace = this.getNamespace( prefix );
        return namespace.uri;
    }


    /**
     * returns an {@link Iterator<String>} of prefixes that matches
     * {@code namespaceURI}
     *
     * @param namespaceURI the uri to search for prefixes for
     * @return an Iterator containing prefixes
     */
    @Override
    public Iterator<String> getPrefixes( String namespaceURI )
    {
        List<String> prefixes = new ArrayList<String>();

        for( FedoraNamespace ns : FedoraNamespace.values() )
        {
            if( ns.uri.equals( namespaceURI ) )
            {
                prefixes.add( ns.prefix );
            }
        }

        return prefixes.iterator();
    }


    /**
     * Gets the {@code prefix} associated with {@code uri}
     * @param uri the {@code uri} to find a {@code prefix} for
     * @return the {@code prefix} that matched {@code uri}
     */
    @Override
    public String getPrefix( String uri )
    {
        return this.getPrefix( uri );
    }
public static void main(String []args){
	QName  one = new QName( FedoraNamespace.FEDORARELSEXT.getURI(),
            "isMemberOfCollection",
           FedoraNamespace.FEDORA.getPrefix()); 
	QName one1 = new QName ("","id001",FedoraNamespace.BRIL.getPrefix());
		
		
	//String two = FedoraNamespace.FEDORARELSEXT.getURI();//+ FedoraNamespace.FEDORA.getPrefix();
	System.out.println(one);
	System.out.println(one1);
	System.out.println(one1.getPrefix());
//	System.out.println(one.getPrefix());
	
	//System.out.println(two);
}

}
 