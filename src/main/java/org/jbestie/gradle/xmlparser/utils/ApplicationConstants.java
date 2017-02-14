package org.jbestie.gradle.xmlparser.utils;

/**
 * Created by bestie on 13.02.2017.
 */
public final class ApplicationConstants {
    private ApplicationConstants() {}

    public final static String APPLICATION_PARAMETER_SRC = "--src=";
    public final static String APPLICATION_PARAMETER_DST = "--dst=";
    public final static String APPLICATION_PARAMETER_FAILED = "--failed=";
    public final static String APPLICATION_PARAMETER_DB_URL = "--url=";
    public final static String APPLICATION_PARAMETER_DB_USER = "--username=";
    public final static String APPLICATION_PARAMETER_DB_PASS = "--password=";
    public final static String APPLICATION_PARAMETER_MAX_PROC_THREADS = "--threads=";
    public final static String APPLICATION_PARAMETER_CONFIG = "--config=";
    public final static String APPLICATION_PARAMETER_MONITORING_PERIOD = "--period=";


    public final static String CONFIG_CONNECTION_URL = "connection.url";
    public final static String CONFIG_CONNECTION_USERNAME = "connection.username";
    public final static String CONFIG_CONNECTION_PASSWORD = "connection.password";

    public final static String CONFIG_XML_SRC_DIR = "config.src";
    public final static String CONFIG_XML_DST_DIR = "config.dst";
    public final static String CONFIG_XML_FAILED_DIR = "config.failed";

    public final static String CONFIG_MAX_PROC_THREADS = "config.threads";

    public final static String CONFIG_MONITORING_PERIOD = "config.period";
}
