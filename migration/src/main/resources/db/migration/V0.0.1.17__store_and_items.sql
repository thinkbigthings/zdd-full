
set search_path TO public;

CREATE TABLE store (
    id              BIGSERIAL       NOT NULL    PRIMARY KEY,
    name            VARCHAR(255)    NOT NULL    UNIQUE,
    website         VARCHAR(255)    NOT NULL,
    updated         TIMESTAMPTZ     NOT NULL
);

CREATE INDEX store_name_index ON store (name);

CREATE TABLE subspecies (
    id   INT4           NOT NULL PRIMARY KEY,
    name VARCHAR(255)   NOT NULL
);

INSERT INTO subspecies (id, name) VALUES (0, 'SATIVA');
INSERT INTO subspecies (id, name) VALUES (1, 'SATIVA_HYBRID');
INSERT INTO subspecies (id, name) VALUES (2, 'HYBRID');
INSERT INTO subspecies (id, name) VALUES (3, 'INDICA_HYBRID');
INSERT INTO subspecies (id, name) VALUES (4, 'INDICA');
INSERT INTO subspecies (id, name) VALUES (5, 'HIGH_CBD');

CREATE TABLE terpene (
    id   INT4           NOT NULL PRIMARY KEY,
    name VARCHAR(255)   NOT NULL
);

INSERT INTO terpene (id, name) VALUES (0, 'BISABOLOL');
INSERT INTO terpene (id, name) VALUES (1, 'CARYOPHYLLENE');
INSERT INTO terpene (id, name) VALUES (2, 'HUMULENE');
INSERT INTO terpene (id, name) VALUES (3, 'LIMONENE');
INSERT INTO terpene (id, name) VALUES (4, 'LINALOOL');
INSERT INTO terpene (id, name) VALUES (5, 'MYRCENE');
INSERT INTO terpene (id, name) VALUES (6, 'PINENE');
INSERT INTO terpene (id, name) VALUES (7, 'TERPINOLENE');


-- NUMERIC(precision, scale) the number 23.5141 has a precision of 6 and a scale of 4
CREATE TABLE store_item (
    id              BIGSERIAL       NOT NULL    PRIMARY KEY,
    subspecies_id   INT4            NOT NULL    REFERENCES subspecies (id),
    strain          VARCHAR(255)    NOT NULL,
    thc_percent     NUMERIC(5, 3)   NOT NULL,
    cbd_percent     NUMERIC(5, 3)   NOT NULL,
    weight_grams    NUMERIC(3, 1)   NOT NULL,
    price_dollars   INT8            NOT NULL,
    vendor          VARCHAR(255)    NOT NULL,
    store_item_id   INT8            NOT NULL REFERENCES store (id)
);

CREATE TABLE terpene_amount (
    id              BIGSERIAL   NOT NULL    PRIMARY KEY,
    store_item_id   INT8        NOT NULL    REFERENCES store_item (id),
    terpene_id      INT4        NOT NULL    REFERENCES terpene (id),
    terpene_percent NUMERIC(5, 3)   NOT NULL
);

CREATE INDEX index_terpene_amount_terpene ON terpene_amount(terpene_id);
