package it.uniroma3.siw.moviefestival_backend.controller;

import it.uniroma3.siw.moviefestival_backend.model.Film;
import it.uniroma3.siw.moviefestival_backend.model.Utente;
import it.uniroma3.siw.moviefestival_backend.service.FilmService;
import it.uniroma3.siw.moviefestival_backend.service.RecensioneService;
import it.uniroma3.siw.moviefestival_backend.service.RegistaService;
import it.uniroma3.siw.moviefestival_backend.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static it.uniroma3.siw.moviefestival_backend.model.Utente.ADMIN_ROLE;

@Controller
public class FilmController {

    @Autowired
    FilmService filmService;

    @Autowired
    RegistaService registaService;

    @Autowired
    RecensioneService recensioneService;

    @Autowired
    UtenteService utenteService;


    @GetMapping("/films")
    public String getFilms(Model model) {
        model.addAttribute("films", filmService.getAllFilms());
        aggiungiDatiUtente(model);
        return "paginaListaFilm";
    }


    @GetMapping("/films/{id}")
    public String dettaglioFilm(@PathVariable Long id, @RequestParam(defaultValue = "0") int page, Model model) {

        if (page < 0) {
            page = 0;
        }

        model.addAttribute("film", filmService.getFilm(id));

        // recensioni paginate
        model.addAttribute("recensioni", recensioneService.getRecensioniFilm(id, page));

        // statistiche recensioni
        model.addAttribute("numeroRecensioni", recensioneService.getNumeroRecensioni(id));

        model.addAttribute("votoMedio", recensioneService.getVotoMedio(id));

        aggiungiDatiUtente(model);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        /*
         * Se l'utente è autenticato, recuperiamo il suo id
         * e controlliamo se ha già recensito questo film.
         */
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {

            Utente utente = utenteService.findUtenteByUsername(userDetails.getUsername());

            Long utenteId = utente.getId();

            model.addAttribute("utenteId", utenteId);

            model.addAttribute("haGiaRecensito", recensioneService.hasAlreadyRecensione(id, utenteId));

        } else {
            model.addAttribute("utenteId", null);
            model.addAttribute("haGiaRecensito", false);
        }

        return "paginaFilm";
    }


    @GetMapping("/admin/films/nuovo")
    public String aggiungiFilm(Model model) {
        model.addAttribute("film", new Film());
        model.addAttribute("registi", registaService.getAllRegisti());
        model.addAttribute("modifica", false);
        aggiungiDatiUtente(model);

        return "admin/formFilm";
    }


    @PostMapping("/admin/films")
    public String creaFilm(@ModelAttribute Film film, @RequestParam Long registaId, @RequestParam("fileLocandina") MultipartFile fileLocandina) throws IOException {

        film.setRegista(registaService.getRegista(registaId));

        if (!fileLocandina.isEmpty()) {
            film.setLocandina(filmService.salvaLocandina(fileLocandina));
        }

        filmService.createFilm(film);

        return "redirect:/films";
    }


    @GetMapping("/admin/films/{id}/edit")
    public String modificaFilm(@PathVariable Long id, Model model) {
        model.addAttribute("film", filmService.getFilm(id));
        model.addAttribute("registi", registaService.getAllRegisti());
        model.addAttribute("modifica", true);
        aggiungiDatiUtente(model);

        return "admin/formFilm";
    }


    @PostMapping("/admin/films/{id}")
    public String aggiornaFilm(@PathVariable Long id, @ModelAttribute Film film, @RequestParam Long registaId, @RequestParam("fileLocandina") MultipartFile fileLocandina) throws IOException {

        film.setRegista(registaService.getRegista(registaId));

        if (!fileLocandina.isEmpty()) {
            film.setLocandina(filmService.salvaLocandina(fileLocandina));
        }

        filmService.updateFilm(id, film);

        return "redirect:/films/" + id;
    }


    @PostMapping("/admin/films/{id}/delete")
    public String eliminaFilm(@PathVariable Long id) {
        filmService.deleteFilm(id);
        return "redirect:/films";
    }


    private void aggiungiDatiUtente(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {

            model.addAttribute("userDetails", userDetails);

            boolean isAdmin = authentication.getAuthorities().stream().anyMatch(authority -> ADMIN_ROLE.equals(authority.getAuthority()));

            model.addAttribute("isAdmin", isAdmin);

        } else {
            model.addAttribute("userDetails", null);
            model.addAttribute("isAdmin", false);
        }
    }
}