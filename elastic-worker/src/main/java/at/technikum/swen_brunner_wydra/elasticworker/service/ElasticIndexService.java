package at.technikum.swen_brunner_wydra.elasticworker.service;

import at.technikum.swen_brunner_wydra.elasticworker.elasticsearch.DocumentIndex;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ElasticIndexService {

    private final ElasticsearchOperations elastic;

    public ElasticIndexService(ElasticsearchOperations elastic) {
        this.elastic = elastic;
    }

    public void indexOcrText(Long documentId, String ocrText) {
        if (documentId == null) {
            throw new IllegalArgumentException("documentId must not be null");
        }
        if (ocrText == null) {
            ocrText = "";
        }

        DocumentIndex doc = new DocumentIndex(
                String.valueOf(documentId),
                documentId,
                ocrText,
                Instant.now()
        );

        elastic.save(doc);
    }
}
