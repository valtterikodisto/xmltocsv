package com.valtterikodisto.xmlparser;

import java.util.HashMap;
import org.w3c.dom.Node;

/* Every tag will be converted to XmlTagObject to easily access its data */

public class XmlTagObject {
    
    private Node node;
    private String tagName;
    private String value;
    private HashMap<String, String> attributes;
    
    public XmlTagObject(Node node) {
        this.node = node;
        
        try {
            this.tagName = node.getNodeName();
            this.value = node.getTextContent();
            this.attributes = new HashMap<>();

            for (int i=0; i < node.getAttributes().getLength(); i++) {
                Node attribute = node.getAttributes().item(i);
                this.attributes.put(attribute.getNodeName(), attribute.getNodeValue());
            }
        } catch (Exception ex) {
            this.tagName = "";
            this.value = "";
            this.attributes = new HashMap<>();
        }
    }

    public Node getNode() {
        return node;
    }

    public String getTagName() {
        return tagName;
    }

    public String getValue() {
        return value;
    }

    public String getAttribute(String attributeName) {
        return attributes.getOrDefault(attributeName, "");
    }
    
}
