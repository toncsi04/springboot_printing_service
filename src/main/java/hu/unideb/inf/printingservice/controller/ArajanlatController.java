package hu.unideb.inf.printingservice.controller;

import hu.unideb.inf.printingservice.data.entity.ArajanlatEntity;
import hu.unideb.inf.printingservice.data.entity.UserEntity;
import hu.unideb.inf.printingservice.data.repository.ArajanlatRepository;
import hu.unideb.inf.printingservice.data.repository.UserRepository;
import hu.unideb.inf.printingservice.service.ArajanlatService;
import hu.unideb.inf.printingservice.service.dto.ArajanlatKeresDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quotes")
@AllArgsConstructor
public class ArajanlatController {

    private final ArajanlatService arajanlatService;
    private final UserRepository userRepository;
    private final ArajanlatRepository arajanlatRepository;

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
    @GetMapping
    public List<ArajanlatEntity> getAllQuotes() {
        return arajanlatRepository.findAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuote(@PathVariable Long id) {
        arajanlatRepository.deleteById(id);
    }

    // Saját árajánlatok lekérése (bejelentkezett USER / ADMIN)
    @GetMapping("/my")
    public List<ArajanlatEntity> getMyQuotes(Authentication authentication) {
        return arajanlatService.getSajatArajanlatok(authentication.getName());
    }

    // Saját árajánlat módosítása (újraszámolja az árat is!)
    @PutMapping("/my/{id}")
    public int updateMyQuote(
            @PathVariable Long id,
            @Valid @RequestBody ArajanlatKeresDto request,
            Authentication authentication) {
        return arajanlatService.updateSajatArajanlat(
                id,
                authentication.getName(),
                request.getMaterialId(),
                request.getSulyGrammban()
        );
    }

    // Saját árajánlat törlése
    @DeleteMapping("/my/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMyQuote(@PathVariable Long id, Authentication authentication) {
        arajanlatService.deleteSajatArajanlat(id, authentication.getName());
    }
}

