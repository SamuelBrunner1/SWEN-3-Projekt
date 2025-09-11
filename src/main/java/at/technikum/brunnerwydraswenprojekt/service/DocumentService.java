package at.technikum.brunnerwydraswenprojekt.service;

import at.technikum.brunnerwydraswenprojekt.entity.Dokument;
import at.technikum.brunnerwydraswenprojekt.repository.DokumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    private final DokumentRepository repository;

    public DocumentService(DokumentRepository repository) {
        this.repository = repository;
    }

    public List<Dokument> getAll() {
        return repository.findAll();
    }

    public Optional<Dokument> getById(Long id) {
        return repository.findById(id);
    }

    public Dokument save(Dokument dokument) {
        return repository.save(dokument);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
