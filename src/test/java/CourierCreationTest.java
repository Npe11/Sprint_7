import clients.CourierClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.courier.Courier;
import models.courier.CourierCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class CourierCreationTest {

    private CourierClient courierClient;
    private Courier testCourier;
    private int courierId = 0;

    @Before
    @Step("Подготовка тестовых данных: создание уникального курьера")
    public void setUp() {
        courierClient = new CourierClient();
        // Генерация уникального логина для курьера
        String uniqueLogin = "courier" + System.currentTimeMillis();
        testCourier = new Courier(uniqueLogin, "1234", "TestName");
    }

    @After
    @Step("Очистка тестовых данных: удаление созданного курьера и обнуление объектов")
    public void tearDown() {
        if (courierId != 0) {
            courierClient.deleteCourier(courierId);
        }

        courierClient = null;
        testCourier = null;
        courierId = 0;
    }

    @Test
    @Description("Проверка успешного создания курьера: код 201 и ok: true")
    public void testSuccessfulCourierCreation() {
        Response createResponse = courierClient.createCourier(testCourier);
        verifyStatusCode(createResponse, 201);
        verifyResponseBody(createResponse, "ok", true);

        // Получаем id созданного курьера через логин для последующего удаления
        Response loginResponse = courierClient.loginCourier(new CourierCredentials(testCourier.getLogin(), testCourier.getPassword()));
        verifyStatusCode(loginResponse, 200);
        courierId = loginResponse.jsonPath().getInt("id");
    }

    @Test
    @Description("Проверка создания курьера без обязательного поля (логин)")
    public void testCourierCreationWithoutLogin() {
        Courier courierWithoutLogin = new Courier(null, "1234", "TestName");
        Response response = courierClient.createCourier(courierWithoutLogin);
        verifyStatusCode(response, 400);
        verifyResponseBody(response, "message", "Недостаточно данных для создания учетной записи");
    }

    @Test
    @Description("Проверка создания курьера без обязательного поля (пароль)")
    public void testCourierCreationWithoutPassword() {
        Courier courierWithoutPassword = new Courier(testCourier.getLogin(), null, "TestName");
        Response response = courierClient.createCourier(courierWithoutPassword);
        verifyStatusCode(response, 400);
        verifyResponseBody(response, "message", "Недостаточно данных для создания учетной записи");
    }

    @Test
    @Description("Проверка невозможности создания курьера с повторяющимся логином")
    public void testDuplicateCourierCreation() {
        // Первый запрос: создаём курьера
        Response response1 = courierClient.createCourier(testCourier);
        verifyStatusCode(response1, 201);
        verifyResponseBody(response1, "ok", true);
        // Получаем id созданного курьера для удаления
        Response loginResponse = courierClient.loginCourier(new CourierCredentials(testCourier.getLogin(), testCourier.getPassword()));
        verifyStatusCode(loginResponse, 200);
        courierId = loginResponse.jsonPath().getInt("id");

        // Второй запрос: пытаемся создать курьера с тем же логином
        Response response2 = courierClient.createCourier(testCourier);
        verifyStatusCode(response2, 409);
        verifyResponseBody(response2, "message", "Этот логин уже используется. Попробуйте другой.");
    }

    @Step("Проверка, что код ответа равен {1}")
    private void verifyStatusCode(Response response, int expectedStatusCode) {
        response.then().statusCode(expectedStatusCode);
    }

    @Step("Проверка, что значение поля {field} равно {expected}")
    private void verifyResponseBody(Response response, String field, Object expected) {
        response.then().body(field, equalTo(expected));
    }
}
