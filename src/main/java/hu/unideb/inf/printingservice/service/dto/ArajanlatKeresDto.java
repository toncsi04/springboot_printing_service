package hu.unideb.inf.printingservice.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ArajanlatKeresDto {

    @NotNull
    private Long materialId;

    @NotNull
    @Positive
    private Long sulyGrammban;
}
