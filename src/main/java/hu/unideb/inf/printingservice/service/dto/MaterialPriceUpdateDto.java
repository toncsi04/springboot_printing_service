package hu.unideb.inf.printingservice.service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MaterialPriceUpdateDto {

    @NotNull(message = "A grammár megadása kötelező!")
    @DecimalMin(value = "0.1", message = "Az árnak pozitívnak kell lennie!")
    private Double grammAr;
}