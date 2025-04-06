package clients;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.courier.Courier;
import models.courier.CourierCredentials;

import static io.restassured.RestAssured.given;

public class CourierClient {
    private final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    public CourierClient() {
        RestAssured.baseURI = BASE_URL;
    }

    @Step("Создание курьера с данными: login = {courier.getLogin()}, firstName = {courier.getFirstName()}")
    public Response createCourier(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Логин курьера с данными: login = {credentials.getLogin()}")
    public Response loginCourier(CourierCredentials credentials) {
        return given()
                .header("Content-type", "application/json")
                .body(credentials)
                .when()
                .post("/api/v1/courier/login");
    }
}
