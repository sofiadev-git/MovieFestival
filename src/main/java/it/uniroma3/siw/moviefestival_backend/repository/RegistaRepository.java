package it.uniroma3.siw.moviefestival_backend.repository;

import it.uniroma3.siw.moviefestival_backend.model.Regista;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistaRepository extends JpaRepository<Regista,Long> {
}
