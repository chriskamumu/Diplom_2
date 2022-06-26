package ru.yandex.burgers;

import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.burgers.client.AuthClient;
import ru.yandex.burgers.model.AuthorizedUser;
import ru.yandex.burgers.model.User;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class EditByUnauthorizedUserTest {

    private final String email;
    private final String password;
    private final String name;
    private User user;
    private AuthClient authClient;

    @Before
    public void setUp() {
        authClient = new AuthClient();
        user = new User("random_kr_email@mail.ru", "pass", "name");
    }

    public EditByUnauthorizedUserTest(String email, String pass, String name) {
        this.email = email;
        this.password = pass;
        this.name = name;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getTestData(){
        return Arrays.asList(new Object[][]{
                {"updated_email@randomkr.ru", "pass", "name"},
                {"random_kr_email@mail.ru", "updated_pass", "name"},
                {"random_kr_email@mail.ru", "pass", "updated_name"}
        });
    }

    @Test
    public void testEditByUnauthorizedUser(){
        User updated_user = new User(email, password, name);
        ValidatableResponse responseOfEditing = authClient.edit(new AuthorizedUser(user, "", ""), updated_user);
        responseOfEditing.assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
