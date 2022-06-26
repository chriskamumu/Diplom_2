package ru.yandex.burgers;

import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.burgers.client.AuthClient;
import ru.yandex.burgers.model.User;
import ru.yandex.burgers.model.UserCredentials;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;

public class LoginTest {
    private AuthClient authClient;
    private String accessToken;

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
    public void testLoginUnderExistingUserReturnsSuccessTrue(){
        User user = new User("user_kr2_rand@mail.ru", "pass", "name");
        authClient.register(user).assertThat().statusCode(SC_OK);

        ValidatableResponse responseOfLogin = authClient.login(new UserCredentials(user.getEmail(), user.getPassword()));
        responseOfLogin.assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));
        accessToken = responseOfLogin.extract().path("accessToken");

    }

    @Test
    public void testLoginWithIncorrectEmailReturnsSuccessFalse(){
        User user = new User("user_kr2_rand@mail.ru", "pass", "name");
        accessToken = authClient.register(user).assertThat().statusCode(SC_OK).extract().path("accessToken");

        ValidatableResponse responseOfLogin = authClient.login(new UserCredentials("Random_kr_2022", user.getPassword()));
        responseOfLogin.assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    public void testLoginWithIncorrectPasswordReturnsSuccessFalse(){
        User user = new User("user_kr2_rand@mail.ru", "pass", "name");
        accessToken = authClient.register(user).assertThat().statusCode(SC_OK).extract().path("accessToken");

        ValidatableResponse responseOfLogin = authClient.login(new UserCredentials(user.getEmail(), "random_password"));
        responseOfLogin.assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

}
