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

import java.util.List;

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
    @Override
    @Transactional(readOnly = true)
    public List<ArajanlatEntity> getSajatArajanlatok(String felhasznalonev) {
        return arajanlatRepository.findAllByUser_Felhasznalonev(felhasznalonev);
    }

    @Override
    @Transactional
    public int updateSajatArajanlat(Long quoteId, String felhasznalonev, Long materialId, Long ujSulyGrammban) {
        // Csak akkor találja meg, ha az övé! Különben hibát dob (BOLA védelem)
        ArajanlatEntity quote = arajanlatRepository.findByIdAndUser_Felhasznalonev(quoteId, felhasznalonev)
                .orElseThrow(() -> new RuntimeException("Az árajánlat nem található vagy nincs jogosultságod módosítani!"));

        MaterialEntity material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Nyomtatóanyag nem található!"));

        int ujAr = (int) Math.round(((ujSulyGrammban * material.getGrammAr()) + 1500) * 1.5);

        quote.setMaterial(material);
        quote.setSuly(ujSulyGrammban);
        quote.setAr(ujAr);
        arajanlatRepository.save(quote);

        return ujAr;
    }

    @Override
    @Transactional
    public void deleteSajatArajanlat(Long quoteId, String felhasznalonev) {
        ArajanlatEntity quote = arajanlatRepository.findByIdAndUser_Felhasznalonev(quoteId, felhasznalonev)
                .orElseThrow(() -> new RuntimeException("Az árajánlat nem található vagy nincs jogosultságod törölni!"));
        arajanlatRepository.delete(quote);
    }

}
