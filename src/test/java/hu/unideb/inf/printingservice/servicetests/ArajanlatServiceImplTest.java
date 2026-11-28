package hu.unideb.inf.printingservice.servicetests;

import hu.unideb.inf.printingservice.data.entity.ArajanlatEntity;
import hu.unideb.inf.printingservice.data.entity.MaterialEntity;
import hu.unideb.inf.printingservice.data.entity.UserEntity;
import hu.unideb.inf.printingservice.data.repository.ArajanlatRepository;
import hu.unideb.inf.printingservice.data.repository.MaterialRepository;
import hu.unideb.inf.printingservice.data.repository.UserRepository;
import hu.unideb.inf.printingservice.service.impl.ArajanlatServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Bekapcsolja a Mockito-t a JUnit 5 teszthez
@ExtendWith(MockitoExtension.class)
class ArajanlatServiceImplTest {

    @Mock
    private ArajanlatRepository arajanlatRepository;

    @Mock
    private MaterialRepository materialRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ArajanlatServiceImpl arajanlatService;

    @Test
    void ArajanlatKalkulacioEsMentes_sikeresSzamitas() {
        // --- ARRANGE (Előkészítés: tesztadatok és a bábuk betanítása) ---
        UserEntity tesztUser = new UserEntity();
        tesztUser.setId(1L);

        MaterialEntity tesztMaterial = new MaterialEntity();
        tesztMaterial.setId(2L);
        tesztMaterial.setGrammAr(10.0); // 10 Ft / gramm

        // Megmondjuk a bábuknak, mit adjanak vissza, ha a Service kérdezi őket
        when(userRepository.findById(1L)).thenReturn(Optional.of(tesztUser));
        when(materialRepository.findById(2L)).thenReturn(Optional.of(tesztMaterial));

        // --- ACT (Művelet: meghívjuk a valódi üzleti logikát 100 gramm súllyal) ---
        // Képlet: ((100 * 10.0) + 1500) * 1.5 = (1000 + 1500) * 1.5 = 2500 * 1.5 = 3750
        int eredmeny = arajanlatService.ArajanlatKalkulacioEsMentes(1L, 2L, 100L);

        // --- ASSERT (Ellenőrzés: jó lett-e a matek, és elmentette-e?) ---
        assertEquals(3750, eredmeny);

        // Ellenőrizzük, hogy a mentés metódus pontosan 1-szer lefutott-e
        verify(arajanlatRepository, times(1)).save(any(ArajanlatEntity.class));
    }

    @Test
    void ArajanlatKalkulacioEsMentes_nemLetezoUserEsetenHibatDob() {
        // --- ARRANGE: A bábu üres Optional-t ad vissza (mintha nem lenne ilyen user) ---
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT & ASSERT: Ellenőrizzük, hogy tényleg RuntimeException-t dob-e ---
        assertThrows(RuntimeException.class, () -> {
            arajanlatService.ArajanlatKalkulacioEsMentes(99L, 2L, 100L);
        });

        // Mivel hibára futott, az adatbázisba mentésnek (save) SOHA nem szabad lefutnia!
        verify(arajanlatRepository, never()).save(any());
    }

    @Test
    void updateSajatArajanlat_sikeresModositasEsUjraszamolas() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setFelhasznalonev("tesztuser");

        MaterialEntity ujMaterial = new MaterialEntity();
        ujMaterial.setId(2L);
        ujMaterial.setGrammAr(20.0);

        ArajanlatEntity meglevoQuote = new ArajanlatEntity();
        meglevoQuote.setId(10L);
        meglevoQuote.setUser(user);

        when(arajanlatRepository.findByIdAndUser_Felhasznalonev(10L, "tesztuser"))
                .thenReturn(Optional.of(meglevoQuote));
        when(materialRepository.findById(2L)).thenReturn(Optional.of(ujMaterial));

        // Képlet: ((50 * 20.0) + 1500) * 1.5 = 2500 * 1.5 = 3750
        int ujAr = arajanlatService.updateSajatArajanlat(10L, "tesztuser", 2L, 50L);

        assertEquals(3750, ujAr);
        verify(arajanlatRepository, times(1)).save(meglevoQuote);
    }

    @Test
    void updateSajatArajanlat_masFelhasznaloAjanlataraHibatDob() {
        // A "tamado" nem találja meg a 10L azonosítójú ajánlatot, mert nem az övé
        when(arajanlatRepository.findByIdAndUser_Felhasznalonev(10L, "tamado"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            arajanlatService.updateSajatArajanlat(10L, "tamado", 2L, 50L);
        });

        verify(arajanlatRepository, never()).save(any());
    }
}