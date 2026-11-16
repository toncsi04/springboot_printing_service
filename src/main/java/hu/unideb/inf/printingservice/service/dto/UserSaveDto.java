package hu.unideb.inf.printingservice.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserSaveDto {

    private long Id;

    @NotBlank
    @Size(min = 3, max = 100)
    private String felhasznalonev;

    @NotBlank
    @Size(min = 6)
    private String jelszo;

    @NotBlank
    @Email
    private String email;
}