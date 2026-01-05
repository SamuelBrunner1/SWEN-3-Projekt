package at.technikum.ocrworker.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ElasticSearchIndexService {

    private final ElasticsearchClient elasticsearchClient;

    public ElasticSearchIndexService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public void indexDocument(Long documentId, String filename, String ocrText) {
        try {
            elasticsearchClient.index(i -> i
                    .index("documents")
                    .id(documentId.toString())
                    .document(Map.of(
                            "documentId", documentId,
                            "filename", filename,
                            "content", ocrText
                    ))
            );
        } catch (Exception e) {
            System.err.println("ElasticSearch indexing failed");
            e.printStackTrace();
        }

    }
}
