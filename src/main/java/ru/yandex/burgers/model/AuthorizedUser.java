package ru.yandex.burgers.model;

public class AuthorizedUser {

    private final User user;
    private final String accessToken;
    private final String refreshToken;

    public AuthorizedUser(User user, String accessToken, String refreshToken){
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public User getUser() {
        return user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
