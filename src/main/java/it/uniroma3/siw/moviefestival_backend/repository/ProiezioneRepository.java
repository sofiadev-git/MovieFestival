package it.uniroma3.siw.moviefestival_backend.repository;

import it.uniroma3.siw.moviefestival_backend.model.Film;
import it.uniroma3.siw.moviefestival_backend.model.Proiezione;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProiezioneRepository extends JpaRepository<Proiezione,Long> {

    //per il 2°caso d'uso: visualizzazione del dettaglio di un festival
    @EntityGraph(attributePaths = {"film", "sala"}) //carichiamo subito il film, così associamo a esso le varie proiezioni, e la sala per ogni proiezione (tutto per il 2°caso)
    /*entityGraph non è necessario ma risolve il problema delle N+1 Query, visto che quelle informazioni sono comunque richieste*/
    List<Proiezione> findByFestival_IdOrderByDataAscOraAsc(Long festivalId); //trova le proiezioni di un festival per data e ora, poi nel service le raggruppiamo per film

    //controlla se esistono proiezioni associate a quel festival e film
    Boolean existsByFestival_IdAndFilm_Id(Long festivalId, Long filmId);

    //elimina la proiezione associata a quel film e festival
    void deleteByFestival_IdAndFilm_Id(Long festivalId, Long filmId);

}
