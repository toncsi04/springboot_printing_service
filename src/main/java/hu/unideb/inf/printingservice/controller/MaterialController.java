package hu.unideb.inf.printingservice.controller;

import hu.unideb.inf.printingservice.service.MaterialService;
import hu.unideb.inf.printingservice.service.dto.MaterialPriceUpdateDto;
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

    @PostMapping
    public MaterialSaveDto saveMaterial(@Valid @RequestBody MaterialSaveDto dto) {
        return materialService.save(dto);
    }

    @GetMapping
    public List<MaterialSaveDto> getAllMaterials() {
        return materialService.findAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMaterial(@PathVariable Long id) {
        materialService.delete(id);
    }

    @PatchMapping("/{id}/price")
    public MaterialSaveDto updateMaterialPrice(
            @PathVariable Long id,
            @Valid @RequestBody MaterialPriceUpdateDto dto) {
        return materialService.updatePrice(id, dto.getGrammAr());
    }
}