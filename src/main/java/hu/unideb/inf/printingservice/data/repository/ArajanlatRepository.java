package hu.unideb.inf.printingservice.data.repository;

import hu.unideb.inf.printingservice.data.entity.ArajanlatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArajanlatRepository extends JpaRepository<ArajanlatEntity, Long> {
}
