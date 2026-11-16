package hu.unideb.inf.printingservice.service.impl;

import hu.unideb.inf.printingservice.data.entity.JogEntity;
import hu.unideb.inf.printingservice.data.repository.JogRepository;
import hu.unideb.inf.printingservice.service.JogService;
import hu.unideb.inf.printingservice.service.dto.JogDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class JogServiceImpl implements JogService {

    private final JogRepository jogRepository;

    @Override
    public JogDto save(JogDto dto) {
        JogEntity jogEntity = new JogEntity();
        jogEntity.setNev(dto.getNev());
        jogEntity.setLeiras(dto.getLeiras());

        jogEntity = jogRepository.save(jogEntity);
        dto.setId(jogEntity.getId());
        return dto;
    }

    @Override
    public List<JogDto> findAll() {
        return jogRepository.findAll().stream().map(entity -> {
            JogDto dto = new JogDto();
            dto.setId(entity.getId());
            dto.setNev(entity.getNev());
            dto.setLeiras(entity.getLeiras());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        jogRepository.deleteById(id);
    }
}
