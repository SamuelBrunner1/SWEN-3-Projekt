package at.technikum.swen_brunner_wydra.search;

import at.technikum.swen_brunner_wydra.entity.Dokument;
import at.technikum.swen_brunner_wydra.repository.DokumentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;


import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.elasticsearch.client.elc.Queries.matchQuery;


@Service
public class SearchService {

    private final ElasticsearchOperations elastic;
    private final DokumentRepository dokumentRepository;

    public SearchService(ElasticsearchOperations elastic, DokumentRepository dokumentRepository) {
        this.elastic = elastic;
        this.dokumentRepository = dokumentRepository;
    }

    public List<SearchResultDTO> searchForUser(String q, Long userId) {
        String query = (q == null) ? "" : q.trim();
        if (query.isEmpty()) return List.of();

        // 1) Elasticsearch: finde passende Dokumente
        NativeQuery esQuery = NativeQuery.builder()
                .withQuery(qb -> qb
                        .match(m -> m
                                .field("text")
                                .query(query)
                        )
                )
                .withPageable(PageRequest.of(0, 20))
                .build();


        SearchHits<DocumentIndex> hits = elastic.search(esQuery, DocumentIndex.class);

        // docId -> snippet aus ES Text
        Map<Long, String> snippetByDocId = new LinkedHashMap<>();
        for (var hit : hits) {
            DocumentIndex src = hit.getContent();
            if (src == null || src.getDocumentId() == null) continue;
            snippetByDocId.put(src.getDocumentId(), makeSnippet(src.getText(), query));
        }

        if (snippetByDocId.isEmpty()) return List.of();

        // 2) DB: filter auf User-Besitz (Security!)
        List<Long> ids = new ArrayList<>(snippetByDocId.keySet());
        List<Dokument> owned = dokumentRepository.findAllByIdInAndUser_Id(ids, userId);

        // 3) Ergebnis in Treffer-Reihenfolge zurückgeben
        Map<Long, Dokument> byId = owned.stream()
                .collect(Collectors.toMap(Dokument::getId, d -> d));

        List<SearchResultDTO> result = new ArrayList<>();
        for (Long id : ids) {
            Dokument d = byId.get(id);
            if (d == null) continue; // Treffer war nicht vom User
            result.add(new SearchResultDTO(
                    d.getId(),
                    d.getTitel(),
                    snippetByDocId.getOrDefault(id, "")
            ));
        }
        return result;
    }

    private String makeSnippet(String text, String q) {
        if (text == null) return "";

        String lower = text.toLowerCase();
        String needle = q.toLowerCase();

        int idx = lower.indexOf(needle);
        if (idx < 0) {
            // fallback: erste 160 chars
            return text.length() <= 160 ? text : text.substring(0, 160) + "…";
        }

        int start = Math.max(0, idx - 60);
        int end = Math.min(text.length(), idx + needle.length() + 80);

        String snippet = text.substring(start, end).replaceAll("\\s+", " ").trim();
        if (start > 0) snippet = "… " + snippet;
        if (end < text.length()) snippet = snippet + " …";
        return snippet;
    }
}
