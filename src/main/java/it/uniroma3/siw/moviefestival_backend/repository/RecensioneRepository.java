package it.uniroma3.siw.moviefestival_backend.repository;

import it.uniroma3.siw.moviefestival_backend.model.Recensione;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecensioneRepository extends JpaRepository<Recensione, Long> {

    Page<Recensione> findByFilm_Id(Long filmId, Pageable pageable);

    List<Recensione> findByFilm_Id(Long filmId);

    boolean existsByFilm_IdAndAutore_Id(Long filmId, Long autoreId);
}
