package it.uniroma3.siw.moviefestival_backend.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import static it.uniroma3.siw.moviefestival_backend.model.Utente.ADMIN_ROLE;

/*Serve per il design in comune a tutte le pagine*/
@ControllerAdvice(annotations = Controller.class)
public class NavigationAdvice {
    @ModelAttribute
    public void addNavigation(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
        boolean admin = authenticated && auth.getAuthorities().stream()
                .anyMatch(role -> ADMIN_ROLE.equals(role.getAuthority()));
        model.addAttribute("navAuthenticated", authenticated);
        model.addAttribute("navAdmin", admin);
        model.addAttribute("navUsername", authenticated ? auth.getName() : "");
    }
}
