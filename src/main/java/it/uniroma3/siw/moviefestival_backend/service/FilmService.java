package it.uniroma3.siw.moviefestival_backend.service;

import it.uniroma3.siw.moviefestival_backend.exception.NotFoundException;
import it.uniroma3.siw.moviefestival_backend.model.Film;
import it.uniroma3.siw.moviefestival_backend.repository.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.util.Arrays;

import java.util.List;

@Service
public class FilmService {
    @Autowired
    private FilmRepository filmRepository;

    public List<Film> getAllFilms(){
        return filmRepository.findAll();
    }

    public Film getFilm(Long id){
        return filmRepository.findById(id).orElseThrow(()-> new NotFoundException("film non trovato"));
    }

    public Film createFilm(Film film){
        return filmRepository.save(film);
    }

    public Film updateFilm(Long id, Film film){
        Film f = getFilm(id);

        f.setTitolo(film.getTitolo());
        f.setAnno(film.getAnno());
        f.setDurata(film.getDurata());
        f.setGenere(film.getGenere());
        f.setPaeseProduzione(film.getPaeseProduzione());
        f.setLocandina(film.getLocandina());
        f.setRegista(film.getRegista());

        return filmRepository.save(f);
    }

    public void deleteFilm(Long id){
        Film f= getFilm(id);
        filmRepository.delete(f);
    }



// metodo per caricare le immagini dal progetto, viene salvatoo l'url nella stringa locandina e poi caricata
    public List<String> getLocandineDisponibili(){
        File cartella =
                new File("src/main/resources/static/images/locandine");

        File[] files = cartella.listFiles((dir, nome) ->
                nome.toLowerCase().endsWith(".png"));

        if(files == null){
            return List.of();
        }

        return Arrays.stream(files)
                .map(File::getName)
                .sorted()
                .map(nome -> "/images/locandine/" + nome)
                .toList();
    }
}
