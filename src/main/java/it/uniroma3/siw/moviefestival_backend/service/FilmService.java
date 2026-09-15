package it.uniroma3.siw.moviefestival_backend.service;

import it.uniroma3.siw.moviefestival_backend.exception.NotFoundException;
import it.uniroma3.siw.moviefestival_backend.model.Film;
import it.uniroma3.siw.moviefestival_backend.repository.FilmRepository;
import it.uniroma3.siw.moviefestival_backend.repository.RecensioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FilmService {
    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private RecensioneRepository recensioneRepository;

    public List<Film> getAllFilms() {
        return filmRepository.findAll();
    }

    public Film getFilm(Long id) {
        return filmRepository.findById(id).orElseThrow(() -> new NotFoundException("film non trovato"));
    }
    @Transactional
    public Film createFilm(Film film) {
        return filmRepository.save(film);
    }
    @Transactional
    public Film updateFilm(Long id, Film film) {
        Film f = getFilm(id);

        f.setTitolo(film.getTitolo());
        f.setAnno(film.getAnno());
        f.setDurata(film.getDurata());
        f.setGenere(film.getGenere());
        f.setPaeseProduzione(film.getPaeseProduzione());

        if (film.getLocandina() != null) {
            f.setLocandina(film.getLocandina());
        }

        f.setRegista(film.getRegista());

        return filmRepository.save(f);
    }
    @Transactional
    public void deleteFilm(Long id) {
        Film f = getFilm(id);

        recensioneRepository.deleteByFilm_Id(id);

        filmRepository.delete(f);
    }


    // salva la locandina caricata dall'utente e restituisce il suo URL
    @Transactional
    public String salvaLocandina(MultipartFile file) throws IOException {

        Path cartella = Paths.get("uploads", "locandine");

        Files.createDirectories(cartella);

        String nomeOriginale = file.getOriginalFilename();

        String nomeFile = UUID.randomUUID() + "-" + nomeOriginale;

        Path destinazione = cartella.resolve(nomeFile);

        Files.copy(file.getInputStream(), destinazione, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/locandine/" + nomeFile;
    }
}