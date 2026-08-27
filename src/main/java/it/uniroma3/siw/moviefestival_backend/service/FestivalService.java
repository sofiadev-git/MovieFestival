package it.uniroma3.siw.moviefestival_backend.service;

import it.uniroma3.siw.moviefestival_backend.model.Festival;
import it.uniroma3.siw.moviefestival_backend.model.Film;
import it.uniroma3.siw.moviefestival_backend.model.Proiezione;
import it.uniroma3.siw.moviefestival_backend.dto.BasicFilmInfoDto;
import it.uniroma3.siw.moviefestival_backend.dto.FestivalDetailsDto;
import it.uniroma3.siw.moviefestival_backend.dto.ProiezioniFilmDto;
import it.uniroma3.siw.moviefestival_backend.repository.FestivalRepository;
import it.uniroma3.siw.moviefestival_backend.repository.FilmRepository;
import it.uniroma3.siw.moviefestival_backend.repository.ProiezioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FestivalService {

    @Autowired
    private FestivalRepository festivalRepository;
    @Autowired
    private FilmRepository filmRepository;
    @Autowired
    private ProiezioneRepository proiezioneRepository;

    //caso d'uso 1: visualizzazione dell’elenco dei festival (con thymeleaf)
    @Transactional(readOnly = true)
    public List<Festival> getAllFestivals(){
        return festivalRepository.findAll();
    }

    //per i casi 2-3-4, facciamo visualizzazione festival, film e proiezioni insieme
    @Transactional(readOnly = true)
    public FestivalDetailsDto getFestivalDetails(Long festivalId){

        Festival festival = festivalRepository.findById(festivalId).orElseThrow(() -> new RuntimeException("Festival non trovato"));
        /*registriamo il festival che ci interessa
        * findById(id) restituisce un optional, che permette di lanciare un eccezione se il valore non esiste
        * al momento usiamo RuntimeException*/

        List<Film> film = filmRepository.findByFestival_idOrderByTitoloAsc(festivalId);

        List<Proiezione> proiezioni = proiezioneRepository.findByFestival_IdOrderByDataAscOraAsc(festivalId);

        //dobbiamo raggruppare le proiezioni (solo i dati che ci servono -> dto) in base al film con map
        Map<Long,List<ProiezioniFilmDto>> proiezioniFilm = new HashMap<>();
        //Ogni proiezione ha il suo dto, Creare la classe/record DTO non basta, il service deve passargli i parametri. Vanno costruiti dal più interno al più esterno
        for(Proiezione proiezione:proiezioni){
            ProiezioniFilmDto proiezioniFilmDto =
                    new ProiezioniFilmDto(
                            proiezione.getId(),
                            proiezione.getData(),
                            proiezione.getOra(),
                            proiezione.getStato(),
                            proiezione.getSala().getNome()
                    );

            Long filmId = proiezione.getFilm().getId();
            if( !proiezioniFilm.containsKey(filmId)) {
                proiezioniFilm.put(filmId, new ArrayList<>());
            }
            proiezioniFilm.get(filmId).add(proiezioniFilmDto);
        }

        //ogni dto di ogni film del festival sta qui
        List<BasicFilmInfoDto> filmDto = new ArrayList<>();

        for(Film f : film){
            filmDto.add(new BasicFilmInfoDto(
                    f.getId(),
                    f.getTitolo(),
                    f.getGenere(),
                    f.getLocandina(),
                    proiezioniFilm.getOrDefault(f.getId(),new ArrayList<>()) // se non ci sono proiezioni associate a quel film e festival, crea la lista al posto di dare null nella map
            ));
        }
        return new FestivalDetailsDto(
                festival.getId(),
                festival.getNome(),
                festival.getAnno(),
                festival.getCitta(),
                festival.getDataInizio(),
                festival.getDataFine(),
                festival.getDescrizione(),
                filmDto
        );
    }
    /*  CRUD  */

    //crea
    @Transactional
    public Festival createFestival(Festival festival){
        //non serve new festival perché spring crea in automatico l'oggetto dal form
        controllaDate(festival);
        return festivalRepository.save(festival);
    }

    //modifica
    @Transactional
    public Festival updateFestival(Long id, Festival f){ //passo id e i nuovi dati
        Festival festival = festivalRepository.findById(id).orElseThrow(()->new RuntimeException("Festival non trovato"));
        controllaDate(f);
        //riprendo le proiezioni e controllo che, se sono cambiate, le date siano coerenti
        List<Proiezione> proiezioni = proiezioneRepository.findByFestival_IdOrderByDataAscOraAsc(id);
        for (Proiezione p : proiezioni){
            if (p.getData().isAfter(f.getDataFine()) || p.getData().isBefore(f.getDataInizio())){
                throw new RuntimeException("La data di una o più proiezioni non rientra nelle date del festival");
            }
        }
        festival.setNome(f.getNome());
        festival.setAnno(f.getAnno());
        festival.setCitta(f.getCitta());
        festival.setDataInizio(f.getDataInizio());
        festival.setDataFine(f.getDataFine());
        festival.setDescrizione(f.getDescrizione());
        //le proiezioni e i film sono casi separati

        return festivalRepository.save(festival);
    }

        //aggiunta di un film a un festival (non creazione film)
    @Transactional
    public void addFilmToFestival(Long festivalId, Long id){
        Festival f = festivalRepository.findById(festivalId).orElseThrow(()->new RuntimeException("Festival non trovato"));
        Film film = filmRepository.findById(id).orElseThrow(()-> new RuntimeException("film non trovato"));
        //se il film ce già (anche se un film nel festival non dovrebbe risultare nella lista di film da poter aggiungere)
        if (!f.getFilmPartecipanti().add(film)) {
            throw new RuntimeException("Il film partecipa già a questo festival");
        }
        //aggiungi il festival nella lista dei festival a cui partecipa
        //la relazione opposta viene fatta nell'if
        film.getFestival().add(f);
        //aggiorna il festival
        festivalRepository.save(f);
    }

    //elimina film da un festival (non elimina film): soffisfa il requisito elimina film dal festival (con relative proiezioni)
    @Transactional
    public void removeFilmFromFestival(Long festivalId, Long id){
        Festival f = festivalRepository.findById(festivalId).orElseThrow(()->new RuntimeException("Festival non trovato"));
        Film film = filmRepository.findById(id).orElseThrow(()-> new RuntimeException("film non trovato"));

        if (!f.getFilmPartecipanti().contains(film)) { //controllo per sicurezza
            throw new RuntimeException("Il film non partecipa a questo festival");
        }

        //elimina anche le relative proiezioni
        proiezioneRepository.deleteByFestival_IdAndFilm_Id(festivalId,id);
        f.getFilmPartecipanti().remove(film);
        film.getFestival().remove(f);

        //aggiorna il festival
        festivalRepository.save(f);
    }


    /*{non necessario} controlla che la data d'inizio non sia successiva a quella finale*/
    public void controllaDate(Festival festival){
        if(festival.getDataInizio()!=null &&
                festival.getDataFine()!=null &&
                festival.getDataFine().isBefore(festival.getDataInizio())) {
        throw new RuntimeException("Le date inserite non sono valide"); //temporaneo
        }
    }
}
