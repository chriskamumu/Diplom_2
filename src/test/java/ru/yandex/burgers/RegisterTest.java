package ru.yandex.burgers;


import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.burgers.client.AuthClient;
import ru.yandex.burgers.model.User;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;

public class RegisterTest {

    private AuthClient authClient;
    private String accessToken;

    @Before
    public void setUp() {
        authClient = new AuthClient();
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            authClient.delete(accessToken).assertThat().statusCode(SC_ACCEPTED);
            accessToken = null;
        }

    }

    @Test
    public void testRegisterUserWithAllFieldsReturnsSuccessTrue(){
        User user = new User("testkr3@mail.ru", "pass", "kristina");

        ValidatableResponse responseOfRefister = authClient.register(user);
        responseOfRefister
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());

        this.accessToken = responseOfRefister.extract().path("accessToken");
    }

    @Test
    public void testRegisterUserThatAlreadyExistsReturnsSuccessFalse(){
        User user = new User("testkr3@mail.ru", "pass", "kristina");

        this.accessToken = authClient.register(user).statusCode(SC_OK).extract().path("accessToken");

        ValidatableResponse responseOfRefister = authClient.register(user);
        responseOfRefister
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }


}
