package at.technikum.swen_brunner_wydra.messaging;

public record DocumentUploadedMessage(
        Long documentId,
        String bucket,
        String objectKey,
        String originalFilename,
        String contentType,
        String uploadedAtUtc
) {}
