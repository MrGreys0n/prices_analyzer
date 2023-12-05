package ru.greyson.prices_analyzer.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationDTO {
    @NotEmpty
    @NotNull
    private String login;

    @NotNull
    @NotEmpty
    private String password;
}