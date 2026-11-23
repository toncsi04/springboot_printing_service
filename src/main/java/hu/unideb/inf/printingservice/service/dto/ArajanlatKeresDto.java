package hu.unideb.inf.printingservice.service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ArajanlatKeresDto {

    @NotNull
    private Long materialId;

    @NotNull
    private Long sulyGrammban;
}
