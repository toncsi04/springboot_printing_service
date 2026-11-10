package hu.unideb.inf.printingservice.service.impl;

import hu.unideb.inf.printingservice.data.entity.MaterialEntity;
import hu.unideb.inf.printingservice.data.repository.MaterialRepository;
import hu.unideb.inf.printingservice.service.MaterialService;
import hu.unideb.inf.printingservice.service.dto.MaterialSaveDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;

    @Override
    public MaterialSaveDto save(MaterialSaveDto dto) {
        MaterialEntity entity = new MaterialEntity();

        // Ha van ID, akkor frissítés történik (Update), ha nincs, akkor új felvétel (Create)
        if (dto.getId() != null) {
            entity = materialRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Anyag nem található!"));
        }

        entity.setNev(dto.getNev());
        entity.setGrammAr(dto.getGrammAr());

        entity = materialRepository.save(entity);

        dto.setId(entity.getId());
        return dto;
    }

    @Override
    public List<MaterialSaveDto> findAll() {
        return materialRepository.findAll().stream().map(entity -> {
            MaterialSaveDto dto = new MaterialSaveDto();
            dto.setId(entity.getId());
            dto.setNev(entity.getNev());
            dto.setGrammAr(entity.getGrammAr());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        materialRepository.deleteById(id);
    }
}