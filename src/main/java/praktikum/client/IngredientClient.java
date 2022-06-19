package praktikum.client;

import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class IngredientClient extends RestAssuredClient {

    private static final String INGREDIENTS_PATH = "/api/ingredients";

    public ValidatableResponse getIngredients() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(INGREDIENTS_PATH)
                .then()
                .statusCode(200);
    }
}
