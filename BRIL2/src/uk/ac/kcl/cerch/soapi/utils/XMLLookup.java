package uk.ac.kcl.cerch.soapi.utils;

import java.io.File;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import uk.ac.kcl.cerch.soapi.SOAPIException;

public class XMLLookup {

    // TODO If method is to be used, maybe convert to CustomProperties
    public Properties readXML(String filePath, String elementName, String firstTagname, String secondTagname)
        throws SOAPIException
    {
        Properties properties = null;
        File file = null;
        
        try{
            file = new File(filePath);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName(elementName);

            if(nodeList != null)
            {
                if(nodeList.getLength() > 0 && nodeList.item(0).getNodeType() == Node.ELEMENT_NODE)
                    properties = new Properties();
            }          
            
            for (int i = 0; i < nodeList.getLength(); i++)
            {
                Node node = nodeList.item(i);
                System.out.println(node.getNodeName());
                System.out.println(node.getFirstChild().getNodeName());
                
                
                if (node.getNodeType() == Node.ELEMENT_NODE) 
                {
                    Element element = (Element) node;
                    NodeList firstTagNameElementList = element.getElementsByTagName(firstTagname);
                    Element firstTagnameElement = (Element) firstTagNameElementList.item(0);
                    NodeList firstTagnameNodeList = firstTagnameElement.getChildNodes();
                    String key = ((Node) firstTagnameNodeList.item(0)).getNodeValue();
                    NodeList lastTagnameElementList = element.getElementsByTagName(secondTagname);
                    Element lastTagnameElement = (Element) lastTagnameElementList.item(0);
                    NodeList lastTagnameNodeList = lastTagnameElement.getChildNodes();
                    String value = ((Node) lastTagnameNodeList.item(0)).getNodeValue();
                    if( key != null && value != null)
                        properties.put(key, value);
                }
            }
        }
        catch(Exception e)
        {
            throw new SOAPIException(e);
        }
        return properties;
    }
    
    // Only works with our XML lookup-file 
    public String searchXMLbyKey(String filePath, String key) throws SOAPIException
    {
        String elementName = "fileformat";
        String firstTagname = "source";
        String secondTagname = "target";
        Properties properties = this.readXML(filePath, elementName, firstTagname, secondTagname);
        return properties.getProperty(key);
    }
}
