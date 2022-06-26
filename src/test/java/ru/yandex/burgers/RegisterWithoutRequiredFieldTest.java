package ru.yandex.burgers;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.burgers.client.AuthClient;
import ru.yandex.burgers.model.User;
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

    public RegisterWithoutRequiredFieldTest(String email, String pass, String name) {
        this.email = email;
        this.password = pass;
        this.name = name;
    }

    @Before
    public void setUp() {
        authClient = new AuthClient();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getTestData(){
        return Arrays.asList(new Object[][]{
                {null, "pass", "name"},
                {"email", null, "name"},
                {"email", "pass", null}
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
    }
}
