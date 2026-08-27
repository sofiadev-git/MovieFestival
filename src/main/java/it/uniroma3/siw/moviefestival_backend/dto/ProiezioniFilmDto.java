package it.uniroma3.siw.moviefestival_backend.dto;

import it.uniroma3.siw.moviefestival_backend.model.Proiezione;

import java.time.LocalDate;
import java.time.LocalTime;

public record ProiezioniFilmDto(
        Long id,
        LocalDate data,
        LocalTime ora,
        Proiezione.StatoProiezione stato,
        String nomeSala
) {
}
