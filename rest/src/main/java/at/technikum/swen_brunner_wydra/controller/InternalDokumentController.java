package at.technikum.swen_brunner_wydra.controller;

import at.technikum.swen_brunner_wydra.service.DocumentService;
import at.technikum.swen_brunner_wydra.service.dto.SummaryDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/dokumente")
public class InternalDokumentController {

    private final DocumentService service;

    public InternalDokumentController(DocumentService service) {
        this.service = service;
    }

    @Operation(summary = "Worker: Summary setzen (nur via X-Internal-Secret)")
    @PutMapping("/{id}/summary")
    public ResponseEntity<Void> updateSummaryInternal(@PathVariable Long id,
                                                      @RequestBody SummaryDTO dto) {
        service.updateSummaryInternal(id, dto.getSummary());
        return ResponseEntity.noContent().build();
    }
}
