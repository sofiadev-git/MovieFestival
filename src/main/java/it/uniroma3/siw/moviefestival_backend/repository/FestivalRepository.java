package it.uniroma3.siw.moviefestival_backend.repository;

import it.uniroma3.siw.moviefestival_backend.model.Festival;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // non serve specificarlo con Jpa tecnicamente
public interface FestivalRepository extends JpaRepository<Festival,Long> {


}
