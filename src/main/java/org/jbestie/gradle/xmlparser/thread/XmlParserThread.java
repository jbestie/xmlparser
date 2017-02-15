package org.jbestie.gradle.xmlparser.thread;

import org.apache.log4j.Logger;
import org.jbestie.gradle.xmlparser.service.XmlEntryService;
import org.jbestie.gradle.xmlparser.utils.ApplicationConstants;
import org.jbestie.gradle.xmlparser.vo.XmlEntry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Validator;
import java.io.File;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Stack;

/**
 * Thread to parse incoming xml and put it to DB
 */
public class XmlParserThread implements Runnable {
    private static final String ROOT_ELEMENT = "Entry";
    private static final String CONTENT_ELEMENT = "content";
    private static final String CREATION_DATE = "creationDate";
    private static final String DATE_XML_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final Map<String, String> configuration;
    private Validator xmlValidator;
    private final Integer threadId;

    private final Logger logger = Logger.getLogger(XmlParserThread.class);
    private volatile Stack<File> filesQueue;
    private XmlEntryService entryService = new XmlEntryService();

    public XmlParserThread(Stack<File> filesQueue, Validator xmlValidator, Map<String, String> configuration, Integer threadId) {
        this.filesQueue = filesQueue;
        this.xmlValidator = xmlValidator;
        this.configuration = configuration;
        this.threadId = threadId;
    }

    @Override
    public void run() {
        logger.debug(" Started thread " + threadId);
        while (filesQueue.size() > 0) {
            File xmlFile;
            synchronized (filesQueue) {
                xmlFile = filesQueue.pop();
            }

            if (xmlFile == null || !xmlFile.exists() ) {
                logger.warn("File " + (xmlFile == null ? "" : xmlFile.getName()) + " doesn't exist");
                continue;
            }

            Document document = null;
            try {
                // read XML file
                logger.debug("Processing " + xmlFile.getName());
                DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                document = parser.parse(xmlFile);
                xmlValidator.validate(new DOMSource(document));
            } catch (Exception ex) {
                String failedDirectory = configuration.get(ApplicationConstants.CONFIG_XML_FAILED_DIR);
                xmlFile.renameTo(new File(failedDirectory + File.separator + xmlFile.getName()));
                logger.warn(ex.getMessage());
                continue;
            }

            logger.debug("File " + xmlFile.getName() + " has correct file structure");

            // parse xml
            NodeList nodeList = document.getElementsByTagName(ROOT_ELEMENT);
            Node nNode = nodeList.item(0);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                String content = eElement.getElementsByTagName(CONTENT_ELEMENT).item(0).getTextContent();
                String date = eElement.getElementsByTagName(CREATION_DATE).item(0).getTextContent();
                SimpleDateFormat formatter = new SimpleDateFormat(DATE_XML_FORMAT);

                Timestamp creationDate;
                try {
                    creationDate = new Timestamp(formatter.parse(date).getTime());
                    XmlEntry entry = new XmlEntry(xmlFile.getName(), content, creationDate);

                    // store to DB
                    entryService.createEntry(entry);
                } catch (ParseException e) {
                    logger.error(e.getMessage());
                    String failedDirectory = configuration.get(ApplicationConstants.CONFIG_XML_FAILED_DIR);
                    xmlFile.renameTo(new File(failedDirectory + File.separator + xmlFile.getName()));
                    continue;
                } catch (RuntimeException ex) {
                    logger.error(ex.getMessage());
                    String failedDirectory = configuration.get(ApplicationConstants.CONFIG_XML_FAILED_DIR);
                    xmlFile.renameTo(new File(failedDirectory + File.separator + xmlFile.getName()));
                    continue;
                }

            }

            // move to processed
            String destinationDirectory = configuration.get(ApplicationConstants.CONFIG_XML_DST_DIR);
            xmlFile.renameTo(new File(destinationDirectory + File.separator + xmlFile.getName()));

            logger.debug("File " + xmlFile.getName() + " successfully processed");
        }

        logger.debug(" Finished thread " + threadId);
    }
}
