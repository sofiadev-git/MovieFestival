package it.uniroma3.siw.moviefestival_backend.controller;

import it.uniroma3.siw.moviefestival_backend.exception.NotValidException;
import it.uniroma3.siw.moviefestival_backend.model.Regista;
import it.uniroma3.siw.moviefestival_backend.service.RegistaService;
import org.springframework.beans.factory.annotation.Autowired;
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

import static it.uniroma3.siw.moviefestival_backend.model.Utente.ADMIN_ROLE;

@Controller
public class RegistaController {

    @Autowired
    private RegistaService registaService;

    @GetMapping("/registi") //quindi ci porta a /admin/registi
    public String getRegisti(Model model){
        model.addAttribute("registi", registaService.getAllRegisti()) ;    //etichetta per html e il corrispondente nel database, con thymeleaf si potrà fare ${registi}
        aggiungiDatiUtente(model);
        return "paginaListaRegisti";
    }

    @GetMapping("/registi/{id}")
    public String dettaglioRegista(@PathVariable Long id, Model model) {
        model.addAttribute("regista", registaService.getRegistaConFilmDiretti(id));
        aggiungiDatiUtente(model);
        return "paginaRegista";
    }

    @GetMapping("/admin/registi/nuovo")
    public String aggiungiRegista(Model model){
        model.addAttribute("regista", new Regista());
        model.addAttribute("modifica", false);
        aggiungiDatiUtente(model);
        return "/admin/formRegista";
    }

    @PostMapping("/admin/registi")
    public String creaRegista(@ModelAttribute Regista regista,Model model) {
        registaService.createRegista(regista);
        aggiungiDatiUtente(model);
        return "redirect:/registi";
    }

    @GetMapping("/admin/registi/{id}/edit")
    public String modificaRegista(@PathVariable Long id, Model model){
        model.addAttribute("regista", registaService.getRegista(id)); //dati già inseriti
        model.addAttribute("modifica", true); // lo usiamo nel thymeleaf con @{modifica} dove se la condizione è true allora mostra il form con le informazioni preesistenti, altrimenti il form vuoto
        aggiungiDatiUtente(model);
        return "admin/formRegista";

    }

    @PostMapping("/admin/registi/{id}")
    public String aggiornaRegista(@PathVariable Long id ,@ModelAttribute Regista regista,Model model){ //@PathVariable prende il valore dall'url, @ModelAttribute Regista regista contiene i nuovi valori inseriti nel form
        registaService.updateRegista(id,regista);
        aggiungiDatiUtente(model);
        return "redirect:/registi/"+id; //redirect si usa con i post, perché ricarica la pagina (quindi fa si che siano presenti i nuovi valori)
    }

    @PostMapping("/admin/registi/{id}/delete")
    public String eliminaRegista(@PathVariable Long id){
        registaService.deleteRegista(id);
        return "redirect:/registi";
    }


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
