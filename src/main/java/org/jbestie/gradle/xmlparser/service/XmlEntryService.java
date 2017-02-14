package org.jbestie.gradle.xmlparser.service;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.jbestie.gradle.xmlparser.utils.HibernateUtils;
import org.jbestie.gradle.xmlparser.vo.XmlEntry;

/**
 * Created by bestie on 12.02.2017.
 */

public class XmlEntryService {

    final Logger logger = Logger.getLogger(XmlEntryService.class);

    public Long createEntry(XmlEntry entry) {
        Session session = HibernateUtils.getSessionFactory().openSession();

        logger.debug("Attempt to store entry " + entry);
        try {
            session.getTransaction().begin();
            session.persist(entry);
            session.getTransaction().commit();
        } catch (RuntimeException ex) {
            session.getTransaction().rollback();
            logger.warn(ex.getMessage());
            throw ex;
        }
        finally {
            session.close();
        }

        return entry.getId();
    }


    public XmlEntry getEntry(Long id) {
        XmlEntry result = null;

        Session session = HibernateUtils.getSessionFactory().openSession();
        logger.debug("Attempt to get entry with id = " + id);

        try {
            result = session.get(XmlEntry.class, id);
        } catch (RuntimeException ex) {
            logger.warn(ex.getMessage());
            throw ex;
        }
        finally {
            session.close();
        }

        return result;
    }
}
