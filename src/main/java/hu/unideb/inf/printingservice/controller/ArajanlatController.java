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

    // Itt csináltam egy függvényt ami bekéri a dto adatait
    @PostMapping("/calculate")
    public int calculateArajanlat(@RequestBody ArajanlatKeresDto request) {

        // Service impl-ben lévő függvény hívása a dto adatokra
        int eredmeny = arajanlatService.ArajanlatKalkulacioEsMentes(
                request.getUserId(),
                request.getMaterialId(),
                request.getSulygrammban()
        );
        System.out.println(eredmeny);
        return eredmeny;
    }
}