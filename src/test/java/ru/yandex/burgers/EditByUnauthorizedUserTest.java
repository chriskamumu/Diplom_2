package ru.yandex.burgers;

import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.burgers.client.AuthClient;
import ru.yandex.burgers.model.User;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class EditByUnauthorizedUserTest {

    private final User oldUser;
    private final String newEmail;
    private final String newPassword;
    private final String newName;

    private AuthClient authClient;
    String accessToken = "";

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

    public EditByUnauthorizedUserTest(User oldUser, String email, String pass, String name) {
        this.oldUser = oldUser;
        this.newEmail = email;
        this.newPassword = pass;
        this.newName = name;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getTestData(){
        User user =  new User("random2_kr_email@mail.ru", "pass", "name");
        return Arrays.asList(new Object[][]{
                {user, "updated_email@randomkr.ru", user.getPassword(), user.getName()},
                {user, user.getEmail(), "updated_pass", user.getName()},
                {user, user.getEmail(), user.getPassword(), "updated_name"}
        });
    }

    @Test
    public void testEditByUnauthorizedUser(){
        //получаю accessToken для удаления пользователя и регистрирую пользователя, информацию о котором буду пытаться редактировать
        accessToken = authClient.register(oldUser).assertThat().statusCode(SC_OK).extract().path("accessToken");
        User updated_user = new User(newEmail, newPassword, newName);
        ValidatableResponse responseOfEditing = authClient.edit("", updated_user);
        responseOfEditing.assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
