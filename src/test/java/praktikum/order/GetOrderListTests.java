package praktikum.order;

import praktikum.info.InfoForCreateNewUser;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.client.OrderClient;
import praktikum.client.UserClient;
import praktikum.info.ListOfIngredientsForCreateNewBurger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class GetOrderListTests {

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
    @DisplayName("Успешное получение списка заказов с авторизацией")
    public void successfulGetOrdersWithLogin() {

        ValidatableResponse response = userClient.create(user);
        accessToken = response.extract().path("accessToken");
        ValidatableResponse responseOrder = orderClient.userOrderInfo(accessToken);
        int statusCodeResponseOrder = responseOrder.extract().statusCode();
        boolean isGetOrders = responseOrder.extract().path("success");
        assertThat(statusCodeResponseOrder, equalTo(200));
        assertTrue("Заказы не получены", isGetOrders);
    }

    @Test
    @DisplayName("Неавторизованный пользователь не может получить список заказов")
    public void unsuccessfulGetOrdersWithoutLogin() {

        ValidatableResponse responseOrder = orderClient.userOrderInfoWithoutToken();
        int statusCodeResponseOrder = responseOrder.extract().statusCode();
        boolean isGetOrders = responseOrder.extract().path("success");
        String message = responseOrder.extract().path("message");
        assertThat(statusCodeResponseOrder, equalTo(401));
        assertFalse("Операция успешна", isGetOrders);
        assertThat(message, equalTo("You should be authorised"));
    }
}