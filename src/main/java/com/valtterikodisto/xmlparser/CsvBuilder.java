package com.valtterikodisto.xmlparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.core.io.ResourceLoader;
import org.w3c.dom.Element;

/* CsvBuilder builds a CSV file based on the given XML file and CSV template files */


public class CsvBuilder {

    // Only method we need to call when we want to build csv file
    // Parsing invoice record is straight forward but since there can be
    // multiple 'Invoice Row', we need to loop them
    
    public static String build(File xmlFile, File invoiceRecordTempate, File invoiceRowRecordTemplate) throws FileNotFoundException, ElementNotFoundException {
        String csv = "";
        Element root = XmlParser.getRootElementOfFile(xmlFile);

        csv += parseTemplate(root, invoiceRecordTempate) ;

        List<XmlTagObject> xmlTagObjects = XmlParser.getTagList(root, "InvoiceRow");
        
        for (XmlTagObject object : xmlTagObjects) {
            csv += parseTemplate((Element) object.getNode(), invoiceRowRecordTemplate);
        }
        
        return csv;
        
    }

    // Root element is for example <Finvoice></Finvoice> in the XML file
    // Loops the template file and based on it builds the CSV file
    // Read more about the template file in README.md
    
    private static String parseTemplate(Element root, File template) throws ElementNotFoundException, FileNotFoundException {
        String csv = "";
        Scanner scanner = new Scanner(template);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isEmpty()) {
                continue;
            }
            csv += getData(root, line) + ";";
        }
        
        return csv + "\n";
    }

    // Gets whatever data template file tells it to get
    // Line represents a line in the template file

    private static String getData(Element root, String line) throws ElementNotFoundException {
        /* No data was give => EMPTY */

        if (line.equals("EMPTY")) return "";
        
        /* Hardcoded values => [value] */
        
        if (line.matches("\\[.+\\]")) return line.substring(1, line.length()-1);
        
        /* Tag's attributes => TagName:AttributeName */

        String[] split = line.split(":");
        if (split.length > 1) {
            XmlTagObject object = XmlParser.getTag(root, split[0]);
            return object.getAttribute(split[1]);
        }
        
        XmlTagObject tag = XmlParser.getTag(root, line);
        
        /* We need to convert following data formats */
        
        if (tag.getTagName().equals("BuyerPartyDetails")) {
            return convertBuyerAddress(tag);
        } else if (tag.getTagName().equals("DeliveryPartyDetails")) {
            return convertDeliveryAddress(tag);
        } else if (tag.getAttribute("Format").equals("CCYYMMDD")) {
            return convertDate(tag);
        }

        /* Normal tag value => TagName */
        
        return tag.getValue();
    }
    
    // Converts buyer address to "\" separated format
    private static String convertBuyerAddress(XmlTagObject buyerPartyDetails) {
        List<String> details = new ArrayList<>();
        
        try {
            details.add(XmlParser.getTag(buyerPartyDetails.getNode(), "BuyerOrganisationName").getValue());
            details.add(XmlParser.getTag(buyerPartyDetails.getNode(), "BuyerStreetName").getValue());
            details.add(XmlParser.getTag(buyerPartyDetails.getNode(), "BuyerPostCodeIdentifier").getValue());
            details.add(XmlParser.getTag(buyerPartyDetails.getNode(), "BuyerTownName").getValue());
            details.add(XmlParser.getTag(buyerPartyDetails.getNode(), "CountryCode").getValue());
            return String.join("\\", details);
        } catch (ElementNotFoundException ex) {
            return "";
        }
    }
    
    // Converts delivery address to "\" separated format
    private static String convertDeliveryAddress(XmlTagObject deliveryPartyDetails) {
        List<String> details = new ArrayList<>();

        try {
            details.add(XmlParser.getTag(deliveryPartyDetails.getNode(), "DeliveryOrganisationName").getValue());
            details.add(XmlParser.getTag(deliveryPartyDetails.getNode(), "DeliveryStreetName").getValue());
            details.add(XmlParser.getTag(deliveryPartyDetails.getNode(), "DeliveryPostCodeIdentifier").getValue());
            details.add(XmlParser.getTag(deliveryPartyDetails.getNode(), "DeliveryTownName").getValue());
            details.add(XmlParser.getTag(deliveryPartyDetails.getNode(), "CountryCode").getValue());
            return String.join("\\", details);
        } catch (ElementNotFoundException ex) {
            return "";
        }
    }

    // Converts date to "." separated format
    private static String convertDate(XmlTagObject date) {
        String oldFormat = "yyyyMMdd";

        SimpleDateFormat finvoiceDate = new SimpleDateFormat(oldFormat);
        SimpleDateFormat procountorDate = new SimpleDateFormat("d.M.yyyy");

        try {
            return procountorDate.format(finvoiceDate.parse(date.getValue()));
        } catch (ParseException ex) {
            System.out.println("Could not parse the date");
        }

        return "";
    }

}