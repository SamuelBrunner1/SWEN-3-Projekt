package at.technikum.brunnerwydraswenprojekt.controller;

import at.technikum.brunnerwydraswenprojekt.entity.Dokument;
import at.technikum.brunnerwydraswenprojekt.service.DocumentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dokumente")
public class DokumentController {

    private final DocumentService service;

    public DokumentController(DocumentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Dokument> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Dokument getById(@PathVariable Long id) {
        return service.getById(id).orElseThrow();
    }

    @PostMapping
    public Dokument create(@RequestBody Dokument dokument) {
        return service.save(dokument);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
