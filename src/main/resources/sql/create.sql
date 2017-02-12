CREATE TABLE xml_entry (
    id BIGINT PRIMARY KEY,
    file_name VARCHAR(255),
    xml_content VARCHAR(1024),
    creation_date timestamp default current_timestamp
);
CREATE SEQUENCE xml_entry_id START 1;