package hu.unideb.inf.printingservice.service.impl;

import hu.unideb.inf.printingservice.data.entity.UserEntity;
import hu.unideb.inf.printingservice.data.repository.JogRepository;
import hu.unideb.inf.printingservice.data.repository.UserRepository;
import hu.unideb.inf.printingservice.service.UserService;
import hu.unideb.inf.printingservice.service.dto.UserDisplayDto;
import hu.unideb.inf.printingservice.service.dto.UserSaveDto;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JogRepository jogRepository;

    @Override
    public UserDisplayDto save(UserSaveDto dto) {
        UserEntity entity = new UserEntity();

        entity.setFelhasznalonev(dto.getFelhasznalonev());
        entity.setJelszo(passwordEncoder.encode(dto.getJelszo()));
        entity.setEmail(dto.getEmail());

        jogRepository.findByNev("USER").ifPresent(userJog ->
                entity.setJogosultsagok(Set.of(userJog))
        );

        // Külön változóba mentjük (vagy rögtön visszaadjuk), így az 'entity' effectively final marad!
        UserEntity savedEntity = userRepository.save(entity);
        return mapToDisplayDto(savedEntity);
    }

    @Override
    public List<UserDisplayDto> findAll() {
        return userRepository.findAll().stream()
                .map(this::mapToDisplayDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDisplayDto findById(Long id) {
        UserEntity entity = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Felhasználó nem található!"));
        return mapToDisplayDto(entity);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    // Segédmetódus az átalakításhoz, hogy ne ismételjük a kódot
    private UserDisplayDto mapToDisplayDto(UserEntity entity) {
        UserDisplayDto displayDto = new UserDisplayDto();
        displayDto.setId(entity.getId());
        displayDto.setFelhasznalonev(entity.getFelhasznalonev());
        displayDto.setEmail(entity.getEmail());
        return displayDto;
    }
}