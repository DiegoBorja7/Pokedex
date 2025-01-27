package ec.edu.uce.pokedex.entities;

import jakarta.persistence.Embeddable;

@Embeddable
public class AbilityDetail {
    private String name;
    private String url;

    // Getters y setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
