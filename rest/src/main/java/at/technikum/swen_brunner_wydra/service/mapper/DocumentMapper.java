package at.technikum.swen_brunner_wydra.service.mapper;

import at.technikum.swen_brunner_wydra.service.dto.DocumentDTO;
import at.technikum.swen_brunner_wydra.entity.Dokument;

public class DocumentMapper {
    public static DocumentDTO toDto(Dokument entity) {
        if (entity == null) return null;
        return new DocumentDTO(
                entity.getId(),
                entity.getTitel(),
                entity.getInhalt(),
                entity.getDateiname()
        );
    }

    public static Dokument toEntity(DocumentDTO dto) {
        if (dto == null) return null;
        Dokument entity = new Dokument();
        entity.setId(dto.getId());
        entity.setTitel(dto.getTitel());
        entity.setInhalt(dto.getInhalt());
        entity.setDateiname(dto.getDateiname());
        return entity;
    }
}
