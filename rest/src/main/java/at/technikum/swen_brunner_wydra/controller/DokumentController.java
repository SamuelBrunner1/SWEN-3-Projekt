package at.technikum.swen_brunner_wydra.controller;
import at.technikum.swen_brunner_wydra.entity.Dokument;
import at.technikum.swen_brunner_wydra.service.DocumentService;
import at.technikum.swen_brunner_wydra.service.dto.DocumentDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import at.technikum.swen_brunner_wydra.service.dto.SummaryDTO;   // NEU
import org.springframework.http.ResponseEntity;                    // NEU



import java.util.List;

@RestController
@RequestMapping("/api/dokumente")
public class DokumentController {

    private final DocumentService service;

    public DokumentController(DocumentService service) {
        this.service = service;
    }

    @Operation(summary = "Alle Dokumente abrufen")
    @GetMapping
    public List<DocumentDTO> getAll() {
        return service.getAll();
    }

    @Operation(summary = "Ein Dokument nach ID abrufen")
    @GetMapping("/{id}")
    public DocumentDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(summary = "Neues Dokument anlegen")
    @PostMapping
    public DocumentDTO create(@RequestBody DocumentDTO dto) {
        return service.save(dto);
    }

    @Operation(summary = "Dokument löschen")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

// NEU: Summary vom GenAI-Worker setzen
@Operation(summary = "Summary für ein Dokument aktualisieren")
@PutMapping("/{id}/summary")
public ResponseEntity<Void> updateSummary(@PathVariable Long id,
                                          @RequestBody SummaryDTO dto) {
    service.updateSummary(id, dto.getSummary());
    return ResponseEntity.noContent().build();
}
}