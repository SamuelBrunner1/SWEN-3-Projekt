package at.technikum.swen_brunner_wydra.controller;

import at.technikum.swen_brunner_wydra.service.DocumentService;
import at.technikum.swen_brunner_wydra.service.dto.DocumentDTO;
import at.technikum.swen_brunner_wydra.service.dto.SummaryDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dokumente")
public class DokumentController {

    private final DocumentService service;

    public DokumentController(DocumentService service) {
        this.service = service;
    }

    private Long requireUserId(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("Unauthorized");
        }
        return (Long) userId;
    }

    @Operation(summary = "Alle Dokumente des eingeloggten Users abrufen")
    @GetMapping
    public List<DocumentDTO> getAll(HttpServletRequest request) {
        return service.getAllForUser(requireUserId(request));
    }

    @Operation(summary = "Ein Dokument des Users nach ID abrufen")
    @GetMapping("/{id}")
    public DocumentDTO getById(@PathVariable Long id, HttpServletRequest request) {
        return service.getByIdForUser(id, requireUserId(request));
    }

    @Operation(summary = "Neues Dokument für den User anlegen")
    @PostMapping
    public DocumentDTO create(@RequestBody DocumentDTO dto, HttpServletRequest request) {
        return service.saveForUser(dto, requireUserId(request));
    }

    @Operation(summary = "Dokument des Users löschen")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        service.deleteForUser(id, requireUserId(request));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Summary für ein Dokument des Users aktualisieren")
    @PutMapping("/{id}/summary")
    public ResponseEntity<Void> updateSummary(
            @PathVariable Long id,
            @RequestBody SummaryDTO dto,
            HttpServletRequest request
    ) {
        service.updateSummaryForUser(id, dto.getSummary(), requireUserId(request));
        return ResponseEntity.noContent().build();
    }
}
