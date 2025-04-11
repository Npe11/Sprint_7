import clients.OrderClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.notNullValue;

public class OrderListTest {

    private OrderClient orderClient;

    @Before
    @Step("Инициализация клиента заказов")
    public void setUp() {
        orderClient = new OrderClient();
    }

    @After
    @Step("Обнуление данных клиента заказов")
    public void tearDown() {
        orderClient = null;
    }

    @Test
    @Description("Проверка, что тело ответа содержит список заказов")
    public void testGetOrdersReturnsList() {
        Response response = orderClient.getOrders();
        verifyStatusCode(response, 200);
        verifyFieldExists(response, "orders");
    }

    @Step("Проверка, что код ответа равен {1}")
    private void verifyStatusCode(Response response, int expectedStatusCode) {
        response.then().statusCode(expectedStatusCode);
    }

    @Step("Проверка, что в ответе присутствует поле {field}")
    private void verifyFieldExists(Response response, String field) {
        response.then().body(field, notNullValue());
    }
}
