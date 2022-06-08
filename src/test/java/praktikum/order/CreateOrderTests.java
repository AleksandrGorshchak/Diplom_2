package praktikum.order;

import praktikum.client.OrderClient;
import praktikum.client.UserClient;
import praktikum.info.InfoForCreateNewUser;
import praktikum.info.ListOfIngredientsForCreateNewBurger;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class CreateOrderTests {

    String accessToken;
    private UserClient userClient;
    private InfoForCreateNewUser user;
    private ListOfIngredientsForCreateNewBurger ingredientsForCreateNewBurger;
    private OrderClient orderClient;

    @Before
    public void setUp() {

        userClient = new UserClient();
        user = InfoForCreateNewUser.getRandom();
        orderClient = new OrderClient();
        ingredientsForCreateNewBurger = ListOfIngredientsForCreateNewBurger.getRandom();
    }

    @After
    public void tearDown() {

        if (accessToken != "") {
            userClient.delete(accessToken);
        }
    }

    @Test
    @DisplayName("Успешное создание заказа с ингредиентами и без авторизации")
    public void successfulCreationOrderWithIngredientsAndWithoutLogin() {

        accessToken = "";
        ValidatableResponse responseOrder = orderClient.createOrder(ingredientsForCreateNewBurger, accessToken);
        int statusCodeResponseOrder = responseOrder.extract().statusCode();
        assertThat(statusCodeResponseOrder, equalTo(200));
        boolean isOrderCreated = responseOrder.extract().path("success");
        assertTrue("Order is not created", isOrderCreated);
        int orderNumber = responseOrder.extract().path("order.number");
        assertNotNull("Пустой номер заказа", orderNumber);

    }

    @Test
    @DisplayName("Успешное создание заказа с ингредиентами и авторизацией")
    public void successfulCreationOrderWithIngredientsAndWithLogin() {

        ValidatableResponse response = userClient.create(user);
        accessToken = response.extract().path("accessToken");
        ValidatableResponse responseOrder = orderClient.createOrder(ingredientsForCreateNewBurger, accessToken);
        int statusCodeResponseOrder = responseOrder.extract().statusCode();
        boolean isOrderCreated = responseOrder.extract().path("success");
        int orderNumber = responseOrder.extract().path("order.number");
        assertThat(statusCodeResponseOrder, equalTo(200));
        assertTrue("Order is not created", isOrderCreated);
        assertNotNull("Пустой номе заказа", orderNumber);
    }

    @Test
    @DisplayName("Заказ не создается с неверным хешем ингредиентов")
    public void orderNotCreationWithIncorrectHashIngredientsAndWithLogin() {

        ValidatableResponse response = userClient.create(user);
        accessToken = response.extract().path("accessToken");
        ValidatableResponse responseOrder = orderClient.createOrder(ListOfIngredientsForCreateNewBurger.getNotReallyIngredients(), accessToken);
        int statusCodeResponseOrder = responseOrder.extract().statusCode();
        assertThat(statusCodeResponseOrder, equalTo(500));
    }

    @Test
    @DisplayName("Заказ не создается без ингредиентов")
    public void orderNotCreationWithoutIngredientsAndWithLogin() {

        ValidatableResponse response = userClient.create(user);
        accessToken = response.extract().path("accessToken");
        ValidatableResponse responseOrder = orderClient.createOrder(ListOfIngredientsForCreateNewBurger.getWithoutIngredients(), accessToken);
        int statusCodeResponseOrder = responseOrder.extract().statusCode();
        boolean isOrderCreated = responseOrder.extract().path("success");
        String orderMessage = responseOrder.extract().path("message");
        assertThat(statusCodeResponseOrder, equalTo(400));
        assertFalse("Order is created", isOrderCreated);
        assertThat(orderMessage, equalTo("Ingredient ids must be provided"));
    }
}
