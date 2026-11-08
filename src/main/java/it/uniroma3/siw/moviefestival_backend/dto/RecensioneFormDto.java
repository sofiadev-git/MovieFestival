package it.uniroma3.siw.moviefestival_backend.dto;

import java.time.LocalDate;

public record RecensioneFormDto(
        String testo,
        Integer voto,
        LocalDate data
) {
}
