package hu.unideb.inf.printingservice.service.dto;

import lombok.Data;

@Data
public class UserDisplayDto {
    private Long id;
    private String felhasznalonev;
    private String email;
}
