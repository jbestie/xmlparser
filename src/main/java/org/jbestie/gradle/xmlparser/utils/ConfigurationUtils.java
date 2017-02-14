package org.jbestie.gradle.xmlparser.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Application's configuration utils
 */
public final class ConfigurationUtils {
    private ConfigurationUtils(){}

    /**
     * Parses the arguments specified for application
     *
     * @param args - incoming application parameters
     *
     * @return filled {@link Map} with configuration
     */
    public static Map<String, String> parseApplicationConfig(String[] args) {
        Map<String, String> applicationConfig = new HashMap<>();

        for (String value : args) {

            // parsing source
            if (value.startsWith(ApplicationConstants.APPLICATION_PARAMETER_SRC)
                    && !applicationConfig.containsKey(ApplicationConstants.CONFIG_XML_SRC_DIR)) { // ignore extra parameters
                String source = value.substring(ApplicationConstants.APPLICATION_PARAMETER_SRC.length());
                validatePath(ApplicationConstants.APPLICATION_PARAMETER_SRC, source);
                applicationConfig.put(ApplicationConstants.CONFIG_XML_SRC_DIR, source);

            // parsing destination
            } else if (value.startsWith(ApplicationConstants.APPLICATION_PARAMETER_DST)
                    && !applicationConfig.containsKey(ApplicationConstants.CONFIG_XML_DST_DIR)) { // ignore extra parameters
                String destination = value.substring(ApplicationConstants.APPLICATION_PARAMETER_DST.length());
                validatePath(ApplicationConstants.APPLICATION_PARAMETER_DST, destination);
                applicationConfig.put(ApplicationConstants.CONFIG_XML_DST_DIR, destination);

            // parsing failed
            } else if (value.startsWith(ApplicationConstants.APPLICATION_PARAMETER_FAILED)
                    && !applicationConfig.containsKey(ApplicationConstants.CONFIG_XML_FAILED_DIR)) { // ignore extra parameters
                String failed = value.substring(ApplicationConstants.APPLICATION_PARAMETER_FAILED.length());
                validatePath(ApplicationConstants.APPLICATION_PARAMETER_FAILED, failed);
                applicationConfig.put(ApplicationConstants.CONFIG_XML_FAILED_DIR, failed);

            // parsing db url
            } else if (value.startsWith(ApplicationConstants.APPLICATION_PARAMETER_DB_URL)
                    && !applicationConfig.containsKey(ApplicationConstants.CONFIG_CONNECTION_URL)) { // ignore extra parameters
                String dbUrl = value.substring(ApplicationConstants.APPLICATION_PARAMETER_DB_URL.length());
                applicationConfig.put(ApplicationConstants.CONFIG_CONNECTION_URL, dbUrl);

            // parsing db user
            } else if (value.startsWith(ApplicationConstants.APPLICATION_PARAMETER_DB_USER)
                    && !applicationConfig.containsKey(ApplicationConstants.CONFIG_CONNECTION_USERNAME)) { // ignore extra parameters
                String dbUser = value.substring(ApplicationConstants.APPLICATION_PARAMETER_DB_USER.length());
                applicationConfig.put(ApplicationConstants.CONFIG_CONNECTION_USERNAME, dbUser);

            // parsing db password
            } else if (value.startsWith(ApplicationConstants.APPLICATION_PARAMETER_DB_PASS)
                    && !applicationConfig.containsKey(ApplicationConstants.CONFIG_CONNECTION_PASSWORD)) { // ignore extra parameters
                String dbPassword = value.substring(ApplicationConstants.APPLICATION_PARAMETER_DB_PASS.length());
                applicationConfig.put(ApplicationConstants.CONFIG_CONNECTION_PASSWORD, dbPassword);

            // parsing max threads count
            } else if (value.startsWith(ApplicationConstants.APPLICATION_PARAMETER_MAX_PROC_THREADS)
                    && !applicationConfig.containsKey(ApplicationConstants.CONFIG_MAX_PROC_THREADS)) { // ignore extra parameters
                String threadsCount = value.substring(ApplicationConstants.APPLICATION_PARAMETER_MAX_PROC_THREADS.length());
                applicationConfig.put(ApplicationConstants.CONFIG_MAX_PROC_THREADS, threadsCount);

            // parsing monitoring period
            } else if (value.startsWith(ApplicationConstants.APPLICATION_PARAMETER_MONITORING_PERIOD)
                    && !applicationConfig.containsKey(ApplicationConstants.CONFIG_MONITORING_PERIOD)) { // ignore extra parameters
                String monitoringPeriod = value.substring(ApplicationConstants.APPLICATION_PARAMETER_MONITORING_PERIOD.length());
                validateLongValue(ApplicationConstants.APPLICATION_PARAMETER_MONITORING_PERIOD, monitoringPeriod);
                applicationConfig.put(ApplicationConstants.CONFIG_MONITORING_PERIOD, monitoringPeriod);

            // parsing path to config
            } else if (value.startsWith(ApplicationConstants.APPLICATION_PARAMETER_CONFIG)) {

                String config = value.substring(ApplicationConstants.APPLICATION_PARAMETER_CONFIG.length());
                validateFile(ApplicationConstants.APPLICATION_PARAMETER_CONFIG, config);

                applicationConfig = parseConfigurationFile(config);

                break;// because of we don't need to parse something else if we have config
            }
        }

        performLastValidationCheck(applicationConfig);

        return applicationConfig;
    }


    /**
     * Performs parsing the configuration file specified as application parameter
     *
     * @param config - full path to config
     *
     * @return {@link Map} filled with configuration values
     */
    private static Map<String, String> parseConfigurationFile(String config) {
        Map<String, String> applicationConfig = new HashMap<>();

        // read properties
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(config));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        for(String key : properties.stringPropertyNames()) {
            switch (key) {
                case ApplicationConstants.CONFIG_CONNECTION_URL:
                    applicationConfig.put(ApplicationConstants.CONFIG_CONNECTION_URL, properties.getProperty(key));
                    break;

                case ApplicationConstants.CONFIG_CONNECTION_USERNAME:
                    applicationConfig.put(ApplicationConstants.CONFIG_CONNECTION_USERNAME, properties.getProperty(key));
                    break;

                case ApplicationConstants.CONFIG_CONNECTION_PASSWORD:
                    applicationConfig.put(ApplicationConstants.CONFIG_CONNECTION_PASSWORD, properties.getProperty(key));
                    break;

                case ApplicationConstants.CONFIG_MAX_PROC_THREADS:
                    String numberOfThreads = properties.getProperty(key);
                    Integer threads = validateMaxProcCount(numberOfThreads);
                    applicationConfig.put(ApplicationConstants.CONFIG_MAX_PROC_THREADS, threads.toString());
                    break;

                case ApplicationConstants.CONFIG_MONITORING_PERIOD:
                    String monitoringPeriod = properties.getProperty(key);
                    validateLongValue(ApplicationConstants.APPLICATION_PARAMETER_MONITORING_PERIOD, monitoringPeriod);
                    applicationConfig.put(ApplicationConstants.CONFIG_MONITORING_PERIOD, monitoringPeriod);
                    break;

                case ApplicationConstants.CONFIG_XML_SRC_DIR:
                    String source = properties.getProperty(key);
                    validatePath(ApplicationConstants.APPLICATION_PARAMETER_SRC, source);
                    applicationConfig.put(ApplicationConstants.CONFIG_XML_SRC_DIR, source);
                    break;

                case ApplicationConstants.CONFIG_XML_DST_DIR:
                    String destination = properties.getProperty(key);
                    validatePath(ApplicationConstants.APPLICATION_PARAMETER_SRC, destination);
                    applicationConfig.put(ApplicationConstants.CONFIG_XML_DST_DIR, destination);
                    break;

                case ApplicationConstants.CONFIG_XML_FAILED_DIR:
                    String failed = properties.getProperty(key);
                    validatePath(ApplicationConstants.APPLICATION_PARAMETER_SRC, failed);
                    applicationConfig.put(ApplicationConstants.CONFIG_XML_FAILED_DIR, failed);
                    break;
                default:
                    break;
            }
        }

        return applicationConfig;
    }


    /**
     * Validates the long value
     *
     * @param parameter - parameter to check
     * @param value - value to check
     *
     * Generates the {@link IllegalArgumentException} when string value has wrong representation of long
     */
    private static void validateLongValue(String parameter, String value) {
        try {
            Long.parseLong(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Wrong long value is specified for parameter " + parameter);
        }
    }


    /**
     * Performs the validation that all required fields are in map
     *
     * @param applicationConfig
     */
    private static void performLastValidationCheck(Map<String, String> applicationConfig) {
        if (!applicationConfig.containsKey(ApplicationConstants.CONFIG_XML_SRC_DIR)) {
            throw new IllegalArgumentException("XML source dir is not specified!");
        } else if (!applicationConfig.containsKey(ApplicationConstants.CONFIG_XML_DST_DIR)) {
            throw new IllegalArgumentException("XML destination dir is not specified!");
        } else if (!applicationConfig.containsKey(ApplicationConstants.CONFIG_XML_FAILED_DIR)) {
            throw new IllegalArgumentException("XML dir for failed XML's is not specified!");
        } else if (!applicationConfig.containsKey(ApplicationConstants.CONFIG_CONNECTION_URL)) {
            throw new IllegalArgumentException("Database connection url is not specified!");
        } else if (!applicationConfig.containsKey(ApplicationConstants.CONFIG_CONNECTION_USERNAME)) {
            throw new IllegalArgumentException("DB username is not specified!");
        } else if (!applicationConfig.containsKey(ApplicationConstants.CONFIG_CONNECTION_PASSWORD)) {
            throw new IllegalArgumentException("DB password is not specified!");
        } else if (!applicationConfig.containsKey(ApplicationConstants.CONFIG_MONITORING_PERIOD)) {
            throw new IllegalArgumentException("Monitoring period is not specified!");
        }
    }


    /**
     * Performs the validation of max proc threads
     *
     * @param value - max proc value
     *
     * @return - same or corrected one if value breaks the acceptable range 1..8!
     */
    private static Integer validateMaxProcCount(String value) {

        Integer numberOfThreads = 4;

        try {
            numberOfThreads = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Wrong number of threads has been specified");
        }

        // validate range of threads
        // nobody will know that we change the number of threads. MUAHAHAHA!!!!
        numberOfThreads = numberOfThreads > 8 ? 8 : numberOfThreads;
        numberOfThreads = numberOfThreads < 1 ? 1 : numberOfThreads;

        return numberOfThreads;
    }


    /**
     * Validates is value not null and file exists
     *
     * @param parameterName - validation parameter
     * @param value - validation value
     *
     * Generates {@link IllegalAccessException} if value is invalid
     */
    private static void validateFile(String parameterName, String value) {
        if (value == null) {
            throw new IllegalArgumentException("Parameter value for " + parameterName + " has to be specified!");
        }

        if (!new File(value).exists()) {
            throw new IllegalArgumentException("Specified path " + value + " doesn't exist!");
        }
    }


    /**
     * Validates is value not null, path exists and specified value is directory
     *
     * @param parameterName - validation parameter
     * @param value - validation value
     *
     * Generates {@link IllegalAccessException} if value is invalid
     */
    private static void validatePath(String parameterName, String value) {
        validateFile(parameterName, value);

        if (!new File(value).isDirectory()) {
            throw new IllegalArgumentException("Specified value " + value + " is not directory!");
        }
    }


    public static String generateHelpFile() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("CONGRATULATION! You have just broken the application!\n\n");
        buffer.append("Please try specify application parameters like:\n");
        buffer.append("--src=/path/to/source/xml/directory --dst=/directory/where/place/processed --failed=/directory/where/place/failed --url=db_connenction_url --username=db_username --password=db_password --period=monitoring_period_in_seconds\n\n");
        buffer.append("Also you can specify path to config with:\n");
        buffer.append("--config=/path/to/config/file\n\n");
        buffer.append("REMEMBER! Config file has greater priority than program arguments!\n");
        buffer.append("If you mix config and program arguments - the config values will be used instead of arguments one\n\n");
        buffer.append("If you still have the issues try to contact me.");

        return buffer.toString();
    }
}
