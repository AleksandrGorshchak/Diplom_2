package praktikum.user;

import praktikum.info.InfoForCreateNewUser;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import praktikum.client.UserClient;


import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class CreateUserWithoutRequiredFieldsTest {

    InfoForCreateNewUser user;
    private UserClient userClient;
    private final int expectedStatus;
    private final boolean expectedSuccess;
    private final String expectedErrorTextMessage;

    public CreateUserWithoutRequiredFieldsTest(InfoForCreateNewUser user, int expectedStatus, boolean expectedSuccess, String expectedErrorTextMessage) {
        this.user = user;
        this.expectedStatus = expectedStatus;
        this.expectedSuccess = expectedSuccess;
        this.expectedErrorTextMessage = expectedErrorTextMessage;
    }

    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return new Object[][]{
                {InfoForCreateNewUser.getWithEmailAndPassword(), 403, false, "Email, password and name are required fields"},
                {InfoForCreateNewUser.getWithPasswordAndName(), 403, false, "Email, password and name are required fields"},
                {InfoForCreateNewUser.getWithEmailAndName(), 403, false, "Email, password and name are required fields"}
        };
    }

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @Test
    @DisplayName("Создание пользователя без заполнения одного из обязательных полей")
    @Description("Создание юзера только с: " +
            "1. Email и пароль " +
            "2. Пароль и имя пользователя " +
            "3. Email и имя пользователя")
    public void createUsersWithoutOneRequiredField() {

        ValidatableResponse response = userClient.create(user);
        int statusCode = response.extract().statusCode();
        assertThat(statusCode, equalTo(expectedStatus));
        boolean actualSuccess = response.extract().path("success");
        assertThat(actualSuccess, equalTo(expectedSuccess));
        String errorTextMessage = response.extract().path("message");
        assertThat(errorTextMessage, equalTo(expectedErrorTextMessage));
    }
}
