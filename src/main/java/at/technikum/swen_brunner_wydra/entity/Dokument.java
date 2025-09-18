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

    // Konstruktoren überflüssig weil in DTO
    // public Dokument() {}
    //public Dokument(String titel, String inhalt) {
    //    this.titel = titel;
    //    this.inhalt = inhalt;
    //}

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitel() { return titel; }
    public void setTitel(String titel) { this.titel = titel; }

    public String getInhalt() { return inhalt; }
    public void setInhalt(String inhalt) { this.inhalt = inhalt; }
}

