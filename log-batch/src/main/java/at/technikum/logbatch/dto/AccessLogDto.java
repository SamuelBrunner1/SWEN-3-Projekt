package at.technikum.logbatch.dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import java.util.List;

@XmlRootElement(name = "accessLog")
public class AccessLogDto {

    private String date;
    private List<AccessEntryDto> entries;

    @XmlAttribute(name = "date")
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @XmlElement(name = "entry")
    public List<AccessEntryDto> getEntries() {
        return entries;
    }

    public void setEntries(List<AccessEntryDto> entries) {
        this.entries = entries;
    }
}
