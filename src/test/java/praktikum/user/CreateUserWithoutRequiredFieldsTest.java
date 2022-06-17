package praktikum.user;

import org.junit.After;
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
    private String accessToken;

    public CreateUserWithoutRequiredFieldsTest(InfoForCreateNewUser user) {
        this.user = user;
        this.expectedStatus = 403;
        this.expectedSuccess = false;
        this.expectedErrorTextMessage = "Email, password and name are required fields";
    }

    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return new Object[][]{
                {InfoForCreateNewUser.getWithEmailAndPassword()},
                {InfoForCreateNewUser.getWithPasswordAndName()},
                {InfoForCreateNewUser.getWithEmailAndName()}
        };
    }

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        userClient.delete(accessToken);
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
        accessToken = response.extract().path("accessToken");
        assertThat(statusCode, equalTo(expectedStatus));
        boolean actualSuccess = response.extract().path("success");
        assertThat(actualSuccess, equalTo(expectedSuccess));
        String errorTextMessage = response.extract().path("message");
        assertThat(errorTextMessage, equalTo(expectedErrorTextMessage));
    }
}
