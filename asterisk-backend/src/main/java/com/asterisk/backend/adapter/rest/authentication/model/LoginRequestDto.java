package com.asterisk.backend.adapter.rest.authentication.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record LoginRequestDto(@NotNull @NotBlank @Size(max = 255) @Email String email,
                              @NotNull @NotBlank @Size(max = 64) String password) {
}
