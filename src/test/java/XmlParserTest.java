import org.jbestie.gradle.xmlparser.service.XmlEntryService;
import org.jbestie.gradle.xmlparser.utils.HibernateUtils;
import org.jbestie.gradle.xmlparser.utils.ValidationUtils;
import org.jbestie.gradle.xmlparser.vo.XmlEntry;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;

/**
 * Some parser tests
 */

public class XmlParserTest {

    private final static XmlEntryService xmlEntryService = new XmlEntryService();
    static {
        HibernateUtils.initializeSessionFactoryConfig(new HashMap<>());
    }

    @Test
    public void testCreateAndDeleteRecords() {
        XmlEntry entry = new XmlEntry("test_file", "some_stuff", new Timestamp(System.currentTimeMillis()));

        xmlEntryService.createEntry(entry);
        Assert.assertNotNull("Id should not be null!", entry.getId());

        Long id = entry.getId();
        Assert.assertEquals("Objects must be equal!", entry, xmlEntryService.getEntry(id));
    }


    @Test
    public void testXmlValidation() throws SAXException, IOException, ParserConfigurationException {
        String xml = "<Entry><content>zzz</content><creationDate>2014-01-01 00:00:00</creationDate></Entry>";
        Validator validator = ValidationUtils.createValidator();

        Document document;
        // read XML file
        DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        document = parser.parse(new ByteArrayInputStream(xml.getBytes()));

        try {
            validator.validate(new DOMSource(document));
        } catch (IllegalArgumentException ex) {
            Assert.fail("XML should be successfully validated");
        }
    }


    @Test
    public void testWrongXmlValidation() throws IOException, ParserConfigurationException {
        String xml = "<Entry><creationDate>2014-01-01 00:00:00</creationDate><content>zzz</content></Entry>";
        Validator validator = ValidationUtils.createValidator();

        DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        try {
            Document document = parser.parse(new ByteArrayInputStream(xml.getBytes()));
            validator.validate(new DOMSource(document));
            Assert.fail("Wrong XML should fail the validation");
        } catch (SAXException ex) {
            // ok, we got required exception
        }
    }
}
