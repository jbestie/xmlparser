package org.jbestie.gradle.xmlparser.utils;

/**
 * Constants used by application
 */
public final class ApplicationConstants {
    private ApplicationConstants() {}

    final static String APPLICATION_PARAMETER_SRC = "--src=";
    final static String APPLICATION_PARAMETER_DST = "--dst=";
    final static String APPLICATION_PARAMETER_FAILED = "--failed=";
    final static String APPLICATION_PARAMETER_DB_URL = "--url=";
    final static String APPLICATION_PARAMETER_DB_USER = "--username=";
    final static String APPLICATION_PARAMETER_DB_PASS = "--password=";
    final static String APPLICATION_PARAMETER_MAX_PROC_THREADS = "--threads=";
    final static String APPLICATION_PARAMETER_CONFIG = "--config=";
    final static String APPLICATION_PARAMETER_MONITORING_PERIOD = "--period=";


    final static String CONFIG_CONNECTION_URL = "connection.url";
    final static String CONFIG_CONNECTION_USERNAME = "connection.username";
    final static String CONFIG_CONNECTION_PASSWORD = "connection.password";

    public final static String CONFIG_XML_SRC_DIR = "config.src";
    public final static String CONFIG_XML_DST_DIR = "config.dst";
    public final static String CONFIG_XML_FAILED_DIR = "config.failed";

    public final static String CONFIG_MAX_PROC_THREADS = "config.threads";

    public final static String CONFIG_MONITORING_PERIOD = "config.period";
}
