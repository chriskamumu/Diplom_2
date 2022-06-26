package ru.yandex.burgers;

import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.burgers.client.AuthClient;
import ru.yandex.burgers.model.AuthorizedUser;
import ru.yandex.burgers.model.User;
import ru.yandex.burgers.model.UserCredentials;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class EditUserTest {

    private AuthClient authClient;
    private AuthorizedUser removalCandidate;

    @Before
    public void setUp() {
        authClient = new AuthClient();
    }

    @After
    public void tearDown() {
        if (removalCandidate != null) {
            authClient.delete(removalCandidate).assertThat().statusCode(SC_ACCEPTED);
            removalCandidate = null;
        }
    }

    @Test
    public void testEditLoginAndNameByAuthorizedUserReturnsSuccessTrue(){
        User user = new User("test_kr2@mail.ru", "pass", "name");
        AuthorizedUser authorizedUser = new AuthorizedUser(user, authClient.register(user).extract().path("accessToken"), "");

        User updatedUser = new User("updated_kr@mail.ru", user.getPassword(), "updated_Name");
        ValidatableResponse responseOfEditing = authClient.edit(authorizedUser, updatedUser);
        responseOfEditing.assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(updatedUser.getEmail()))
                .body("user.name", equalTo(updatedUser.getName()));

        removalCandidate = authorizedUser;
    }

    @Test
    public void testEditPasswordByAuthorizedUserReturnsSuccessTrue(){
        User user = new User("test_kr2@mail.ru", "pass", "name");
        AuthorizedUser authorizedUser = new AuthorizedUser(user, authClient.register(user).extract().path("accessToken"), "");

        User updatedUser = new User(user.getEmail(), "updated_pass", user.getName());
        ValidatableResponse responseOfEditing = authClient.edit(authorizedUser, updatedUser);

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

        removalCandidate = authorizedUser;

    }
}