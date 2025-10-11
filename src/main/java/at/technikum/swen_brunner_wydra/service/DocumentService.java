package at.technikum.swen_brunner_wydra.service;

import at.technikum.swen_brunner_wydra.config.RabbitConfig;
import at.technikum.swen_brunner_wydra.entity.Dokument;
import at.technikum.swen_brunner_wydra.repository.DokumentRepository;
import at.technikum.swen_brunner_wydra.service.dto.DocumentDTO;
import at.technikum.swen_brunner_wydra.service.mapper.DocumentMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    private final DokumentRepository repository;

    @Autowired
    private RabbitTemplate rabbitTemplate; // <--- RabbitMQ Template

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

        // Nachricht an RabbitMQ senden:
        rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_NAME, saved.getId());

        return DocumentMapper.toDto(saved);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
