package ru.yandex.burgers.client;

import io.restassured.response.ValidatableResponse;
import ru.yandex.burgers.model.AuthorizedUser;
import ru.yandex.burgers.model.User;
import ru.yandex.burgers.model.UserCredentials;

import static io.restassured.RestAssured.given;

public class AuthClient extends AbstractRestAssuredClient {

    private final static String AUTH_PATH = "/api/auth";

    public ValidatableResponse register(User user){
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .log().all()
                .post(AUTH_PATH + "/register")
                .then()
                .log().all();
    }

    public ValidatableResponse login(UserCredentials userCredentials){
        return given()
                .spec(getBaseSpec())
                .body(userCredentials)
                .when()
                .log().all()
                .post(AUTH_PATH + "/login")
                .then()
                .log().all();
    }

    public ValidatableResponse delete(AuthorizedUser authorizedUser){
        return given()
                .spec(getBaseSpec())
                .header("Authorization", authorizedUser.getAccessToken())
                .when()
                .log().all()
                .delete(AUTH_PATH + "/user")
                .then()
                .log().all();
    }

    public ValidatableResponse edit(AuthorizedUser authorizedUser, User updatedUser){
        return given()
                .spec(getBaseSpec())
                .header("Authorization", authorizedUser.getAccessToken())
                .body(updatedUser)
                .when()
                .log().all()
                .patch(AUTH_PATH + "/user")
                .then()
                .log().all();
    }

}
