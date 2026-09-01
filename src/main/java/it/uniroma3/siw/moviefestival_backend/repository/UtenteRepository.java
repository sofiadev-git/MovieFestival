package it.uniroma3.siw.moviefestival_backend.repository;

import it.uniroma3.siw.moviefestival_backend.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente,Long> {

    //per utente specifico, uso optional per ricevere l'errore se non esiste
    Optional<Utente> findUtenteByUsername(String username);

    //per il controllo alla registrazione
    Boolean existsUtenteByUsername(String username);
}
