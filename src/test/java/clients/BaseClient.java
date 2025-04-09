package clients;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public class BaseClient {

    public static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    public static RequestSpecification spec = RestAssured.given()
            .baseUri(BASE_URL)
            .header("Content-type", "application/json");
}
