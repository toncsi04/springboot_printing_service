package hu.unideb.inf.printingservice.service;

import hu.unideb.inf.printingservice.service.dto.JogDto;
import java.util.List;

public interface JogService {
    JogDto save(JogDto dto);
    List<JogDto> findAll();
    void delete(Long id);
}