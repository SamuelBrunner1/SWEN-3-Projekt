package at.technikum.swen_brunner_wydra.exception;

public class DocumentNotFoundException extends RuntimeException {
    public DocumentNotFoundException(Long id) {
        super("Dokument mit ID " + id + " wurde nicht gefunden.");
    }
}