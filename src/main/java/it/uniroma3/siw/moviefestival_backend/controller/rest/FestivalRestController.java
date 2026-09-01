package it.uniroma3.siw.moviefestival_backend.controller.rest;

import it.uniroma3.siw.moviefestival_backend.dto.FestivalDetailsDto;
import it.uniroma3.siw.moviefestival_backend.service.FestivalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/festivals")
public class FestivalRestController {
    @Autowired
    FestivalService festivalService;

    @GetMapping("/{id}")
    public FestivalDetailsDto getFestival(@PathVariable Long id) {
        return festivalService.getFestivalDetails(id);
    }
}
