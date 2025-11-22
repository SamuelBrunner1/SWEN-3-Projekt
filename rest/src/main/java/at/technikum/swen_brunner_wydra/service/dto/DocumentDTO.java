package at.technikum.swen_brunner_wydra.service.dto;

public class DocumentDTO {
    private Long id;
    private String titel;
    private String inhalt;
    private String dateiname;
    private String summary;


    // Getter, Setter, Konstruktoren
    public DocumentDTO() {}

    public DocumentDTO(Long id, String titel, String inhalt) {
        this.id = id;
        this.titel = titel;
        this.inhalt = inhalt;
    }

    // Optionaler Konstruktor für Sprint 4
    public DocumentDTO(Long id, String titel, String inhalt, String dateiname) {
        this.id = id;
        this.titel = titel;
        this.inhalt = inhalt;
        this.dateiname = dateiname;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitel() { return titel; }
    public void setTitel(String titel) { this.titel = titel; }

    public String getInhalt() { return inhalt; }
    public void setInhalt(String inhalt) { this.inhalt = inhalt; }

    public String getDateiname() { return dateiname; }
    public void setDateiname(String dateiname) { this.dateiname = dateiname; }

    public String getSummary() {      // NEU
        return summary;
    }

    public void setSummary(String summary) {   // NEU
        this.summary = summary;
    }
}
