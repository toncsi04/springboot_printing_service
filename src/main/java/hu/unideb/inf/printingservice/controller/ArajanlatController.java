package hu.unideb.inf.printingservice.controller;

import hu.unideb.inf.printingservice.data.entity.UserEntity;
import hu.unideb.inf.printingservice.data.repository.UserRepository;
import hu.unideb.inf.printingservice.service.ArajanlatService;
import hu.unideb.inf.printingservice.service.dto.ArajanlatKeresDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quotes")
@AllArgsConstructor
public class ArajanlatController {

    private final ArajanlatService arajanlatService;
    private final UserRepository userRepository;

    // Itt csináltam egy függvényt ami bekéri a dto adatait
    @PostMapping("/calculate")
    public int calculateArajanlat(@Valid @RequestBody ArajanlatKeresDto request, Authentication authentication) {
        UserEntity currentUser = userRepository.findByFelhasznalonev(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Felhasználó nem található!"));

        return arajanlatService.ArajanlatKalkulacioEsMentes(
                currentUser.getId(),
                request.getMaterialId(),
                request.getSulyGrammban()
        );
    }
}