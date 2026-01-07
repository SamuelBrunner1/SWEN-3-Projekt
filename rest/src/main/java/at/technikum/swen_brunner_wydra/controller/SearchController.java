package at.technikum.swen_brunner_wydra.controller;

import at.technikum.swen_brunner_wydra.search.SearchResultDTO;
import at.technikum.swen_brunner_wydra.search.SearchService;
import at.technikum.swen_brunner_wydra.security.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public List<SearchResultDTO> search(@RequestParam("q") String q, HttpServletRequest request) {
        Long userId = SecurityUtil.requireUserId(request);
        return searchService.searchForUser(q, userId);
    }
}
