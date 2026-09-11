package it.uniroma3.siw.moviefestival_backend.repository;

import it.uniroma3.siw.moviefestival_backend.model.Proiezione;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProiezioneRepository extends JpaRepository<Proiezione, Long> {

    // Trova tutte le proiezioni di un festival ordinate per data e ora
    @EntityGraph(attributePaths = {"film", "sala"})  //costituisce lazy con EntithyGRaph
    List<Proiezione> findByFestival_IdOrderByDataAscOraAsc(Long festivalId);

    List<Proiezione> findAllByFestival_IdOrderByDataAscOraAsc(Long festivalId); // per il test con lazy

    // Controlla se esistono proiezioni associate a quel festival e film
    Boolean existsByFestival_IdAndFilm_Id(Long festivalId, Long filmId);

    // Elimina le proiezioni associate a quel film e festival
    void deleteByFestival_IdAndFilm_Id(Long festivalId, Long filmId);

    // Recupera una proiezione caricando subito festival, film e sala
    @Override
    @EntityGraph(attributePaths = {"festival", "film", "sala"})
    Optional<Proiezione> findById(Long id);

    /*
     * Recupera le proiezioni di una sala comprese tra due date.
     * Il film viene caricato subito perché ci serve la sua durata
     * per controllare eventuali sovrapposizioni.
     */
    @EntityGraph(attributePaths = {"film"})
    List<Proiezione> findBySala_IdAndDataBetween(Long salaId,
                                                 LocalDate dataInizio,
                                                 LocalDate dataFine);
}