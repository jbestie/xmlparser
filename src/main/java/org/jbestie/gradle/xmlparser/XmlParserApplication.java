package org.jbestie.gradle.xmlparser;

import org.apache.log4j.Logger;
import org.jbestie.gradle.xmlparser.thread.XmlParserThread;
import org.jbestie.gradle.xmlparser.utils.ApplicationConstants;
import org.jbestie.gradle.xmlparser.utils.HibernateUtils;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by bestie on 12.02.2017.
 */
public class XmlParserApplication {

    private static Logger logger = Logger.getLogger(XmlParserApplication.class);

    private static Map<String, String> configuration = new HashMap<>();

    public static void main(String[] args) {
        Instant start = Instant.now();

        validatePath(ApplicationConstants.XML_SRC_PARAMETER_NAME);
        validatePath(ApplicationConstants.XML_DST_PARAMETER_NAME);
        validatePath(ApplicationConstants.XML_FAILED_PARAMETER_NAME);

        configuration.put(ApplicationConstants.XML_SRC_PARAMETER_NAME, System.getProperty(ApplicationConstants.XML_SRC_PARAMETER_NAME));
        configuration.put(ApplicationConstants.XML_DST_PARAMETER_NAME, System.getProperty(ApplicationConstants.XML_DST_PARAMETER_NAME));
        configuration.put(ApplicationConstants.XML_FAILED_PARAMETER_NAME, System.getProperty(ApplicationConstants.XML_FAILED_PARAMETER_NAME));

        File sourceDirectory = new File (configuration.get(ApplicationConstants.XML_SRC_PARAMETER_NAME));

        // TODO cron

        Stack<File> fileStack = new Stack<>();
        for (File file : sourceDirectory.listFiles()) {
            fileStack.push(file);
            logger.debug("Adding file " + file.getName() + " to process queue");
        }

        int numberOfThreads = 8;

        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);


        for (int i = 0; i < numberOfThreads; i++) {
            service.submit(new XmlParserThread(fileStack, createValidator(), configuration, i));
        }


        service.shutdown();

        try {
            service.awaitTermination(2, TimeUnit.HOURS);
        } catch (InterruptedException ie) {
            service.shutdownNow();
        }


        Instant end = Instant.now();
        logger.info("We done in " + Duration.between(start,end));

        if (service.isShutdown()) {
            HibernateUtils.getSessionFactory().close();
        }
    }


    private static void validatePath(String systemParameterName) {
        String srcDirectory = System.getProperty(systemParameterName);
        if (srcDirectory == null) {
            logger.error("Parameter -D" + systemParameterName + " has to be specified!");
            throw new IllegalArgumentException("Parameter -D" + systemParameterName + " has to be specified!");
        }

        if (!new File(srcDirectory).exists()) {
            logger.error("-D" + systemParameterName + " parameter has to be set to existing directory!");
            throw new IllegalArgumentException("-D" + systemParameterName + " parameter has to be set to existing directory!");
        }

        if (!new File(srcDirectory).isDirectory()) {
            logger.error("-D" + systemParameterName + " parameter has to be set to directory!");
            throw new IllegalArgumentException("-D" + systemParameterName + " parameter has to be set to directory!");
        }
    }


    private static Validator createValidator() {
        // create a SchemaFactory capable of understanding WXS schemas
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        // load a WXS schema, represented by a Schema instance
        ClassLoader classLoader = XmlParserApplication.class.getClassLoader();
        Source schemaFile = new StreamSource(classLoader.getResource("xsd/entry.xsd").getFile());

        Schema schema;
        try {
            schema = factory.newSchema(schemaFile);
        } catch (SAXException ex) {
            logger.error(ex.getMessage());
            throw new RuntimeException(ex);
        }

        // create a Validator instance, which can be used to validate an instance document
        return schema.newValidator();

    }
}
