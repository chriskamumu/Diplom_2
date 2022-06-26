package ru.yandex.burgers.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.burgers.model.User;
import ru.yandex.burgers.model.UserCredentials;

import static io.restassured.RestAssured.given;

public class AuthClient extends AbstractRestAssuredClient {

    private final static String AUTH_PATH = "/api/auth";

    @Step("User registration by sending POST request to /api/auth/register")
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

    @Step("User login by sending POST request to /api/auth/login")
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

    @Step("User deleting by sending DELETE request to /api/auth/user")
    public ValidatableResponse delete(String accessToken){
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .when()
                .log().all()
                .delete(AUTH_PATH + "/user")
                .then()
                .log().all();
    }

    @Step("User editing by sending PATCH request to /api/auth/user")
    public ValidatableResponse edit(String accessToken, User updatedUser){
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .body(updatedUser)
                .when()
                .log().all()
                .patch(AUTH_PATH + "/user")
                .then()
                .log().all();
    }

}
