package org.jbestie.gradle.xmlparser;

import org.apache.log4j.Logger;
import org.jbestie.gradle.xmlparser.thread.XmlParserThread;
import org.jbestie.gradle.xmlparser.utils.ApplicationConstants;
import org.jbestie.gradle.xmlparser.utils.HibernateUtils;
import org.jbestie.gradle.xmlparser.utils.ConfigurationUtils;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.StringReader;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main application class which starts the application
 */
public class XmlParserApplication {

    private static Logger logger = Logger.getLogger(XmlParserApplication.class);

    private static Map<String, String> configuration = new HashMap<>();

    public static void main(String[] args) {

        try {
            // get config
            configuration = ConfigurationUtils.parseApplicationConfig(args);

            // initialize the configuration
            HibernateUtils.initializeSessionFactoryConfig(configuration);
        } catch (IllegalArgumentException ex) {
            logger.warn(ex.getMessage());
            logger.warn(ConfigurationUtils.generateHelpFile());
            return;
        }

        final Long monitoringPeriod = Long.parseLong(configuration.get(ApplicationConstants.CONFIG_MONITORING_PERIOD));
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleWithFixedDelay(()->{
            // begin of thread
            Instant start = Instant.now();
            File sourceDirectory = new File (configuration.get(ApplicationConstants.CONFIG_XML_SRC_DIR));

            // push all files to stack
            Stack<File> fileStack = new Stack<>();
            File[] files = sourceDirectory.listFiles();
            if (files != null) {
                for (File file :files) {
                    fileStack.push(file);
                    logger.debug("Adding file " + file.getName() + " to process queue");
                }
            } else {
                logger.info("Source folder is empty");
            }

            // check the desired quantity of threads
            int numberOfThreads = 4;
            if (configuration.containsKey(ApplicationConstants.CONFIG_MAX_PROC_THREADS)) {
                numberOfThreads = Integer.valueOf(configuration.get(ApplicationConstants.CONFIG_MAX_PROC_THREADS));
            }


            // create service & submit tasks
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
            // end of thread
        }, 0, monitoringPeriod, TimeUnit.SECONDS);

    }


    /**
     * Creates the validator to validate incoming xml file.
     *
     * @return {@link Validator} for xml validation
     */
    private static Validator createValidator() {
        // create a SchemaFactory capable of understanding WXS schemas
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        // dumb compressed xsd schema because of issues with xsd-file in jar :(
        String xsdSchema = "<xs:schema attributeFormDefault=\"unqualified\" elementFormDefault=\"qualified\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" +
                "<xs:element name=\"Entry\"><xs:complexType><xs:sequence><xs:element type=\"xs:string\" name=\"content\">" +
                "<xs:annotation><xs:documentation>строка длиной до 1024 символов</xs:documentation></xs:annotation>" +
                "</xs:element><xs:element type=\"xs:string\" name=\"creationDate\"><xs:annotation><xs:documentation>дата создания записи</xs:documentation>\n" +
                "</xs:annotation></xs:element></xs:sequence></xs:complexType></xs:element></xs:schema>";

        Source schemaFile = new StreamSource(new StringReader(xsdSchema));

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
