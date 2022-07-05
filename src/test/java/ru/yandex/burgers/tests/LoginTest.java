package ru.yandex.burgers.tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.burgers.client.AuthClient;
import ru.yandex.burgers.model.User;
import ru.yandex.burgers.model.UserCredentials;
import ru.yandex.burgers.utils.UserUtils;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;

public class LoginTest {
    private AuthClient authClient;
    private String accessToken;
    private User user;

    @Before
    public void setUp() {
        authClient = new AuthClient();

        user = UserUtils.buildRandom();
        accessToken = authClient.register(user).assertThat().statusCode(SC_OK).extract().path("accessToken");
    }

    @After
    public void tearDown() {
        if (!accessToken.equals("")) {
            authClient.delete(accessToken).assertThat().statusCode(SC_ACCEPTED);
            accessToken = "";
        }
    }

    @Test
    @DisplayName("check login under existing user")
    public void testLoginUnderExistingUserReturnsSuccessTrue(){
        ValidatableResponse responseOfLogin = authClient.login(new UserCredentials(user.getEmail(), user.getPassword()));
        responseOfLogin.assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));

    }

    @Test
    @DisplayName("check login with incorrect email")
    public void testLoginWithIncorrectEmailReturnsSuccessFalse(){
        ValidatableResponse responseOfLogin = authClient.login(new UserCredentials(UserUtils.getRandomEmail(), user.getPassword()));
        responseOfLogin.assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("check login with incorrect password")
    public void testLoginWithIncorrectPasswordReturnsSuccessFalse(){


        ValidatableResponse responseOfLogin = authClient.login(new UserCredentials(user.getEmail(), "random_password"));
        responseOfLogin.assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

}
