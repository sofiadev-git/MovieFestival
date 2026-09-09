package it.uniroma3.siw.moviefestival_backend.service;

import it.uniroma3.siw.moviefestival_backend.exception.NotFoundException;
import it.uniroma3.siw.moviefestival_backend.exception.NotValidException;
import it.uniroma3.siw.moviefestival_backend.model.Festival;
import it.uniroma3.siw.moviefestival_backend.model.Film;
import it.uniroma3.siw.moviefestival_backend.model.Proiezione;
import it.uniroma3.siw.moviefestival_backend.model.Sala;
import it.uniroma3.siw.moviefestival_backend.repository.ProiezioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProiezioneService {

    @Autowired
    private ProiezioneRepository proiezioneRepository;

    @Autowired
    private FestivalService festivalService;

    @Autowired
    private SalaService salaService;

    @Autowired
    private FilmService filmService;


    @Transactional(readOnly = true)
    public List<Proiezione> getAllProiezioni() {
        return proiezioneRepository.findAll();
    }


    @Transactional(readOnly = true)
    public List<Proiezione> getProiezioniFestival(Long festivalId) {

        festivalService.getFestival(festivalId);

        return proiezioneRepository.findByFestival_IdOrderByDataAscOraAsc(festivalId);
    }


    @Transactional(readOnly = true)
    public Proiezione getProiezioneDelFestival(Long festivalId, Long id) {

        Proiezione proiezione = getProiezione(id);

        if (!proiezione.getFestival().getId().equals(festivalId)) {
            throw new NotValidException("Questa proiezione non appartiene a questo festival");
        }

        return proiezione;
    }


    @Transactional(readOnly = true)
    public Proiezione getProiezione(Long id) {

        return proiezioneRepository.findById(id).orElseThrow(() -> new NotFoundException("Proiezione non trovata"));
    }


    @Transactional(readOnly = true)
    public Film getFilmDelFestival(Long festivalId, Long filmId) {

        Festival festival = festivalService.getFestival(festivalId);

        Film film = filmService.getFilm(filmId);

        controllaFilmNelFestival(festival, film);

        return film;
    }


    @Transactional
    public Proiezione createProiezione(Long festivalId, Long filmId, Long salaId, Proiezione proiezione) {

        Festival festival = festivalService.getFestival(festivalId);

        Film film = filmService.getFilm(filmId);

        Sala sala = salaService.getSala(salaId);

        controllaFilmNelFestival(festival, film);

        controllaData(festival, proiezione);

        controllaDisponibilitaSala(sala, film, proiezione, null);

        proiezione.setFestival(festival);
        proiezione.setFilm(film);
        proiezione.setSala(sala);

        proiezione.setStato(Proiezione.StatoProiezione.SCHEDULED);

        return proiezioneRepository.save(proiezione);
    }


    @Transactional
    public Proiezione updateProiezione(Long festivalId, Long proiezioneId, Long salaId, Proiezione nuoviDati) {

        Proiezione proiezione = getProiezioneDelFestival(festivalId, proiezioneId);

        Festival festival = proiezione.getFestival();

        Sala sala = salaService.getSala(salaId);

        Film film = proiezione.getFilm();

        controllaData(festival, nuoviDati);

        /*
         * Se la proiezione viene annullata,
         * non sta più occupando la sala.
         */
        if (nuoviDati.getStato() != Proiezione.StatoProiezione.CANCELLED) {

            controllaDisponibilitaSala(sala, film, nuoviDati, proiezioneId);
        }

        proiezione.setData(nuoviDati.getData());
        proiezione.setOra(nuoviDati.getOra());
        proiezione.setSala(sala);
        proiezione.setStato(nuoviDati.getStato());

        return proiezioneRepository.save(proiezione);
    }


    @Transactional
    public void deleteProiezione(Long festivalId, Long proiezioneId) {

        Proiezione proiezione = getProiezioneDelFestival(festivalId, proiezioneId);

        proiezioneRepository.delete(proiezione);
    }


    private void controllaFilmNelFestival(Festival festival, Film film) {

        boolean partecipa = festival.getFilmPartecipanti().stream().anyMatch(f -> f.getId().equals(film.getId()));

        if (!partecipa) {
            throw new NotValidException("Il film non partecipa a questo festival");
        }
    }


    private void controllaData(Festival festival, Proiezione proiezione) {

        if (proiezione.getData() == null) {
            throw new NotValidException("La data è obbligatoria");
        }

        if (proiezione.getOra() == null) {
            throw new NotValidException("L'ora è obbligatoria");
        }

        if (proiezione.getData().isBefore(festival.getDataInizio()) || proiezione.getData().isAfter(festival.getDataFine())) {

            throw new NotValidException("La data della proiezione deve rientrare nelle date del festival");
        }
    }


    private void controllaDisponibilitaSala(Sala sala, Film film, Proiezione nuovaProiezione, Long proiezioneDaEscludere) {

        /*
         * Costruiamo l'istante di inizio e di fine
         * della nuova proiezione.
         */

        LocalDateTime nuovoInizio = LocalDateTime.of(nuovaProiezione.getData(), nuovaProiezione.getOra());

        LocalDateTime nuovaFine = nuovoInizio.plusMinutes(film.getDurata());


        /*
         * Controlliamo anche il giorno precedente perché,
         * ad esempio, un film iniziato alle 23:30 potrebbe
         * terminare dopo la mezzanotte.
         */

        LocalDate primaDataDaControllare = nuovaProiezione.getData().minusDays(1);

        LocalDate ultimaDataDaControllare = nuovaFine.toLocalDate();


        List<Proiezione> proiezioniEsistenti = proiezioneRepository.findBySala_IdAndDataBetween(sala.getId(), primaDataDaControllare, ultimaDataDaControllare);


        for (Proiezione esistente : proiezioniEsistenti) {

            /*
             * Durante una modifica dobbiamo ignorare
             * la proiezione che stiamo modificando,
             * altrimenti entrerebbe in conflitto con se stessa.
             */

            if (proiezioneDaEscludere != null && esistente.getId().equals(proiezioneDaEscludere)) {

                continue;
            }


            /*
             * Una proiezione annullata non occupa più
             * la sala.
             */

            if (esistente.getStato() == Proiezione.StatoProiezione.CANCELLED) {

                continue;
            }


            LocalDateTime inizioEsistente = LocalDateTime.of(esistente.getData(), esistente.getOra());

            LocalDateTime fineEsistente = inizioEsistente.plusMinutes(esistente.getFilm().getDurata());


            /*
             * Due intervalli si sovrappongono quando:
             *
             * nuovoInizio < fineEsistente
             *          E
             * nuovaFine > inizioEsistente
             */

            boolean sovrapposte = nuovoInizio.isBefore(fineEsistente) && nuovaFine.isAfter(inizioEsistente);


            if (sovrapposte) {

                throw new NotValidException("La sala è già occupata da un'altra proiezione in questo intervallo di tempo");
            }
        }
    }
}