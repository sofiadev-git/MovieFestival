package it.uniroma3.siw.moviefestival_backend.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "recensione_utente_film", columnNames = {"utente_id", "film_id"})})
public class Recensione {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(
            nullable = false,
            length = 4000
    )
    private String testo;

    @Column(nullable = false)
    private Integer voto;

    @Column(nullable = false)
    private LocalDate data;


    @ManyToOne(optional = false)
    @JoinColumn(name = "film_id", nullable = false)
    private Film film;


    @ManyToOne(optional = false)
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente autore;

    /* GETTER E SETTER*/


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public Integer getVoto() {
        return voto;
    }

    public void setVoto(Integer voto) {
        this.voto = voto;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public Utente getAutore() {
        return autore;
    }

    public void setAutore(Utente autore) {
        this.autore = autore;
    }
}