package at.technikum.swen_brunner_wydra.service;

import at.technikum.swen_brunner_wydra.config.RabbitConfig;
import at.technikum.swen_brunner_wydra.entity.Dokument;
import at.technikum.swen_brunner_wydra.entity.User;
import at.technikum.swen_brunner_wydra.exception.DocumentNotFoundException;
import at.technikum.swen_brunner_wydra.messaging.OcrRequestMessage;
import at.technikum.swen_brunner_wydra.repository.DokumentRepository;
import at.technikum.swen_brunner_wydra.repository.UserRepository;
import at.technikum.swen_brunner_wydra.service.dto.DocumentDTO;
import at.technikum.swen_brunner_wydra.service.mapper.DocumentMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class DocumentService {

    private final DokumentRepository dokumentRepository;
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;

    public DocumentService(DokumentRepository dokumentRepository,
                           UserRepository userRepository,
                           RabbitTemplate rabbitTemplate) {
        this.dokumentRepository = dokumentRepository;
        this.userRepository = userRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<DocumentDTO> getAllForUser(Long userId) {
        return dokumentRepository.findAllByUser_Id(userId)
                .stream()
                .map(DocumentMapper::toDto)
                .toList();
    }

    public DocumentDTO getByIdForUser(Long docId, Long userId) {
        Dokument dokument = dokumentRepository.findByIdAndUser_Id(docId, userId)
                .orElseThrow(() -> new DocumentNotFoundException(docId));
        return DocumentMapper.toDto(dokument);
    }

    @Transactional
    public void deleteForUser(Long docId, Long userId) {
        if (!dokumentRepository.existsByIdAndUser_Id(docId, userId)) {
            throw new DocumentNotFoundException(docId);
        }
        dokumentRepository.deleteByIdAndUser_Id(docId, userId);
    }

    public void updateSummaryForUser(Long docId, String summary, Long userId) {
        Dokument dokument = dokumentRepository.findByIdAndUser_Id(docId, userId)
                .orElseThrow(() -> new DocumentNotFoundException(docId));
        dokument.setSummary(summary);
        dokumentRepository.save(dokument);
    }

    public void updateSummaryInternal(Long docId, String summary) {
        Dokument dokument = dokumentRepository.findById(docId)
                .orElseThrow(() -> new DocumentNotFoundException(docId));
        dokument.setSummary(summary);
        dokumentRepository.save(dokument);
    }
}
