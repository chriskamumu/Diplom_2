package ru.yandex.burgers.utils;

import ru.yandex.burgers.model.User;

import java.util.Locale;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class UserUtils {
    private final static String END_OF_EMAIL = "@mail.ru";

    public static User buildRandom() {
        return new User(
                randomAlphabetic(10).toLowerCase(Locale.ROOT) + END_OF_EMAIL,
                randomAlphabetic(10),
                randomAlphabetic(10));
    }

    public static String getRandomEmail(){
        return randomAlphabetic(10).toLowerCase(Locale.ROOT) + END_OF_EMAIL;
    }

    public static User buildRandomEmailAndPassword(String name) {
        final String email = randomAlphabetic(10).toLowerCase(Locale.ROOT) + END_OF_EMAIL;
        final String password = randomAlphabetic(10);

        return new User(email, password, name);
    }

    public static User buildRandomEmailAndName(String password) {
        final String email = randomAlphabetic(10).toLowerCase(Locale.ROOT) + END_OF_EMAIL;
        final String name = randomAlphabetic(10);

        return new User(email, password, name);
    }

    public static User buildRandomPassword(String email, String name) {
        final String password = randomAlphabetic(10);
        return new User(email, password, name);
    }
}
