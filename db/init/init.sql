CREATE TABLE IF NOT EXISTS document_access_stats (
                                                     id BIGSERIAL PRIMARY KEY,
                                                     document_id BIGINT NOT NULL,
                                                     access_date DATE NOT NULL,
                                                     access_count INT NOT NULL,
                                                     CONSTRAINT uq_document_access UNIQUE (document_id, access_date)
    );
