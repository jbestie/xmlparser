package org.jbestie.gradle.xmlparser.utils;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.StringReader;

/**
 * Validation utils
 */
public class ValidationUtils {
    private ValidationUtils() {}

    // dumb compressed xsd schema because of some issues with xsd-file in jar :(
    private static String XSD_SCHEMA = "<xs:schema attributeFormDefault=\"unqualified\" elementFormDefault=\"qualified\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" +
            "<xs:element name=\"Entry\"><xs:complexType><xs:sequence><xs:element type=\"xs:string\" name=\"content\">" +
            "<xs:annotation><xs:documentation>String with length less or equal 1024 chars</xs:documentation></xs:annotation>" +
            "</xs:element><xs:element type=\"xs:string\" name=\"creationDate\"><xs:annotation><xs:documentation>Creation date of record</xs:documentation>\n" +
            "</xs:annotation></xs:element></xs:sequence></xs:complexType></xs:element></xs:schema>";

    /**
     * Creates the validator to validate incoming xml file.
     *
     * @return {@link Validator} for xml validation
     */
    public static Validator createValidator() {
        // create a SchemaFactory capable of understanding WXS schemas
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source schemaFile = new StreamSource(new StringReader(XSD_SCHEMA));

        Schema schema;
        try {
            schema = factory.newSchema(schemaFile);
        } catch (SAXException ex) {
            throw new RuntimeException(ex);
        }

        // create a Validator instance, which can be used to validate an instance document
        return schema.newValidator();
    }
}
