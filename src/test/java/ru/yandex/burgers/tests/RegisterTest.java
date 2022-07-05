package ru.yandex.burgers.tests;


import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.burgers.client.AuthClient;
import ru.yandex.burgers.model.User;
import ru.yandex.burgers.utils.UserUtils;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;

public class RegisterTest {

    private AuthClient authClient;
    private List<String> accessTokens;

    @Before
    public void setUp() {
        authClient = new AuthClient();
        accessTokens = new ArrayList<>();
    }

    @After
    public void tearDown() {
        if (!accessTokens.isEmpty()) {
            for (String accessToken :
                    accessTokens) {
                authClient.delete(accessToken).assertThat().statusCode(SC_ACCEPTED);
            }
        }
    }

    @Test
    @DisplayName("check user registration with all required fields")
    public void testRegisterUserWithAllFieldsReturnsSuccessTrue(){
        User user = UserUtils.buildRandom();
        ValidatableResponse responseOfRefister = authClient.register(user);

        responseOfRefister
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());

        accessTokens.add(responseOfRefister.extract().path("accessToken"));
    }

    @Test
    @DisplayName("check registration of user that already exists")
    public void testRegisterUserThatAlreadyExistsReturnsSuccessFalse(){
        User user = UserUtils.buildRandom();
        accessTokens.add(authClient.register(user).statusCode(SC_OK).extract().path("accessToken"));

        ValidatableResponse responseOfRegister = authClient.register(user);
        responseOfRegister
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
        if  (responseOfRegister.extract().path("accessToken") != null) {
            accessTokens.add(responseOfRegister.extract().path("accessToken"));
        }
    }


}
