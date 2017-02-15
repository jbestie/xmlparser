package org.jbestie.gradle.xmlparser;

import org.apache.log4j.Logger;
import org.jbestie.gradle.xmlparser.thread.XmlParserThread;
import org.jbestie.gradle.xmlparser.utils.ApplicationConstants;
import org.jbestie.gradle.xmlparser.utils.HibernateUtils;
import org.jbestie.gradle.xmlparser.utils.ConfigurationUtils;
import org.jbestie.gradle.xmlparser.utils.ValidationUtils;

import java.io.File;
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
                service.submit(new XmlParserThread(fileStack, ValidationUtils.createValidator(), configuration, i));
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

}
