package de.EinWildesCJay;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Vertrag {

    private String id;
    private String titel;
    private String datum;
    private UUID ersteller;
    private List<UUID> vertragspartner = new ArrayList<>();
    private List<UUID> signiert = new ArrayList<>();
    private List<String> content;

    public Vertrag(String id, String titel, String datum, UUID ersteller, List<String> content) {
        this.id = id;
        this.titel = titel;
        this.datum = datum;
        this.ersteller = ersteller;
        this.content = content;
    }

    public String getId() {
        return this.id;
    }

    public String getDatum() {
        return this.datum;
    }

    public String getTitel() {
        return this.titel;
    }

    public UUID getErsteller() {
        return ersteller;
    }

    public List<UUID> getVertragspartner() {
        return vertragspartner;
    }

    public void addVertragspartner(UUID uuid) {
        this.vertragspartner.add(uuid);
    }

    public void removeVertragspartner(UUID uuid) {
        this.vertragspartner.remove(uuid);
    }

    public Boolean isSigniert(UUID uuid) {
        return this.signiert.contains(uuid);
    }

    public List<String> getContent() {
        return this.content;
    }

    public void setSigniert(UUID uuid) {
        this.signiert.add(uuid);
    }

    public void removeSignatur(UUID uuid) {
        this.signiert.remove(uuid);
    }

    public void setContent(List<String> list) {
        this.content = list;
    }

}
