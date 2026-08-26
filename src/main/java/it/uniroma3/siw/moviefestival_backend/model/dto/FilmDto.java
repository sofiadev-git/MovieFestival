package it.uniroma3.siw.moviefestival_backend.model.dto;

import java.util.List;

/* questo dto serve per caricare le informazioni nella pagina dettagli film*/
public record FilmDto(
        Long id,
        String nome,
        String titolo,
        Integer anno,
        Integer durata,
        String genere,
        String paeseProduzione,
        String locandina,
        Long idRegista, // per poi accedere alla pagina dettagli artista
        String nomeRegista,
        String CognomeRegista
) {
}
