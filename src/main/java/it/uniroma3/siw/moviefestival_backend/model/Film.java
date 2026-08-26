package it.uniroma3.siw.moviefestival_backend.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String titolo;
    private Integer anno;
    private Integer durata;
    private String genere;
    private String paeseProduzione;
    private String locandina; //da vedere, da usare poi MultiPartFile per l'upload

    public Set<Festival> getFestival() {
        return festival;
    }

    public void setFestival(Set<Festival> festival) {
        this.festival = festival;
    }

    @ManyToMany(mappedBy = "filmPartecipanti", fetch = FetchType.LAZY) //per ora tutti lazy
    private Set<Festival> festival = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "regista_id",nullable = false) // non posso salvare un regista in un unica colonna, prendo la chiave
    private Regista regista;

    @OneToMany(mappedBy = "film")
    private Set<Proiezione> proiezioni = new HashSet<>();

    @OneToMany(mappedBy = "film")
    private Set<Recensione> recensioni = new HashSet<>();

    /* GETTER E SETTER*/
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public Integer getAnno() {
        return anno;
    }

    public void setAnno(Integer anno) {
        this.anno = anno;
    }

    public Integer getDurata() {
        return durata;
    }

    public void setDurata(Integer durata) {
        this.durata = durata;
    }

    public String getGenere() {
        return genere;
    }

    public void setGenere(String genere) {
        this.genere = genere;
    }

    public String getPaeseProduzione() {
        return paeseProduzione;
    }

    public void setPaeseProduzione(String paeseProduzione) {
        this.paeseProduzione = paeseProduzione;
    }

    public Regista getRegista() {
        return regista;
    }

    public void setRegista(Regista regista) {
        this.regista = regista;
    }

    public Set<Proiezione> getProiezioni() {
        return proiezioni;
    }

    public void setProiezioni(Set<Proiezione> proiezioni) {
        this.proiezioni = proiezioni;
    }

    public Set<Recensione> getRecensioni() {
        return recensioni;
    }

    public void setRecensioni(Set<Recensione> recensioni) {
        this.recensioni = recensioni;
    }

    public String getLocandina() {
        return locandina;
    }

    public void setLocandina(String locandina) {
        this.locandina = locandina;
    }
}
