package it.uniroma3.siw.moviefestival_backend.controller;

import it.uniroma3.siw.moviefestival_backend.model.Festival;
import it.uniroma3.siw.moviefestival_backend.service.FestivalService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import static it.uniroma3.siw.moviefestival_backend.model.Utente.ADMIN_ROLE;

@Controller
public class FestivalController {

    private final FestivalService festivalService;

    public FestivalController(FestivalService festivalService) {
        this.festivalService = festivalService;
    }

    @GetMapping({"/", "/festivals"})
    public String home(Model model) {
        model.addAttribute("festivals", festivalService.getAllFestivals());
        aggiungiDatiUtente(model);

        return "paginaListaFestival";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        aggiungiDatiUtente(model);
        return "admin";
    }

    @GetMapping("/admin/festivals/nuovo")
    public String aggiungiFestival(Model model) {
        model.addAttribute("festival", new Festival());
        model.addAttribute("modifica", false);
        aggiungiDatiUtente(model);
        return "admin/formFestival";
    }

    @PostMapping("/admin/festivals")
    public String creaFestival(@ModelAttribute Festival festival) {
        Festival nuovoFestival = festivalService.createFestival(festival);
        return "redirect:/admin/festivals/" + nuovoFestival.getId() + "/proiezioni";
    }

    @GetMapping("/admin/festivals/{id}/edit")
    public String modificaFestival(@PathVariable Long id, Model model) {
        model.addAttribute("festival", festivalService.getFestival(id));
        model.addAttribute("modifica", true);
        aggiungiDatiUtente(model);
        return "admin/formFestival";
    }

    @PostMapping("/admin/festivals/{id}")
    public String aggiornaFestival(@PathVariable Long id, @ModelAttribute Festival festival) {
        festivalService.updateFestival(id, festival);
        return "redirect:/admin/festivals/" + id + "/proiezioni";
    }

    @PostMapping("/admin/festivals/{festivalId}/films")
    public String aggiungiFilmAlFestival(@PathVariable Long festivalId, @RequestParam Long filmId) {
        festivalService.addFilmToFestival(festivalId, filmId);
        return "redirect:/admin/festivals/" + festivalId + "/proiezioni";
    }

    @PostMapping("/admin/festivals/{festivalId}/films/{filmId}/delete")
    public String rimuoviFilmDalFestival(@PathVariable Long festivalId, @PathVariable Long filmId) {
        festivalService.removeFilmFromFestival(festivalId, filmId);
        return "redirect:/admin/festivals/" + festivalId + "/proiezioni";
    }

    @PostMapping("/admin/festivals/{id}/delete")
    public String eliminaFestival(@PathVariable Long id) {
        festivalService.deleteFestival(id);
        return "redirect:/festivals";
    }




    /* Dati usati da Thymeleaf per mostrare la navbar e i comandi admin. */
    private void aggiungiDatiUtente(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            model.addAttribute("userDetails", null);
            model.addAttribute("isAdmin", false);
            return;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            model.addAttribute("userDetails", userDetails);
        } else {
            model.addAttribute("userDetails", null);
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> ADMIN_ROLE.equals(authority.getAuthority()));
        model.addAttribute("isAdmin", isAdmin);
    }
}
