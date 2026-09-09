package it.uniroma3.siw.moviefestival_backend.controller;

import it.uniroma3.siw.moviefestival_backend.model.Proiezione;
import it.uniroma3.siw.moviefestival_backend.service.FestivalService;
import it.uniroma3.siw.moviefestival_backend.service.ProiezioneService;
import it.uniroma3.siw.moviefestival_backend.service.SalaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProiezioneController {

    private final ProiezioneService proiezioneService;
    private final FestivalService festivalService;
    private final SalaService salaService;

    public ProiezioneController(ProiezioneService proiezioneService,
                                FestivalService festivalService,
                                SalaService salaService) {
        this.proiezioneService = proiezioneService;
        this.festivalService = festivalService;
        this.salaService = salaService;
    }

    @GetMapping("/admin/festivals/{festivalId}/proiezioni")
    public String getProiezioniFestival(@PathVariable Long festivalId,
                                        Model model) {

        model.addAttribute(
                "festival",
                festivalService.getFestival(festivalId)
        );

        model.addAttribute(
                "proiezioni",
                proiezioneService.getProiezioniFestival(festivalId)
        );

        model.addAttribute(
                "films",
                festivalService.getFilmPartecipanti(festivalId)
        );

        model.addAttribute(
                "filmDisponibili",
                festivalService.getFilmDisponibili(festivalId)
        );

        return "admin/paginaListaProiezioni";
    }

    @GetMapping("/admin/festivals/{festivalId}/films/{filmId}/proiezioni/nuova")
    public String aggiungiProiezione(@PathVariable Long festivalId,
                                     @PathVariable Long filmId,
                                     Model model) {

        model.addAttribute("proiezione", new Proiezione());

        model.addAttribute(
                "film",
                proiezioneService.getFilmDelFestival(festivalId, filmId)
        );

        model.addAttribute("modifica", false);

        aggiungiDatiForm(festivalId, model);

        return "admin/formProiezione";
    }

    @PostMapping("/admin/festivals/{festivalId}/films/{filmId}/proiezioni")
    public String creaProiezione(@PathVariable Long festivalId,
                                 @PathVariable Long filmId,
                                 @ModelAttribute Proiezione proiezione,
                                 @RequestParam Long salaId) {

        proiezioneService.createProiezione(
                festivalId,
                filmId,
                salaId,
                proiezione
        );

        return "redirect:/admin/festivals/"
                + festivalId
                + "/proiezioni";
    }

    @GetMapping("/admin/festivals/{festivalId}/proiezioni/{id}/edit")
    public String modificaProiezione(@PathVariable Long festivalId,
                                     @PathVariable Long id,
                                     Model model) {

        Proiezione proiezione =
                proiezioneService.getProiezioneDelFestival(
                        festivalId,
                        id
                );

        model.addAttribute("proiezione", proiezione);
        model.addAttribute("film", proiezione.getFilm());
        model.addAttribute("modifica", true);

        aggiungiDatiForm(festivalId, model);

        return "admin/formProiezione";
    }

    @PostMapping("/admin/festivals/{festivalId}/proiezioni/{id}")
    public String aggiornaProiezione(@PathVariable Long festivalId,
                                     @PathVariable Long id,
                                     @ModelAttribute Proiezione proiezione,
                                     @RequestParam Long salaId) {

        proiezioneService.updateProiezione(
                festivalId,
                id,
                salaId,
                proiezione
        );

        return "redirect:/admin/festivals/"
                + festivalId
                + "/proiezioni";
    }

    @PostMapping("/admin/festivals/{festivalId}/proiezioni/{id}/delete")
    public String eliminaProiezione(@PathVariable Long festivalId,
                                    @PathVariable Long id) {

        proiezioneService.deleteProiezione(festivalId, id);

        return "redirect:/admin/festivals/"
                + festivalId
                + "/proiezioni";
    }

    private void aggiungiDatiForm(Long festivalId, Model model) {

        model.addAttribute(
                "festival",
                festivalService.getFestival(festivalId)
        );

        model.addAttribute(
                "sale",
                salaService.getAllSale()
        );
    }
}