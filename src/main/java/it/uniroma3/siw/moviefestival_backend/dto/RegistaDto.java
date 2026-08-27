package it.uniroma3.siw.moviefestival_backend.dto;

import java.time.LocalDate;
import java.util.List;

public record RegistaDto(
        Long id,
        String nome,
        String cognome,
        LocalDate dataNascita,
        String nazionalita,
        List<String> filmDiretti
) {
}
