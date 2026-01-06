package at.technikum.swen_brunner_wydra.entity;

import at.technikum.swen_brunner_wydra.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "dokumente")
public class Dokument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titel;

    @Column(columnDefinition = "TEXT")
    private String inhalt;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "dateiname")
    private String dateiname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitel() { return titel; }
    public void setTitel(String titel) { this.titel = titel; }

    public String getInhalt() { return inhalt; }
    public void setInhalt(String inhalt) { this.inhalt = inhalt; }


    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }


    /**
     * Der eindeutige Dateiname (Objekt-Key) im MinIO-Bucket,
     * z. B. "2025/11/02/1a2b3c4d.pdf".
     */
    public String getDateiname() {
        return dateiname;
    }

    public void setDateiname(String dateiname) {
        this.dateiname = dateiname;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
