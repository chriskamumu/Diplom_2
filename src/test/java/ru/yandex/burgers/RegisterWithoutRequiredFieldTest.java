package ru.yandex.burgers;

import io.restassured.response.ValidatableResponse;
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

    private String email;
    private String password;
    private String name;

    public RegisterWithoutRequiredFieldTest(String email, String pass, String name) {
        this.email = email;
        this.password = pass;
        this.name = name;
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
    public void testRegisterWithoutRequiredField(){
        User user = new User(email, password, name);
        AuthClient authClient = new AuthClient();
        ValidatableResponse responseOfRegister = authClient.register(user);
        responseOfRegister
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
