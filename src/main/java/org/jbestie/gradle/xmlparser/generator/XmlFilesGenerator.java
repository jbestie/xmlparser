package org.jbestie.gradle.xmlparser.generator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Generates the xml files in specified directory
 */
public class XmlFilesGenerator {
    public static void main(String[] args) {
        String srcDirectory = System.getProperty("xml_dst_content");

        if (srcDirectory == null) {
            throw new IllegalArgumentException("xml_dst_content parameter should be specified");
        }

        File destination = new File(srcDirectory);
        if (!destination.exists() || !destination.isDirectory()) {
            throw new IllegalArgumentException("xml_dst_content parameter should be path to existing directory");
        }



        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
            docBuilder = docFactory.newDocumentBuilder();
            int desiredQuantity = 1000000;
            for (int i = 0; i < desiredQuantity; i++) {

                // root elements
                Document doc = docBuilder.newDocument();
                Element rootElement = doc.createElement("Entry");
                doc.appendChild(rootElement);

                // staff elements
                Element content = doc.createElement("content");
                content.appendChild(doc.createTextNode("Somestuff id" + i));
                rootElement.appendChild(content);

                Element creationDate = doc.createElement("creationDate");

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                creationDate.appendChild(doc.createTextNode(dateFormat.format(new Date())));
                rootElement.appendChild(creationDate);

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File(srcDirectory + File.separator + i + ".xml"));
                transformer.transform(source, result);
            }
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }
}
