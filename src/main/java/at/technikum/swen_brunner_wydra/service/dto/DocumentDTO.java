package at.technikum.swen_brunner_wydra.service.dto;

public class DocumentDTO {
    private Long id;
    private String titel;
    private String inhalt;

    // Getter, Setter, Konstruktoren
    public DocumentDTO() {}

    public DocumentDTO(Long id, String titel, String inhalt) {
        this.id = id;
        this.titel = titel;
        this.inhalt = inhalt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitel() { return titel; }
    public void setTitel(String titel) { this.titel = titel; }

    public String getInhalt() { return inhalt; }
    public void setInhalt(String inhalt) { this.inhalt = inhalt; }
}
