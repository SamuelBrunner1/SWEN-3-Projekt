package at.technikum.brunnerwydraswenprojekt.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dokumente")
public class Dokument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titel;
    private String inhalt;

    // Konstruktoren
    public Dokument() {}
    public Dokument(String titel, String inhalt) {
        this.titel = titel;
        this.inhalt = inhalt;
    }

    // Getter & Setter
    public Long getId() { return id; }
    public String getTitel() { return titel; }
    public void setTitel(String titel) { this.titel = titel; }
    public String getInhalt() { return inhalt; }
    public void setInhalt(String inhalt) { this.inhalt = inhalt; }
}
