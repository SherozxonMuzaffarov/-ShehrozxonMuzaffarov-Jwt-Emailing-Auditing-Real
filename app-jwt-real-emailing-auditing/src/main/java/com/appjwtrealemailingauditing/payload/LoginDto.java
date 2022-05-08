package com.appjwtrealemailingauditing.payload;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class LoginDto {

    @NotNull
    @Email
    private String username; //userni emaili(username sifatida ishlatiladi)

    @NotNull
    private String password;
}
