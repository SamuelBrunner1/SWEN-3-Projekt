package at.technikum.swen_brunner_wydra.service;

import at.technikum.swen_brunner_wydra.entity.Dokument;
import at.technikum.swen_brunner_wydra.repository.DokumentRepository;
import at.technikum.swen_brunner_wydra.service.dto.DocumentDTO;
import at.technikum.swen_brunner_wydra.service.mapper.DocumentMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    private final DokumentRepository repository;

    public DocumentService(DokumentRepository repository) {
        this.repository = repository;
    }

    public List<DocumentDTO> getAll() {
        return repository.findAll().stream()
                .map(DocumentMapper::toDto)
                .toList();
    }

    public DocumentDTO getById(Long id) {
        return repository.findById(id)
                .map(DocumentMapper::toDto)
                .orElse(null);
    }

    public DocumentDTO save(DocumentDTO dto) {
        Dokument entity = DocumentMapper.toEntity(dto);
        Dokument saved = repository.save(entity);
        return DocumentMapper.toDto(saved);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
