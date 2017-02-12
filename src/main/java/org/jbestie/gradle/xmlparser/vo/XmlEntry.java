package org.jbestie.gradle.xmlparser.vo;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by bestie on 12.02.2017.
 */
@Entity
@Table(name = "xml_entry")
public class XmlEntry {
    @Id
    @SequenceGenerator(name="xml_entry_id_gen",sequenceName="xml_entry_id", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="xml_entry_id_gen")
    @Column(name="id", unique=true, nullable=false)
    Long id;

    @Column(name="file_name", unique=true, nullable=false)
    String fileName;

    @Column(name="xml_content", length = 1024)
    String content;

    @Column(name="file_name", nullable=false)
    Timestamp creationDate;

    public XmlEntry() {
    }

    public XmlEntry(String fileName, String content, Timestamp creationDate) {
        this.fileName = fileName;
        this.content = content;
        this.creationDate = creationDate;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        XmlEntry xmlEntry = (XmlEntry) o;

        if (!id.equals(xmlEntry.id)) return false;
        if (!fileName.equals(xmlEntry.fileName)) return false;
        if (content != null ? !content.equals(xmlEntry.content) : xmlEntry.content != null) return false;
        return creationDate.equals(xmlEntry.creationDate);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + fileName.hashCode();
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + creationDate.hashCode();
        return result;
    }


    @Override
    public String toString() {
        return "XmlEntry{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", content='" + content + '\'' +
                ", creationDate=" + creationDate +
                '}';
    }
}
