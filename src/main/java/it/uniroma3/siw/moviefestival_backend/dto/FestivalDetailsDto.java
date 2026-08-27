package it.uniroma3.siw.moviefestival_backend.dto;

import java.time.LocalDate;
import java.util.List;

public record FestivalDetailsDto(
        Long id,
        String nome,
        Integer anno,
        String citta,
        LocalDate dataInizio,//LocalDate rappresenta una data di calendario senza orario e senza fuso orario, permette di scrivere xx/xx/xxxx
        LocalDate dataFine,
        String descrizione,
        List<BasicFilmInfoDto> film
) {
}
