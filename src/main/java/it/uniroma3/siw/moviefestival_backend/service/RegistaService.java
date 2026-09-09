package it.uniroma3.siw.moviefestival_backend.service;

import it.uniroma3.siw.moviefestival_backend.exception.NotFoundException;
import it.uniroma3.siw.moviefestival_backend.exception.NotValidException;
import it.uniroma3.siw.moviefestival_backend.model.Festival;
import it.uniroma3.siw.moviefestival_backend.model.Regista;
import it.uniroma3.siw.moviefestival_backend.repository.RegistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RegistaService {

    /*non è regista gestire l'associazione con i film diretti
    * visto che un film quando viene creato deve avere un regista, ma un regista può esistere senza film*/


    @Autowired
    private RegistaRepository registaRepository;

    public List<Regista> getAllRegisti(){
        return registaRepository.findAll();
    }

    public Regista getRegista(Long id){
        return registaRepository.findById(id).orElseThrow(()-> new NotFoundException("regista non trovato"));
    }

    @Transactional(readOnly = true)
    public Regista getRegistaConFilmDiretti(Long id){
        Regista regista = registaRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("regista non trovato"));
        // Inizializza la collezione lazy usata dalla pagina pubblica del regista.
        regista.getFilmDiretti().size();
        return regista;
    }

    public Regista createRegista(Regista regista){
        controllaData(regista);
        return registaRepository.save(regista);
    }

    public Regista updateRegista(Long id, Regista r){
        Regista regista = getRegista(id);
        controllaData(r);
        regista.setNome(r.getNome());
        regista.setCognome(r.getCognome());
        regista.setDataNascita(r.getDataNascita());
        regista.setNazionalita(r.getNazionalita());
        return  registaRepository.save(regista);
    }

    public void deleteRegista(Long id){
        Regista regista = getRegista(id);
        registaRepository.delete(regista);
    }

    public void controllaData(Regista regista){
        if(regista.getDataNascita()!= null &&
                (regista.getDataNascita().isBefore(LocalDate.of(1800, 1, 1))|| regista.getDataNascita().isAfter(LocalDate.of(2026, 1, 1)))) {
            throw new NotValidException("Le date inserite non sono valide"); //temporaneo
        }
    }
}
