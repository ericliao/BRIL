package uk.ac.kcl.cerch.soapi.utils;

/**
 * Helper class used to create RDF statements for the Fedora Repository. It is specific to
 * this project and intended only for RELS-INT; these, are internal object relationships and
 * not RELS-EXT which represent relationships between Fedora objects.
 * 
 * An example statement is the following
 * 
 *    <rdf:RDF xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:fedora="info:fedora/fedora-system:def/relations-external#" xmlns:myns="AHDS SCHEMA" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
 *      <rdf:Description rdf:about="File_Preservation.1">
 *          <myns:isPreservationManifestation rdf:resource="File_Original.1"/>
 *      </rdf:Description>
 *    </rdf:RDF>
 * 
 * @author Andreas Mavrides
 *
 */
public class RDFGenerator {

    /**
     * Main overloaded method that accepts five arguments to construct an RDF statement.
     * It accepts the namespace, as well as the schema for the RDF statement.
     * This is the only method that actually constructs an RDF statement. The other two
     * overloaded methods, use less arguments and simply call this method with null arguments
     * accordingly.
     * 
     * @param namespace The namespace for the RDF statement
     * @param schema The schema on which the RDF statement conforms to
     * @param subject The subject of the RDF statement
     * @param object The object of the RDF statement
     * @param predicate The predicate to be used in constructing the RDF statement 
     * @return String Generated RDF statement
     */
    public String generateRDF(String namespace, String schema,
            String subject, String object, String predicate)
    {
        String rdfStatement = null;
        String namespacePrefix ="";
        if(namespace == null)
            namespace = "";
        else if(namespace != null && !namespace.equals(""))
        {
            namespace = " " + namespace;
            namespacePrefix = namespace.substring(namespace.indexOf("xmlns:"), namespace.indexOf("="));
            namespacePrefix = namespacePrefix.substring(namespacePrefix.indexOf(":")+1);
        }

        if( schema == null)
            schema = "";
        else if(schema != null && !schema.equals(""))
            schema = " " + schema;
        
        rdfStatement = "<rdf:RDF" + schema + namespace + ">";
        rdfStatement += "\n\t" + "<rdf:Description rdf:about=\"" + object + "\">";
        rdfStatement += "\n\t\t" + "<" + namespacePrefix + ":" + predicate +" rdf:resource=\"" + subject +"\"/>";
        rdfStatement += "\n\t" + "</rdf:Description>";
        rdfStatement += "\n" +"</rdf:RDF>";
        
        return rdfStatement;
    }
    
    /**
     * Overloaded method that accepts four arguments excluding the schema. It calls the main
     * overloaded method with five arguments, with the schema set to null
     * 
     * @param namespace The namespace for the RDF statement
     * @param subject The subject of the RDF statement
     * @param object The object of the RDF statement
     * @param predicate The predicate to be used in constructing the RDF statement 
     * @return String Generated RDF statement
     */
    public String generateRDF(String namespace, String subject,
            String object, String predicate)
    {
        return generateRDF(namespace, null, subject, object, predicate);
    }
    
    /**
     * Overloaded method that accepts three arguments excluding the schema and the namespace.
     * It calls the main overloaded method with five arguments, with the schema and namespace
     * set to null
     * 
     * @param subject The subject of the RDF statement
     * @param object The object of the RDF statement
     * @param predicate The predicate to be used in constructing the RDF statement 
     * @return String Generated RDF statement
     */
    public String generateRDF(String subject, String object, String predicate)
    {
        return generateRDF(null, null, subject, object, predicate);
    }
}
