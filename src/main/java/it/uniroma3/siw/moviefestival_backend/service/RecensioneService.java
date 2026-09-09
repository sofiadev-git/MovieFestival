package it.uniroma3.siw.moviefestival_backend.service;

import it.uniroma3.siw.moviefestival_backend.exception.NotFoundException;
import it.uniroma3.siw.moviefestival_backend.exception.NotValidException;
import it.uniroma3.siw.moviefestival_backend.model.Film;
import it.uniroma3.siw.moviefestival_backend.model.Recensione;
import it.uniroma3.siw.moviefestival_backend.model.Utente;
import it.uniroma3.siw.moviefestival_backend.repository.FilmRepository;
import it.uniroma3.siw.moviefestival_backend.repository.RecensioneRepository;
import it.uniroma3.siw.moviefestival_backend.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RecensioneService {

    private static final int RECENSIONI_PER_PAGINA = 5;

    @Autowired
    private RecensioneRepository recensioneRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Transactional(readOnly = true)
    public Page<Recensione> getRecensioniFilm(Long filmId, int page) {
        Pageable pageable = PageRequest.of(
                page,
                RECENSIONI_PER_PAGINA,
                Sort.by("data").descending().and(Sort.by("id").descending())
        );
        return recensioneRepository.findByFilm_Id(filmId, pageable);
    }

    @Transactional(readOnly = true)
    public Recensione getRecensione(Long id) {
        return recensioneRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("recensione non trovata"));
    }

    @Transactional(readOnly = true)
    public boolean hasAlreadyRecensione(Long filmId, Long utenteId) {
        return recensioneRepository.existsByFilm_IdAndAutore_Id(filmId, utenteId);
    }

    @Transactional(readOnly = true)
    public boolean isAutore(Long recensioneId, Long utenteId) {
        return getRecensione(recensioneId).getAutore().getId().equals(utenteId);
    }

    @Transactional
    public Recensione createRecensione(Long filmId, Long utenteId, Recensione recensione) {
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new NotFoundException("film non trovato"));

        Utente autore = utenteRepository.findById(utenteId)
                .orElseThrow(() -> new NotFoundException("utente non trovato"));

        if (hasAlreadyRecensione(filmId, utenteId)) {
            throw new NotValidException("hai già inserito una recensione per questo film");
        }

        recensione.setFilm(film);
        recensione.setAutore(autore);
        recensione.setData(LocalDate.now());

        return recensioneRepository.save(recensione);
    }

    @Transactional
    public Recensione updateRecensione(Long id, Long utenteId, Recensione nuovaRecensione) {
        Recensione recensione = getRecensione(id);

        if (!recensione.getAutore().getId().equals(utenteId)) {
            throw new NotValidException("non puoi modificare una recensione scritta da un altro utente");
        }

        recensione.setTesto(nuovaRecensione.getTesto());
        recensione.setVoto(nuovaRecensione.getVoto());

        return recensioneRepository.save(recensione);
    }

    @Transactional
    public void deleteRecensione(Long id, Long utenteId) {
        Recensione recensione = getRecensione(id);

        if (!recensione.getAutore().getId().equals(utenteId)) {
            throw new NotValidException("non puoi eliminare una recensione scritta da un altro utente");
        }

        recensioneRepository.delete(recensione);
    }

    @Transactional(readOnly = true)
    public int getNumeroRecensioni(Long filmId) {
        return recensioneRepository.findByFilm_Id(filmId).size();
    }

    @Transactional(readOnly = true)
    public double getVotoMedio(Long filmId) {
        List<Recensione> recensioni = recensioneRepository.findByFilm_Id(filmId);

        if (recensioni.isEmpty()) {
            return 0.0;
        }

        int somma = 0;
        for (Recensione recensione : recensioni) {
            somma += recensione.getVoto();
        }

        return (double) somma / recensioni.size();
    }
}
