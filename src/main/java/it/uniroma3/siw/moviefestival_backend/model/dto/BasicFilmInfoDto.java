package it.uniroma3.siw.moviefestival_backend.model.dto;

import java.util.List;

/*cosa viene caricato quando si apre la pagina di un festival è dato da BasicFilmInfoDto, FestivalDetailsDto e ProiezionifilmDto*/
public record BasicFilmInfoDto(
        Long id,
        String titolo,
        String genere,
        String locandina,
        List<ProiezionifilmDto> proiezioni
) {
}
