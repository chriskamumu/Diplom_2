package ru.yandex.burgers.model;

import lombok.Data;

@Data
public class User {

    private final String email;
    private final String password;
    private final String name;

}
