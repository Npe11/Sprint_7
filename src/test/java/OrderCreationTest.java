import clients.OrderClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.order.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreationTest {

    private OrderClient orderClient;
    private Order testOrder;
    private int orderTrack = 0;

    // Параметры для тестирования цвета
    private String testCaseDescription;
    private String[] colors;

    public OrderCreationTest(String testCaseDescription, String[] colors) {
        this.testCaseDescription = testCaseDescription;
        this.colors = colors;
    }

    @Parameterized.Parameters(name = "{index}: {0} with colors {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"Один цвет BLACK", new String[] {"BLACK"}},
                {"Один цвет GREY", new String[] {"GREY"}},
                {"Оба цвета", new String[] {"BLACK", "GREY"}},
                {"Без цвета", null}
        });
    }

    @Before
    @Step("Подготовка тестовых данных: создание заказа с параметрами: {0}")
    public void setUp() {
        orderClient = new OrderClient();
        // Создаём заказ с фиксированными значениями и параметризованным полем цвета
        testOrder = new Order(
                "Naruto",
                "Uchiha",
                "Konoha, 142 apt.",
                4,
                "+7 800 355 35 35",
                5,
                "2020-06-06",
                "Saske, come back to Konoha",
                colors
        );
    }

    @After
    @Step("Очистка тестовых данных: отмена созданного заказа и обнуление объектов")
    public void tearDown() {
        if (orderTrack != 0) {
            orderClient.cancelOrder(orderTrack);
        }

        orderClient = null;
        testOrder = null;
        orderTrack = 0;
    }

    @Test
    @Description("Проверка создания заказа: успешный запрос возвращает статус 201 и тело ответа содержит track")
    public void testOrderCreation() {
        Response response = orderClient.createOrder(testOrder);
        verifyStatusCode(response, 201);
        verifyFieldExists(response, "track");

        orderTrack = response.jsonPath().getInt("track");
    }

    @Step("Проверка, что код ответа равен {1}")
    private void verifyStatusCode(Response response, int expectedStatusCode) {
        response.then().statusCode(expectedStatusCode);
    }

    @Step("Проверка, что поле {field} присутствует в ответе")
    private void verifyFieldExists(Response response, String field) {
        response.then().body(field, notNullValue());
    }
}
