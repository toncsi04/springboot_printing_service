package hu.unideb.inf.printingservice.service.impl;

import hu.unideb.inf.printingservice.data.entity.ArajanlatEntity;
import hu.unideb.inf.printingservice.data.entity.MaterialEntity;
import hu.unideb.inf.printingservice.data.entity.UserEntity;
import hu.unideb.inf.printingservice.data.repository.ArajanlatRepository;
import hu.unideb.inf.printingservice.data.repository.MaterialRepository;
import hu.unideb.inf.printingservice.data.repository.UserRepository;
import hu.unideb.inf.printingservice.service.ArajanlatService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ArajanlatServiceImpl implements ArajanlatService {
    private final ArajanlatRepository arajanlatRepository;
    private final MaterialRepository materialRepository;
    private final UserRepository userRepository;

    // Bónusz költségek a számítások kiegészítéséhez
    private static final int FIX_ALAPDIJ = 1500;
    private static final double HASZONKULCS = 1.5;

    @Transactional
    @Override
    public int ArajanlatKalkulacioEsMentes(Long userId, Long materialId, Long sulyGrammban) {

        // Adatok lekérése a Repository-kon keresztül az adatbázisból
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Felhasználó nem található!"));

        MaterialEntity material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Anyag nem található!"));

        // A MATEK: Kiszámoljuk az árat (Súly * Grammonkénti ár + Fix költség) * Haszonkulcs
        double nyersAr = (sulyGrammban * material.getGrammAr()) + FIX_ALAPDIJ;
        int vegsoAr = (int) Math.round(nyersAr * HASZONKULCS);

        // Entity összerakása mentéshez
        ArajanlatEntity ujArajanlat = new ArajanlatEntity();
        ujArajanlat.setUser(user);
        ujArajanlat.setMaterial(material);
        ujArajanlat.setSuly(sulyGrammban);
        ujArajanlat.setAr(vegsoAr);

        // Mentés az adatbázisba
        arajanlatRepository.save(ujArajanlat);

        return vegsoAr;
    }
}
