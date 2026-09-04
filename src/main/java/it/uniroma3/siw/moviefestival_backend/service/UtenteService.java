package it.uniroma3.siw.moviefestival_backend.service;


import it.uniroma3.siw.moviefestival_backend.exception.NotFoundException;
import it.uniroma3.siw.moviefestival_backend.exception.NotValidException;
import it.uniroma3.siw.moviefestival_backend.model.Utente;
import it.uniroma3.siw.moviefestival_backend.repository.UtenteRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static it.uniroma3.siw.moviefestival_backend.model.Utente.DEFAULT_ROLE;

@Service
public class UtenteService {

    UtenteRepository utenteRepository;

    PasswordEncoder passwordEncoder;

    public UtenteService(UtenteRepository u, PasswordEncoder p){
        this.utenteRepository=u;
        this.passwordEncoder=p;
    }
    @Transactional(readOnly = true)
    public Utente findUtenteByUsername(String username){
        return utenteRepository.findUtenteByUsername(username).orElseThrow(()-> new NotFoundException("username non trovato"));
    }

    //per il login ci pensa il securityConfig con UserDetailsService e la parte dedicata al login
    @Transactional
    public void registraUtente(String username, String password){
        String normalizedUsername = NormalizeUsername(username);

        if(normalizedUsername.isBlank()){
            throw new NotValidException("è necessario inserire uno username");
        }
        if(utenteRepository.existsUtenteByUsername(normalizedUsername)){
            throw new NotValidException("username non disponibile, provane un altro");
        }
        if(password.isBlank()){
            throw new NotValidException("è necessario inserire una password");
        }
        Utente utente = new Utente();
        utente.setUsername(normalizedUsername);
        utente.setPassword(passwordEncoder.encode(password));
        utente.setRole(DEFAULT_ROLE);

        utenteRepository.save(utente);
    }

    private static String NormalizeUsername(String username){
        if(username==null){ // è un controllo per sicurezza, trim si null darebbe nullPointerExeption
            return "";
        }
        else{
           return username.trim().toLowerCase();
        }
    }






}
