package ru.yandex.burgers.model;

import lombok.Data;

@Data
public class UserCredentials {

    private final String email;
    private final String password;
}
