package it.uniroma3.siw.moviefestival_backend.repository;

import it.uniroma3.siw.moviefestival_backend.model.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilmRepository extends JpaRepository<Film,Long> {

    //per il 2°caso d'uso: visualizzazione del dettaglio di un festival
    List<Film> findByFestival_idOrderByTitoloAsc(Long festivalId); // per trovare la lista di film di un festival e ordinarla alfabeticamente per titolo in ordine ascendente

}
