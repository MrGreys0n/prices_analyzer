package ru.greyson.prices_analyzer.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemDTO {
    @NotEmpty
    @NotNull
    private String name;

    @NotEmpty
    @NotNull
    private String price;

    @NotEmpty
    @NotNull
    private String shop;
}
