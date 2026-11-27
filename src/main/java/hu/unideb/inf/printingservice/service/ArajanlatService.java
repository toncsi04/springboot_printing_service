package hu.unideb.inf.printingservice.service;

import hu.unideb.inf.printingservice.data.entity.ArajanlatEntity;

import java.util.List;

public interface ArajanlatService {
    int ArajanlatKalkulacioEsMentes(Long id,Long materialId, Long sulyGrammban);
    List<ArajanlatEntity> getSajatArajanlatok(String felhasznalonev);
    int updateSajatArajanlat(Long quoteId, String felhasznalonev, Long materialId, Long ujSulyGrammban);
    void deleteSajatArajanlat(Long quoteId, String felhasznalonev);
}
