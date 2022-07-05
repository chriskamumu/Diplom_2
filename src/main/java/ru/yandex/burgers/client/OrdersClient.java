package ru.yandex.burgers.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.burgers.model.Order;

import static io.restassured.RestAssured.given;

public class OrdersClient extends AbstractRestAssuredClient{

    private final static String ORDERS_PATH = "/api/orders";

    @Step("Order creation by sending POST request to /api/orders")
    public ValidatableResponse create(Order order, String accessToken){
        return given()
                .spec(getBaseSpec())
                .body(order)
                .header("Authorization", accessToken)
                .when()
                .log().all()
                .post(ORDERS_PATH)
                .then()
                .log().all();

    }

    @Step("Getting user orders by sending GET request to /api/orders")
    public ValidatableResponse getUserOrders(String accessToken){
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .when()
                .log().all()
                .get(ORDERS_PATH)
                .then()
                .log().all();

    }


}
