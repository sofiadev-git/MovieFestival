package it.uniroma3.siw.moviefestival_backend.model.dto;

import java.time.LocalDate;

public record RecensioniDto(
        Long id,
        String testo,
        Integer voto,
        LocalDate data,
        String autore
) {
}
