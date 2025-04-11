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
import static org.hamcrest.Matchers.notNullValue;

public class CourierLoginTest {

    private CourierClient courierClient;
    private Courier testCourier; // Курьер, зарегистрированный для успешного логина

    @Before
    @Step("Подготовка тестовых данных: регистрация уникального курьера")
    public void setUp() {
        courierClient = new CourierClient();
        // Генерация уникального логина для курьера
        String uniqueLogin = "courier" + System.currentTimeMillis();
        testCourier = new Courier(uniqueLogin, "1234", "TestName");
        // Регистрируем курьера для последующего успешного логина
        courierClient.createCourier(testCourier);
    }

    @After
    @Step("Очистка тестовых данных: удаление созданного курьера и обнуление объектов")
    public void tearDown() {
        if (testCourier != null) {
            // Выполняем логин с корректными данными, чтобы получить ID созданного курьера
            Response loginResponse = courierClient.loginCourier(new CourierCredentials(testCourier.getLogin(), testCourier.getPassword()));
            if (loginResponse.getStatusCode() == 200) {
                int courierId = loginResponse.jsonPath().getInt("id");
                if (courierId != 0) {
                    courierClient.deleteCourier(courierId);
                }
            }
        }
        courierClient = null;
        testCourier = null;
    }

    @Test
    @Description("Проверка успешного логина курьера: возвращается статус 200 и поле id")
    public void testSuccessfulCourierLogin() {
        CourierCredentials credentials = new CourierCredentials(testCourier.getLogin(), testCourier.getPassword());
        Response response = courierClient.loginCourier(credentials);
        verifyStatusCode(response, 200);
        verifyFieldExists(response, "id");
    }

    @Test
    @Description("Проверка логина без обязательного поля (логин): возвращается 400 и сообщение 'Недостаточно данных для входа'")
    public void testLoginWithoutLogin() {
        CourierCredentials credentials = new CourierCredentials("", testCourier.getPassword());
        Response response = courierClient.loginCourier(credentials);
        verifyStatusCode(response, 400);
        verifyResponseBody(response, "message", "Недостаточно данных для входа");
    }

    @Test
    @Description("Проверка логина без обязательного поля (пароль): возвращается 400 и сообщение 'Недостаточно данных для входа'")
    public void testLoginWithoutPassword() {
        CourierCredentials credentials = new CourierCredentials(testCourier.getLogin(), "");
        Response response = courierClient.loginCourier(credentials);
        verifyStatusCode(response, 400);
        verifyResponseBody(response, "message", "Недостаточно данных для входа");
    }

    @Test
    @Description("Проверка логина с некорректными данными - неверный пароль: возвращается 404 и сообщение 'Учетная запись не найдена'")
    public void testLoginWithIncorrectPassword() {
        CourierCredentials credentials = new CourierCredentials(testCourier.getLogin(), "wrongPassword");
        Response response = courierClient.loginCourier(credentials);
        verifyStatusCode(response, 404);
        verifyResponseBody(response, "message", "Учетная запись не найдена");
    }

    @Test
    @Description("Проверка логина с некорректными данными - неверный логин: возвращается 404 и сообщение 'Учетная запись не найдена'")
    public void testLoginWithIncorrectLogin() {
        CourierCredentials credentials = new CourierCredentials("nonexistentLogin", testCourier.getPassword());
        Response response = courierClient.loginCourier(credentials);
        verifyStatusCode(response, 404);
        verifyResponseBody(response, "message", "Учетная запись не найдена");
    }

    @Step("Проверка, что код ответа равен {1}")
    private void verifyStatusCode(Response response, int expectedStatusCode) {
        response.then().statusCode(expectedStatusCode);
    }

    @Step("Проверка, что значение поля {field} равно {expected}")
    private void verifyResponseBody(Response response, String field, Object expected) {
        response.then().body(field, equalTo(expected));
    }

    @Step("Проверка, что поле {field} присутствует в ответе")
    private void verifyFieldExists(Response response, String field) {
        response.then().body(field, notNullValue());
    }
}
