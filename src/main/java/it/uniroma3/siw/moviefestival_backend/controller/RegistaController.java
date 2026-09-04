package it.uniroma3.siw.moviefestival_backend.controller;

import it.uniroma3.siw.moviefestival_backend.model.Regista;
import it.uniroma3.siw.moviefestival_backend.service.RegistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/registi") // ogni GetMapping avrà da questa radice
public class RegistaController {

    @Autowired
    private RegistaService registaService;

    @GetMapping //quindi ci porta a /admin/registi
    public String getRegisti(Model model){
        model.addAttribute("registi", registaService.getAllRegisti()) ;    //etichetta per html e il corrispondente nel database, con thymeleaf si potrà fare ${registi}
        return "admin/paginaListaRegisti";
    }

    @GetMapping("/nuovo")
    public String aggiungiRegista(Model model){
        model.addAttribute("regista", new Regista());
        return "/admin/formRegista";
    }

    @PostMapping//("/admin/registi") dal RequestMapping
    public String creaRegista(@ModelAttribute Regista regista) {
        registaService.createRegista(regista);
        return "redirect:/admin/registi";
    }

    @GetMapping("/{id}/edit")
    public String modificaRegista(@PathVariable Long id, Model model){
        model.addAttribute("regista", registaService.getRegista(id)); //dati già inseriti
        model.addAttribute("modifica", true);
        return "admin/formRegista";

    }

    @PostMapping("/{id}")
    public String aggiornaRegista(@PathVariable Long id ,@ModelAttribute Regista regista){ //@PathVariable prende il valore dall'url, @ModelAttribute Regista regista contiene i nuovi valori inseriti nel form
        registaService.updateRegista(id,regista);
        return "redirect:/admin/registi"; //redirect si usa con i post, perché ricarica la pagina (quindi fa si che siano presenti i nuovi valori)
    }
}
