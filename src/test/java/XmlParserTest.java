import org.jbestie.gradle.xmlparser.service.XmlEntryService;
import org.jbestie.gradle.xmlparser.vo.XmlEntry;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Timestamp;

/**
 * Created by bestie on 12.02.2017.
 */

public class XmlParserTest {

    private final static XmlEntryService xmlEntryService = new XmlEntryService();

    @Test
    public void testCreateAndDeleteRecords() {
        XmlEntry entry = new XmlEntry("test_file", "some_stuff", new Timestamp(System.currentTimeMillis()));

        xmlEntryService.createEntry(entry);
        Assert.assertNotNull("Id should not be null!", entry.getId());

        Long id = entry.getId();
        Assert.assertEquals("Objects must be equal!", entry, xmlEntryService.getEntry(id));


    }
}
