package hu.unideb.inf.printingservice.controller;

import hu.unideb.inf.printingservice.data.entity.JogEntity;
import hu.unideb.inf.printingservice.data.entity.MaterialEntity;
import hu.unideb.inf.printingservice.data.entity.UserEntity;
import hu.unideb.inf.printingservice.data.repository.JogRepository;
import hu.unideb.inf.printingservice.data.repository.MaterialRepository;
import hu.unideb.inf.printingservice.data.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/init")
@AllArgsConstructor
public class InitController {

    private final UserRepository userRepository;
    private final MaterialRepository materialRepository;
    private final JogRepository jogRepository;

    @GetMapping
    public String initData() {
        // 1. Jog létrehozása
        JogEntity jog = new JogEntity();
        jog.setNev("USER");
        jog.setLeiras("Alapértelmezett felhasználó");
        jog = jogRepository.save(jog);

        // 2. Felhasználó létrehozása a beleptetorendszer logikája alapján[cite: 6]
        UserEntity user = new UserEntity();
        user.setFelhasznalonev("tesztuser");
        user.setEmail("teszt@mail.com");
        user.setJelszo("titkos123");
        user.setJogosultsagok(Set.of(jog));
        userRepository.save(user); // Ennek lesz az ID-ja 1

        // 3. Anyag létrehozása
        MaterialEntity material = new MaterialEntity();
        material.setNev("PLA");
        material.setGrammAr(15.5);
        materialRepository.save(material); // Ennek is 1 lesz az ID-ja

        return "Tesztadatok sikeresen feltöltve az adatbázisba!";
    }
}