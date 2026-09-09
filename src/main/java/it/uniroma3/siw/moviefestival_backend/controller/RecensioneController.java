package it.uniroma3.siw.moviefestival_backend.controller;

import it.uniroma3.siw.moviefestival_backend.model.Recensione;
import it.uniroma3.siw.moviefestival_backend.model.Utente;
import it.uniroma3.siw.moviefestival_backend.service.FilmService;
import it.uniroma3.siw.moviefestival_backend.service.RecensioneService;
import it.uniroma3.siw.moviefestival_backend.service.UtenteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RecensioneController {

    @Autowired
    RecensioneService recensioneService;

    @Autowired
    FilmService filmService;

    @Autowired
    UtenteService utenteService;

    @GetMapping("/films/{filmId}/recensioni/nuova")
    public String creaRecensione(@PathVariable Long filmId, Model model, Authentication authentication) {
        Utente utente = getUtenteAutenticato(authentication);
        Long utenteId = utente.getId();

        if (recensioneService.hasAlreadyRecensione(filmId, utenteId)) {
            return "redirect:/films/" + filmId + "?erroreRecensione=giaPresente";
        }

        model.addAttribute("recensione", new Recensione());
        model.addAttribute("film", filmService.getFilm(filmId));
        model.addAttribute("modifica", false);
        aggiungiDatiUtente(model);

        return "formRecensione";
    }

    @PostMapping("/films/{filmId}/recensioni")
    public String aggiungiRecensione(@PathVariable Long filmId,
                                     @Valid @ModelAttribute("recensione") Recensione recensione,
                                     BindingResult bindingResult,
                                     Authentication authentication,
                                     Model model) {
        Utente utente = getUtenteAutenticato(authentication);
        Long utenteId = utente.getId();

        if (bindingResult.hasErrors()) {
            model.addAttribute("film", filmService.getFilm(filmId));
            model.addAttribute("modifica", false);
            aggiungiDatiUtente(model);
            return "formRecensione";
        }

        if (recensioneService.hasAlreadyRecensione(filmId, utenteId)) {
            return "redirect:/films/" + filmId + "?erroreRecensione=giaPresente";
        }

        recensioneService.createRecensione(filmId, utenteId, recensione);
        return "redirect:/films/" + filmId;
    }

    @GetMapping("/recensioni/{id}/edit")
    public String modificaRecensione(@PathVariable Long id, Model model, Authentication authentication) {
        Utente utente = getUtenteAutenticato(authentication);
        Long utenteId = utente.getId();
        Recensione recensione = recensioneService.getRecensione(id);

        if (!recensioneService.isAutore(id, utenteId)) {
            return "redirect:/films/" + recensione.getFilm().getId() + "?erroreRecensione=nonAutore";
        }

        model.addAttribute("recensione", recensione);
        model.addAttribute("film", recensione.getFilm());
        model.addAttribute("modifica", true);
        aggiungiDatiUtente(model);

        return "formRecensione";
    }

    @PostMapping("/recensioni/{id}")
    public String aggiornaRecensione(@PathVariable Long id,
                                     @Valid @ModelAttribute("recensione") Recensione recensione,
                                     BindingResult bindingResult,
                                     Authentication authentication,
                                     Model model) {
        Utente utente = getUtenteAutenticato(authentication);
        Long utenteId = utente.getId();
        Recensione corrente = recensioneService.getRecensione(id);
        Long filmId = corrente.getFilm().getId();

        if (!recensioneService.isAutore(id, utenteId)) {
            return "redirect:/films/" + filmId + "?erroreRecensione=nonAutore";
        }

        if (bindingResult.hasErrors()) {
            recensione.setId(id);
            model.addAttribute("film", corrente.getFilm());
            model.addAttribute("modifica", true);
            aggiungiDatiUtente(model);
            return "formRecensione";
        }

        recensioneService.updateRecensione(id, utenteId, recensione);
        return "redirect:/films/" + filmId;
    }

    @PostMapping("/recensioni/{id}/delete")
    public String eliminaRecensione(@PathVariable Long id, Authentication authentication) {
        Recensione recensione = recensioneService.getRecensione(id);
        Long filmId = recensione.getFilm().getId();
        Utente utente = getUtenteAutenticato(authentication);
        Long utenteId = utente.getId();

        if (!recensioneService.isAutore(id, utenteId)) {
            return "redirect:/films/" + filmId + "?erroreRecensione=nonAutore";
        }

        recensioneService.deleteRecensione(id, utenteId);
        return "redirect:/films/" + filmId;
    }

    private void aggiungiDatiUtente(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            model.addAttribute("userDetails", userDetails);
        } else {
            model.addAttribute("userDetails", null);
        }
    }

    private Utente getUtenteAutenticato(Authentication authentication) {
        return utenteService.findUtenteByUsername(authentication.getName());
    }
}
