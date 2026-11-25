package hu.unideb.inf.printingservice.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MaterialSaveDto {

    private Long id;

    @NotBlank
    private String nev;

    @NotNull
    @Positive
    private Double grammAr;
}
