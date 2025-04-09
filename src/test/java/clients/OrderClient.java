package clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.order.Order;
import static clients.BaseClient.spec;

import java.util.HashMap;
import java.util.Map;

public class OrderClient {

    @Step("Создание заказа с данными: firstName = {order.getFirstName()}, lastName = {order.getLastName()}, color = {order.getColor()}")
    public Response createOrder(Order order) {
        return spec.body(order)
                .when()
                .post("/api/v1/orders");
    }

    @Step("Получение списка заказов")
    public Response getOrders() {
        return spec
                .when()
                .get("/api/v1/orders");
    }

    @Step("Отмена заказа с track: {track}")
    public Response cancelOrder(int track) {
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("track", track);

        return spec.body(requestBody)
                .when()
                .put("/api/v1/orders/cancel");
    }
}
