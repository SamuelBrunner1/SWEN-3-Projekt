package at.technikum.swen_brunner_wydra.controller;

import at.technikum.swen_brunner_wydra.entity.Dokument;
import at.technikum.swen_brunner_wydra.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

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
    public List<Dokument> getAll() {
        return service.getAll();
    }

    @Operation(summary = "Ein Dokument nach ID abrufen")
    @GetMapping("/{id}")
    public Dokument getById(@PathVariable Long id) {
        return service.getById(id).orElseThrow();
    }

    @Operation(summary = "Neues Dokument anlegen")
    @PostMapping
    public Dokument create(@RequestBody Dokument dokument) {
        return service.save(dokument);
    }

    @Operation(summary = "Dokument löschen")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
