package ru.yandex.burgers;

import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.burgers.client.AuthClient;
import ru.yandex.burgers.client.IngredientsClient;
import ru.yandex.burgers.client.OrdersClient;
import ru.yandex.burgers.model.Order;
import ru.yandex.burgers.model.User;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class GetUserOrdersTest {

    private AuthClient authClient;
    private OrdersClient ordersClient;
    private List<String> ingredientsList;
    private String accessToken;

    @Before
    public void setUp() {
        authClient = new AuthClient();
        ordersClient = new OrdersClient();
        IngredientsClient ingredientsClient = new IngredientsClient();
        ingredientsList = ingredientsClient.get().extract().path("data._id");
        accessToken = authClient.register(new User("krkr1219_rand@mail.ru", "pass", "name")).extract().path("accessToken");
    }

    @After
    public void tearDown() {
        if (!accessToken.equals("")) {
            authClient.delete(accessToken).assertThat().statusCode(SC_ACCEPTED);
            accessToken = "";
        }
    }

    @Test
    public void testGetUserOrdersByAuthorizedUserReturnsAccessTrue() {
        String orderId = ordersClient.create(new Order(List.of(ingredientsList.get(0))), accessToken).extract().path("order._id");
        ValidatableResponse responseOfGettingOrders = ordersClient.getUserOrders(accessToken);
        responseOfGettingOrders.assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("orders", notNullValue());
        List<String> ordersIdes = responseOfGettingOrders.extract().path("orders._id");
        Assert.assertTrue(ordersIdes.contains(orderId));

    }

    @Test
    public void testGetUserOrdersWithoutAuthorizationReturnsAccessFalse() {
        ValidatableResponse responseOfGettingOrders = ordersClient.getUserOrders("");
        responseOfGettingOrders.assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("message", equalTo("You should be authorised"));

    }
}
