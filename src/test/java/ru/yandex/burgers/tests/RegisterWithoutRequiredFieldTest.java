package ru.yandex.burgers.tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.burgers.client.AuthClient;
import ru.yandex.burgers.model.User;
import ru.yandex.burgers.utils.UserUtils;

import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.hamcrest.CoreMatchers.*;

import java.util.Arrays;
import java.util.Collection;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;

@RunWith(Parameterized.class)
public class RegisterWithoutRequiredFieldTest {

    private final String email;
    private final String password;
    private final String name;

    private AuthClient authClient;
    private String accessToken = "";

    public RegisterWithoutRequiredFieldTest(String email, String pass, String name) {
        this.email = email;
        this.password = pass;
        this.name = name;
    }

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

    @Parameterized.Parameters
    public static Collection<Object[]> getTestData(){
        return Arrays.asList(new Object[][]{
                {null, "pass", "name"},
                {UserUtils.getRandomEmail(), null, "name"},
                {UserUtils.getRandomEmail(), "pass", null}
        });
    }

    @Test
    @DisplayName("check user registration without one of required fields")
    public void testRegisterWithoutRequiredField(){
        User user = new User(email, password, name);
        ValidatableResponse responseOfRegister = authClient.register(user);
        responseOfRegister
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));

        if  (responseOfRegister.extract().path("accessToken") != null) {
            accessToken = responseOfRegister.extract().path("accessToken");
        }
    }

}
