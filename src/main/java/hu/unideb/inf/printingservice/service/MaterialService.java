package hu.unideb.inf.printingservice.service;

import hu.unideb.inf.printingservice.service.dto.MaterialSaveDto;
import java.util.List;

public interface MaterialService {
    MaterialSaveDto save(MaterialSaveDto dto);
    List<MaterialSaveDto> findAll();
    void delete(Long id);
}