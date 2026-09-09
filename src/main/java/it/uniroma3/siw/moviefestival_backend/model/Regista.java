package it.uniroma3.siw.moviefestival_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Regista {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "il nome è obbligatorio")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "il cognome è obbligatorio")
    @Column(nullable = false)
    private String cognome;

    @NotNull(message = "la data di nascita è obbligatoria")
    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataNascita;

    @NotBlank(message = "la nazionalità è obbligatoria")
    @Column(nullable = false)
    private String nazionalita;

    @OneToMany(mappedBy = "regista")
    private Set<Film> filmDiretti= new HashSet<>();

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

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public LocalDate getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    public String getNazionalita() {
        return nazionalita;
    }

    public void setNazionalita(String nazionalita) {
        this.nazionalita = nazionalita;
    }

    public Set<Film> getFilmDiretti() {
        return filmDiretti;
    }

    public void setFilmDiretti(Set<Film> filmDiretti) {
        this.filmDiretti = filmDiretti;
    }
}
