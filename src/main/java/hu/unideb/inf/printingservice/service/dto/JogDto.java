package hu.unideb.inf.printingservice.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JogDto {
    private Long id;

    @NotBlank
    private String nev;

    private String leiras;
}
