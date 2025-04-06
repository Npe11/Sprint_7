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
    @Step("Очистка тестовых данных: обнуление объектов, созданных в setUp")
    public void tearDown() {
        courierClient = null;
        testCourier = null;
    }

    @Test
    @Description("Проверка успешного логина курьера: возвращается статус 200 и id")
    public void testSuccessfulCourierLogin() {
        CourierCredentials credentials = new CourierCredentials(testCourier.getLogin(), testCourier.getPassword());
        Response response = courierClient.loginCourier(credentials);
        verifyStatusCode(response, 200);
        verifyFieldExists(response, "id");
    }

    @Test
    @Description("Проверка логина без обязательного поля: возвращается 400 и сообщение об ошибке")
    public void testLoginWithoutLogin() {
        CourierCredentials credentials = new CourierCredentials(null, testCourier.getPassword());
        Response response = courierClient.loginCourier(credentials);
        verifyStatusCode(response, 400);
        verifyResponseBody(response, "message", "Недостаточно данных для входа");
    }

    @Test
    @Description("Проверка логина с некорректными данными: возвращается 404 и сообщение 'Учетная запись не найдена'")
    public void testLoginWithIncorrectCredentials() {
        // Попытка логина с неверным паролем
        CourierCredentials wrongPasswordCredentials = new CourierCredentials(testCourier.getLogin(), "wrongPassword");
        Response responseWrongPassword = courierClient.loginCourier(wrongPasswordCredentials);
        verifyStatusCode(responseWrongPassword, 404);
        verifyResponseBody(responseWrongPassword, "message", "Учетная запись не найдена");

        // Попытка логина с неверным логином
        CourierCredentials wrongLoginCredentials = new CourierCredentials("nonexistentLogin", testCourier.getPassword());
        Response responseWrongLogin = courierClient.loginCourier(wrongLoginCredentials);
        verifyStatusCode(responseWrongLogin, 404);
        verifyResponseBody(responseWrongLogin, "message", "Учетная запись не найдена");
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
