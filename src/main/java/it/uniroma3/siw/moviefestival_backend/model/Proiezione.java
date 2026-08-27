package it.uniroma3.siw.moviefestival_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Proiezione {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDate data;

    private LocalTime ora;

    public enum StatoProiezione {  // al posto di final, definiamo le opzioni in enum
        SCHEDULED,
        CANCELLED,
        COMPLETED
    }

    @NotNull
    @Enumerated(EnumType.STRING) //salva il contenuto dell'enum per usarlo
    private StatoProiezione stato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "festival_id",nullable = false)
    private Festival festival;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "film_id",nullable = false)
    private Film film;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sala_id")
    private Sala sala;


    /* GETTER E SETTER*/
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getOra() {
        return ora;
    }

    public void setOra(LocalTime ora) {
        this.ora = ora;
    }

    public StatoProiezione getStato() {
        return stato;
    }

    public void setStato(StatoProiezione stato) {
        this.stato = stato;
    }

    public Festival getFestival() {
        return festival;
    }

    public void setFestival(Festival festival) {
        this.festival = festival;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }
}
