package ru.yandex.burgers.client;

import io.restassured.response.ValidatableResponse;
import ru.yandex.burgers.model.Order;

import static io.restassured.RestAssured.given;

public class OrdersClient extends AbstractRestAssuredClient{

    private final static String ORDERS_PATH = "/api/orders";

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
