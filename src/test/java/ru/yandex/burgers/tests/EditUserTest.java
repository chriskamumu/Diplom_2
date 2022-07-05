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
import static org.hamcrest.CoreMatchers.equalTo;

public class EditUserTest {

    private AuthClient authClient;
    private String accessToken = "";

    @Before
    public void setUp() {
        authClient = new AuthClient();
    }

    @After
    public void tearDown() {
        if (!accessToken.equals("")) {
            authClient.delete(accessToken).assertThat().statusCode(SC_ACCEPTED);
            accessToken = "";
        }
    }

    @Test
    @DisplayName("check editing of user's login and name by authorized user")
    public void testEditLoginAndNameByAuthorizedUserReturnsSuccessTrue(){
        User user = UserUtils.buildRandom();
        accessToken = authClient.register(user).extract().path("accessToken");

        User updatedUser = UserUtils.buildRandomEmailAndName(user.getPassword());
        ValidatableResponse responseOfEditing = authClient.edit(accessToken, updatedUser);
        responseOfEditing.assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(updatedUser.getEmail()))
                .body("user.name", equalTo(updatedUser.getName()));

    }

    @Test
    @DisplayName("check editing of user's password by authorized user")
    public void testEditPasswordByAuthorizedUserReturnsSuccessTrue(){
        User user = UserUtils.buildRandom();
        accessToken = authClient.register(user).extract().path("accessToken");

        User updatedUser = UserUtils.buildRandomPassword(user.getEmail(), user.getName());
        ValidatableResponse responseOfEditing = authClient.edit(accessToken, updatedUser);

        responseOfEditing.assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));

        //проверяю, что пароль изменился с помощью метода авторизации
        authClient.login(new UserCredentials(updatedUser.getEmail(), updatedUser.getPassword()))
                .assertThat().statusCode(SC_OK);
        authClient.login(new UserCredentials(user.getEmail(), user.getPassword()))
                .assertThat().statusCode(SC_UNAUTHORIZED);


    }
}