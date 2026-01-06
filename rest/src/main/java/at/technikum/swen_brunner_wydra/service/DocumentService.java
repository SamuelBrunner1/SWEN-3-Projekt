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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    private final DokumentRepository dokumentRepository;
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;

    public DocumentService(
            DokumentRepository dokumentRepository,
            UserRepository userRepository,
            RabbitTemplate rabbitTemplate
    ) {
        this.dokumentRepository = dokumentRepository;
        this.userRepository = userRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<DocumentDTO> getAllForUser(Long userId) {
        return dokumentRepository.findAllByUserId(userId)
                .stream()
                .map(DocumentMapper::toDto)
                .toList();
    }

    public DocumentDTO getByIdForUser(Long docId, Long userId) {
        Dokument dokument = dokumentRepository.findByIdAndUserId(docId, userId)
                .orElseThrow(() -> new DocumentNotFoundException(docId));
        return DocumentMapper.toDto(dokument);
    }

    public DocumentDTO saveForUser(DocumentDTO dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Dokument dokument = DocumentMapper.toEntity(dto);
        dokument.setUser(user);

        Dokument saved = dokumentRepository.save(dokument);

        rabbitTemplate.convertAndSend(
                RabbitConfig.QUEUE_NAME,
                new OcrRequestMessage(saved.getId(), saved.getDateiname())
        );

        return DocumentMapper.toDto(saved);
    }

    public void deleteForUser(Long docId, Long userId) {
        long deleted = dokumentRepository.deleteByIdAndUserId(docId, userId);
        if (deleted == 0) {
            throw new DocumentNotFoundException(docId);
        }
    }

    public void updateSummaryForUser(Long docId, String summary, Long userId) {
        Dokument dokument = dokumentRepository.findByIdAndUserId(docId, userId)
                .orElseThrow(() -> new DocumentNotFoundException(docId));

        dokument.setSummary(summary);
        dokumentRepository.save(dokument);
    }
}
