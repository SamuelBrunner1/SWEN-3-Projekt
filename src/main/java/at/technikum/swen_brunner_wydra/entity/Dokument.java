package at.technikum.swen_brunner_wydra.entity;

import jakarta.persistence.*;

@Entity //Tabelle
@Table(name = "dokumente") //Name
public class Dokument {

    @Id //PrimärKey
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titel; //spalte
    private String inhalt; //spalte

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

