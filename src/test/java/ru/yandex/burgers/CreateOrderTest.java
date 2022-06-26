package ru.yandex.burgers;

import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.burgers.client.AuthClient;
import ru.yandex.burgers.client.IngredientsClient;
import ru.yandex.burgers.client.OrdersClient;
import ru.yandex.burgers.model.Order;
import ru.yandex.burgers.model.User;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;

public class CreateOrderTest {

    private AuthClient authClient;
    private OrdersClient ordersClient;
    private List<String> ingredientsList;

    private String accessToken = "";

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
    public void testCreateOrderByAuthorizedUserWithIngredientsReturnsSuccessTrue(){
        Order order = new Order(List.of(ingredientsList.get(0), ingredientsList.get(1)));
        ValidatableResponse responseOfOrderCreating = ordersClient.create(order, accessToken);
        responseOfOrderCreating.assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue())
                .body("order.ingredients", notNullValue())
                .body("order._id", notNullValue())
                .body("order.owner", notNullValue())
                .body("order.status", notNullValue())
                .body("order.createdAt", notNullValue())
                .body("order.updatedAt", notNullValue())
                .body("order.price", notNullValue());
    }

    @Test
    public void testCreateOrderByAuthorizedUserWithoutIngredientsReturnsSuccessFalse(){
        Order order = new Order(List.of());
        ValidatableResponse responseOfCreating = ordersClient.create(order, accessToken);
        responseOfCreating.assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    public void testCreateOrderByAuthorizedUserWithNonexistentIngredientsReturnsSuccessFalse(){
        Order order = new Order(List.of("6497837493"));
        ValidatableResponse responseOfCreating = ordersClient.create(order, accessToken);
        responseOfCreating.assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testCreateOrderWithoutAuthorizationWithIngredientsReturnsSuccessTrue(){
        Order order = new Order(List.of(ingredientsList.get(0), ingredientsList.get(1)));
        ValidatableResponse responseOfOrderCreating = ordersClient.create(order, "");
        responseOfOrderCreating.assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue())
                .body("order.ingredients", nullValue())
                .body("order._id", nullValue())
                .body("order.owner", nullValue())
                .body("order.status", nullValue())
                .body("order.createdAt", nullValue())
                .body("order.updatedAt", nullValue())
                .body("order.price", nullValue());
    }

    @Test
    public void testCreateOrderWithoutAuthorizationWithoutIngredientsReturnsSuccessFalse(){
        Order order = new Order(List.of());
        ValidatableResponse responseOfCreating = ordersClient.create(order, "");
        responseOfCreating.assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    public void testCreateOrderWithoutAuthorizationWithNonexistentIngredientsReturnsSuccessFalse(){
        Order order = new Order(List.of("6497837493"));
        ValidatableResponse responseOfCreating = ordersClient.create(order, "");
        responseOfCreating.assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}
