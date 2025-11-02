package at.technikum.ocrworker.messaging;

public record DocumentUploadedMessage(
        Long documentId,
        String bucket,
        String objectKey,
        String originalFilename,
        String contentType,
        String uploadedAtUtc
) {}
