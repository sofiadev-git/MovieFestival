package it.uniroma3.siw.moviefestival_backend.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nome;
    private String indirizzo;
    private int capienza;

    @OneToMany(mappedBy = "sala")
    private Set<Proiezione> proiezioni = new HashSet<>();

    /* GETTER E SETTER */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public int getCapienza() {
        return capienza;
    }

    public void setCapienza(int capienza) {
        this.capienza = capienza;
    }

    public Set<Proiezione> getProiezioni() {
        return proiezioni;
    }

    public void setProiezioni(Set<Proiezione> proiezioni) {
        this.proiezioni = proiezioni;
    }
}
