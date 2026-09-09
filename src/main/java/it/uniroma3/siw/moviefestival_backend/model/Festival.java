package it.uniroma3.siw.moviefestival_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Festival {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank
    private String nome;

    @NotNull
    @Min(1900)
    @Max(2030)
    private Integer anno;

    @NotBlank
    private String citta;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataInizio;

    @NotNull//LocalDate rappresenta una data di calendario senza orario e senza fuso orario, permette di scrivere xx/xx/xxxx
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataFine;

    @Column(
            nullable = false,
            length = 4000
    )
    private String descrizione;

    @ManyToMany(fetch = FetchType.LAZY) //per ora lazy
    @JoinTable(
            name = "film_nel_festival",
            joinColumns = @JoinColumn(name = "festival_id"),
            inverseJoinColumns = @JoinColumn(name = "film_id"),
            uniqueConstraints =  @UniqueConstraint( columnNames = {"festival_id","film_id"}) //la coppia film festival è unica, un festiva non può avere due volte lo stesso film (al massimo la proiezione)
    )
    private Set<Film> filmPartecipanti = new HashSet<>();

    @OneToMany(mappedBy = "festival")
    private Set<Proiezione> proiezioni = new HashSet<>();

    /* GETTER E SETTER*/
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

    public Integer getAnno() {
        return anno;
    }

    public void setAnno(Integer anno) {
        this.anno = anno;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDate dataInizio) {
        this.dataInizio = dataInizio;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDate dataFine) {
        this.dataFine = dataFine;
    }

    public Set<Film> getFilmPartecipanti() {
        return filmPartecipanti;
    }

    public void setFilmPartecipanti(Set<Film> filmPartecipanti) {
        this.filmPartecipanti = filmPartecipanti;
    }

    public Set<Proiezione> getProiezioni() {
        return proiezioni;
    }

    public void setProiezioni(Set<Proiezione> proiezioni) {
        this.proiezioni = proiezioni;
    }
}
