package it.uniroma3.siw.moviefestival_backend.dto;

import java.util.List;

/*cosa viene caricato quando si apre la pagina di un festival è dato da BasicFilmInfoDto, FestivalDetailsDto e ProiezioniFilmDto*/
public record BasicFilmInfoDto(
        Long id,
        String titolo,
        String genere,
        String locandina,
        List<ProiezioniFilmDto> proiezioni
) {
}
