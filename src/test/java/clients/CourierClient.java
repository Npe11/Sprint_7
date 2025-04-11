package clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.courier.Courier;
import models.courier.CourierCredentials;
import static clients.BaseClient.spec;

public class CourierClient {

    @Step("Создание курьера с данными: login = {courier.getLogin()}, firstName = {courier.getFirstName()}")
    public Response createCourier(Courier courier) {
        return spec.body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Логин курьера с данными: login = {credentials.getLogin()}")
    public Response loginCourier(CourierCredentials credentials) {
        return spec.body(credentials)
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Удаление курьера с id: {courierId}")
    public Response deleteCourier(int courierId) {
        return spec
                .when()
                .delete("/api/v1/courier/" + courierId);
    }
}
