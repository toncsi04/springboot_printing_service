package hu.unideb.inf.printingservice.controller;

import hu.unideb.inf.printingservice.service.JogService;
import hu.unideb.inf.printingservice.service.dto.JogDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jogok")
@AllArgsConstructor
public class JogController {

    private final JogService jogService;

    @PostMapping
    public JogDto saveJog(@Valid @RequestBody JogDto dto) {
        return jogService.save(dto);
    }

    @GetMapping
    public List<JogDto> getAllJogok() {
        return jogService.findAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteJog(@PathVariable Long id) {
        jogService.delete(id);
    }
}