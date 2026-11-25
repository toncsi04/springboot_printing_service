package hu.unideb.inf.printingservice.security;

import hu.unideb.inf.printingservice.data.entity.JogEntity;
import hu.unideb.inf.printingservice.data.entity.UserEntity;
import hu.unideb.inf.printingservice.data.repository.JogRepository;
import hu.unideb.inf.printingservice.data.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final JogRepository jogRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Alapértelmezett jogok létrehozása, ha még nincsenek az adatbázisban
        JogEntity adminJog = jogRepository.findByNev("ADMIN").orElseGet(() -> {
            JogEntity jog = new JogEntity();
            jog.setNev("ADMIN");
            jog.setLeiras("Rendszergazdai jogosultság");
            return jogRepository.save(jog);
        });

        JogEntity userJog = jogRepository.findByNev("USER").orElseGet(() -> {
            JogEntity jog = new JogEntity();
            jog.setNev("USER");
            jog.setLeiras("Általános felhasználói jogosultság");
            return jogRepository.save(jog);
        });

        // Teszt admin felhasználó létrehozása (felhasznalonev: admin, jelszo: admin123)
        if (userRepository.findByFelhasznalonev("admin").isEmpty()) {
            UserEntity adminUser = new UserEntity();
            adminUser.setFelhasznalonev("admin");
            adminUser.setJelszo(passwordEncoder.encode("admin123"));
            adminUser.setEmail("admin@printingservice.hu");
            adminUser.setJogosultsagok(Set.of(adminJog, userJog));
            userRepository.save(adminUser);
        }
    }
}