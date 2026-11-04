package hu.unideb.inf.printingservice.data.repository;

import hu.unideb.inf.printingservice.data.entity.JogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JogRepository extends JpaRepository<JogEntity, Long> {
}
