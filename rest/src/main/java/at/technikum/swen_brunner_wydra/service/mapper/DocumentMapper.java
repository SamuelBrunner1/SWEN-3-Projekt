package at.technikum.swen_brunner_wydra.service.mapper;

import at.technikum.swen_brunner_wydra.entity.Dokument;
import at.technikum.swen_brunner_wydra.service.dto.DocumentDTO;

public class DocumentMapper {

    public static DocumentDTO toDto(Dokument entity) {
        if (entity == null) return null;

        DocumentDTO dto = new DocumentDTO(
                entity.getId(),
                entity.getTitel(),
                entity.getInhalt(),
                entity.getDateiname()
        );
        dto.setSummary(entity.getSummary());   // NEU

        return dto;
    }

    public static Dokument toEntity(DocumentDTO dto) {
        if (dto == null) return null;

        Dokument entity = new Dokument();
        entity.setId(dto.getId());
        entity.setTitel(dto.getTitel());
        entity.setInhalt(dto.getInhalt());
        entity.setDateiname(dto.getDateiname());
        entity.setSummary(dto.getSummary());
        return entity;
    }
}
