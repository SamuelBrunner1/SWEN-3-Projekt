package at.technikum.swen_brunner_wydra.controller;

import at.technikum.swen_brunner_wydra.service.DocumentService;
import at.technikum.swen_brunner_wydra.service.dto.DocumentDTO;
import at.technikum.swen_brunner_wydra.service.dto.SummaryDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/dokumente")
public class DokumentController {

    private final DocumentService service;

    public DokumentController(DocumentService service) {
        this.service = service;
    }

    private Long requireUserId(HttpServletRequest request) {
        Object v = request.getAttribute("userId");

        if (v instanceof Long l) return l;
        if (v instanceof Integer i) return i.longValue();
        if (v instanceof String s) {
            try { return Long.parseLong(s); } catch (NumberFormatException ignored) {}
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
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

    @Operation(summary = "Dokument löschen (nur eigenes)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        service.deleteForUser(id, requireUserId(request));
        return ResponseEntity.noContent().build();
    }

    /**
     * Optional: Wenn du willst, dass User manuell eine Summary setzen darf,
     * kannst du diesen Endpoint lassen.
     *
     * Aber: für den GenAI-Worker machen wir einen separaten "internal" Endpoint,
     * weil der keinen JWT Token hat.
     */
    @Operation(summary = "Summary manuell setzen (nur eigenes Dokument)")
    @PutMapping("/{id}/summary")
    public ResponseEntity<Void> updateSummaryForUser(
            @PathVariable Long id,
            @RequestBody SummaryDTO dto,
            HttpServletRequest request
    ) {
        service.updateSummaryForUser(id, dto.getSummary(), requireUserId(request));
        return ResponseEntity.noContent().build();
    }
}
