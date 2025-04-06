package clients;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.order.Order;

import static io.restassured.RestAssured.given;

public class OrderClient {
    private final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    public OrderClient() {
        RestAssured.baseURI = BASE_URL;
    }

    @Step("Создание заказа с данными: firstName = {order.getFirstName()}, lastName = {order.getLastName()}, color = {order.getColor()}")
    public Response createOrder(Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post("/api/v1/orders");
    }

    @Step("Получение списка заказов")
    public Response getOrders() {
        return given()
                .header("Content-type", "application/json")
                .when()
                .get("/api/v1/orders");
    }
}
