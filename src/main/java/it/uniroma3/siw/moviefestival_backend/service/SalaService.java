package it.uniroma3.siw.moviefestival_backend.service;

import it.uniroma3.siw.moviefestival_backend.exception.NotFoundException;
import it.uniroma3.siw.moviefestival_backend.exception.NotValidException;
import it.uniroma3.siw.moviefestival_backend.model.Sala;
import it.uniroma3.siw.moviefestival_backend.repository.SalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SalaService {
    @Autowired
    SalaRepository salaRepository;

    @Transactional(readOnly = true)
    public List<Sala> getAllSale(){
        return salaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Sala getSala(Long id){
        return salaRepository.findById(id).orElseThrow(()-> new NotFoundException("sala non trovata"));
    }
    @Transactional
    public Sala createSala(Sala sala){
        controllaCapienza(sala);
        return salaRepository.save(sala);
    }
    @Transactional
    public Sala updateSala(Long id, Sala sala){
        Sala s = getSala(id);
        controllaCapienza(sala);
        s.setNome(sala.getNome());
        s.setIndirizzo(sala.getIndirizzo());
        s.setCapienza(sala.getCapienza());
        return salaRepository.save(s);
    }
    @Transactional
    public void deleteSala(Long id){
        Sala s = getSala(id);
        if (!s.getProiezioni().isEmpty()) {
            throw new NotValidException("Non puoi eliminare una sala associata a una proiezione");
        }
        salaRepository.delete(s);
    }


    private void controllaCapienza(Sala sala){
        if(sala.getCapienza()==null||sala.getCapienza()<=0){
            throw new NotValidException("la capienza non può essere pari o inferiore a zero");
        }
    }

}
