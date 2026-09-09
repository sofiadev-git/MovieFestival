package it.uniroma3.siw.moviefestival_backend.controller;

import it.uniroma3.siw.moviefestival_backend.exception.NotValidException;
import it.uniroma3.siw.moviefestival_backend.model.Sala;
import it.uniroma3.siw.moviefestival_backend.service.SalaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SalaController {
    @Autowired
    private SalaService salaService;

    @GetMapping("/admin/sale")
    public String getSale(Model model){
        model.addAttribute("sale",salaService.getAllSale());
        aggiungiDatiUtente(model);
        return "admin/paginaListaSale";
    }

    @GetMapping("admin/sale/nuova")
    public String aggiungiSala(Model model){
        model.addAttribute("sala",new Sala());
        model.addAttribute("modifica",false);
        aggiungiDatiUtente(model);
        return "admin/formSala";
    }

    @PostMapping("/admin/sale")
    public String creaSala(@ModelAttribute Sala sala) {
        salaService.createSala(sala);
        return "redirect:/admin/sale";
    }

    @GetMapping("/admin/sale/{id}/edit")
    public  String modificaSala(@PathVariable Long id, Model model){
        model.addAttribute("sala", salaService.getSala(id));
        model.addAttribute("modifica", true);
        aggiungiDatiUtente(model);
        return "admin/formSala";
    }

    @PostMapping("/admin/sale/{id}")
    public String aggiornaSala(@PathVariable Long id, @ModelAttribute Sala sala){
        salaService.updateSala(id,sala);
        return "redirect:/admin/sale";

    }

    /*una sala è collegata a delle proiezioni, non la si può eliminare a prescindere*/
    @PostMapping("/admin/sale/{id}/delete")
    public String eliminaSala(@PathVariable Long id, Model model){
        try {
            salaService.deleteSala(id);
            return "redirect:/admin/sale";

        } catch (NotValidException exception) {
            model.addAttribute("sale", salaService.getAllSale());
            model.addAttribute("error", exception.getMessage());
            aggiungiDatiUtente(model);

            return "admin/paginaListaSale";
        }
    }



    private void aggiungiDatiUtente(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            model.addAttribute("userDetails", userDetails);
        } else {
            model.addAttribute("userDetails", null);
        }
    }
}
