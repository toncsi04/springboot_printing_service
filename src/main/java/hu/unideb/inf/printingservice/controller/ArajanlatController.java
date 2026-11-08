package hu.unideb.inf.printingservice.controller;

import hu.unideb.inf.printingservice.service.ArajanlatService;
import hu.unideb.inf.printingservice.service.dto.ArajanlatKeresDto;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quotes")
@AllArgsConstructor
public class ArajanlatController {

    private final ArajanlatService arajanlatService;

    // A @PostMapping jelzi, hogy ide új adatokat (árajánlat kérést) küldünk be
    @PostMapping("/calculate")
    public int calculateArajanlat(@RequestBody ArajanlatKeresDto request) {

        // Service impl-ben lévő függvény hívása a dto adatokra
        return arajanlatService.ArajanlatKalkulacioEsMentes(
                request.getUserId(),
                request.getMaterialId(),
                request.getSulygrammban()
        );
    }
}