package ru.yandex.burgers.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class IngredientsClient extends AbstractRestAssuredClient{
    private final static String INGREDIENTS_PATH = "/api/ingredients";

    @Step("Getting of ingredients list by sending GET request to /api/ingredients")
    public ValidatableResponse get(){
        return given()
                .spec(getBaseSpec())
                .when()
                .log().all()
                .get(INGREDIENTS_PATH)
                .then()
                .log().all();
    }
}
