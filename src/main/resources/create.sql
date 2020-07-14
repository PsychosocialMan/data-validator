CREATE TABLE record (
    primary_key         VARCHAR2(64) NOT NULL,
    name                VARCHAR2(256),
    description         VARCHAR2(1024),
    updated_timestamp   DATE
);

ALTER TABLE record ADD CONSTRAINT record_pk PRIMARY KEY ( primary_key );