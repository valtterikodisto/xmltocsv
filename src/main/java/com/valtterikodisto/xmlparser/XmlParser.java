package com.valtterikodisto.xmlparser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/* XmlParser finds the right tag using recursion */

public class XmlParser {
    
    // Finds the root element of the given file. Ex. <Finvoice></Finvoice>
    public static Element getRootElementOfFile(File file) throws ElementNotFoundException {

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();

            return document.getDocumentElement();
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            throw new ElementNotFoundException("Could parse the file");
        }
    }
    

    // Returns the first target tag
    // If there was no targetTag, it returns a null XmlTagObject which always returns
    // an empty string. So there is a possibility to add all the Procountor CSV fields
    // in the template file and they render an empty cell if Finvoice XML file did not
    // have those fields.
    public static XmlTagObject getTag(Node parentTag, String targetTag) throws ElementNotFoundException {
        List<XmlTagObject> listOfTags = getTagList(parentTag, targetTag);
        if (!listOfTags.isEmpty()) {
            return listOfTags.get(0);
        }
        return new XmlTagObject(null);
    }

    // Returns a list of target tags
    public static List<XmlTagObject> getTagList(Node parentTag, String targetTag) throws ElementNotFoundException {
        parentTag.getChildNodes();

        List<XmlTagObject> listOfTags = new ArrayList<>();

        if (parentTag.getNodeType() == Node.ELEMENT_NODE) {
            findTag((Element) parentTag, targetTag, listOfTags);
        }
        
        return listOfTags;
    }
    
    // Method search recursively for a tag by the tagName and adds it to the list 
    private static void findTag(Element element, String tagName, List<XmlTagObject> listOfTags) throws ElementNotFoundException {

        if (element == null) {
            throw new ElementNotFoundException("Could not locate " + tagName);
        }

        NodeList children = element.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {

            Node node = children.item(i);
            
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                
                // If the tag is the target tag => add to the list
                if (node.getNodeName().equals(tagName)) {
                    listOfTags.add(new XmlTagObject(node));
                }
                // Recurse
                findTag((Element) node, tagName, listOfTags);
                
            }

        }
    }
    
}

class ElementNotFoundException extends Exception {

    public ElementNotFoundException(String errorMessage) {
        super(errorMessage);
    }

}
