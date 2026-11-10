package hu.unideb.inf.printingservice.controller;

import hu.unideb.inf.printingservice.service.MaterialService;
import hu.unideb.inf.printingservice.service.dto.MaterialSaveDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
@AllArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    // Új anyag felvétele vagy meglévő módosítása (CREATE / UPDATE)
    @PostMapping
    public MaterialSaveDto saveMaterial(@Valid @RequestBody MaterialSaveDto dto) {
        return materialService.save(dto);
    }

    // Összes anyag lekérdezése (READ)
    @GetMapping
    public List<MaterialSaveDto> getAllMaterials() {
        return materialService.findAll();
    }

    // Anyag törlése ID alapján (DELETE)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMaterial(@PathVariable Long id) {
        materialService.delete(id);
    }
}