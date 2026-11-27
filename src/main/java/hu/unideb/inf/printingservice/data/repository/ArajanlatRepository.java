package hu.unideb.inf.printingservice.data.repository;

import hu.unideb.inf.printingservice.data.entity.ArajanlatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArajanlatRepository extends JpaRepository<ArajanlatEntity, Long> {
    List<ArajanlatEntity> findAllByUser_Felhasznalonev(String felhasznalonev);
    Optional<ArajanlatEntity> findByIdAndUser_Felhasznalonev(Long id, String felhasznalonev);
}
