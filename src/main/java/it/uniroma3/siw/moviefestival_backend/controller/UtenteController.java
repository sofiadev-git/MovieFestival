package it.uniroma3.siw.moviefestival_backend.controller;

import it.uniroma3.siw.moviefestival_backend.exception.NotValidException;
import it.uniroma3.siw.moviefestival_backend.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UtenteController {

    @Autowired
    private UtenteService utenteService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "signUp";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password, @RequestParam String confirmPassword, Model model) { // Model serve a passare dati dal controller al template Thymeleaf
        //@REquestPAram prende il valore dalla richiesta HTTP
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Le password non coincidono");// addAttribute fa si che la pagina possa leggere questo attributo e mostrare il messaggio
            model.addAttribute("username", username);
            return "signUp";
            //Torno a signUp.html, ma insieme alla pagina gli passo anche il messaggio di errore e lo username che l’utente aveva già inserito
        }

        try {
            utenteService.registraUtente(username,password);
            return "redirect:/login?registered=true";
        } catch (NotValidException exception) { // se dal service arriva un eccezione
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("username", username);
            return "signUp";
        }
    }
}
