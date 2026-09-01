package it.uniroma3.siw.moviefestival_backend.controller;

import it.uniroma3.siw.moviefestival_backend.service.FestivalService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FestivalController {

    private final FestivalService festivalService;

    public FestivalController(FestivalService festivalService) {
        this.festivalService = festivalService;
    }

    @GetMapping({"/", "/festivals"})
    public String home(Model model) {
        model.addAttribute("festivals", festivalService.getAllFestivals());
        model.addAttribute("userDetails", getUserDetails());

        return "paginaListaFestival";
    }


    //serve per mostrare o meno contenuti (es bottoni) in base a se un utente è autenticato o meno
    public UserDetails getUserDetails() {
        UserDetails user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            user = (UserDetails) authentication.getPrincipal();
        }
        return user;
    }
}
