package hu.unideb.inf.printingservice.service;

import hu.unideb.inf.printingservice.service.dto.UserDisplayDto;
import hu.unideb.inf.printingservice.service.dto.UserSaveDto;

import java.util.List;

public interface UserService {
    UserDisplayDto save(UserSaveDto dto);
    List<UserDisplayDto> findAll();
    UserDisplayDto findById(Long id);
    void delete(Long id);
}