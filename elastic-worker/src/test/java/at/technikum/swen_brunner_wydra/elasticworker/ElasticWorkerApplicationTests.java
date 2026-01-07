package at.technikum.swen_brunner_wydra.elasticworker;

import at.technikum.swen_brunner_wydra.elasticworker.elasticsearch.DocumentIndex;
import at.technikum.swen_brunner_wydra.elasticworker.service.ElasticIndexService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ElasticWorkerApplicationTests {

    @Autowired
    private ElasticIndexService elasticIndexService;


    @Test
    void contextLoads() {
        //  Test besteht, wenn Spring Context startet
    }

    @Test
    void elasticIndexService_isLoaded() {
        //  Beweis: Indexing-Service existiert
        assertThat(elasticIndexService).isNotNull();
    }

    @Test
    void documentIndex_canBeCreated() {
        DocumentIndex index = new DocumentIndex();
        assertThat(index).isNotNull();
    }

}
