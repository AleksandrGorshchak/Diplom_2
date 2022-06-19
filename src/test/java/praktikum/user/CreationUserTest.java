package praktikum.user;

import praktikum.info.InfoForCreateNewUser;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.client.UserClient;
import praktikum.info.UserCredentials;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class CreationUserTest {

    private UserClient userClient;
    private InfoForCreateNewUser user;
    private String accessToken;

    @Before
    public void setUp() {

        userClient = new UserClient();
        user = InfoForCreateNewUser.getRandom();
    }

    @After
    public void tearDown() {
        userClient.delete(accessToken);
    }

    @Test
    @DisplayName("Пользователь создается успешно, если все обязательные поля заполнены")
    public void successfulCreateUserWhenAllRequiredFieldsIsFill() {

        ValidatableResponse response = userClient.create(user);
        int statusCodePositiveResponseCreate = response.extract().statusCode();
        assertThat(statusCodePositiveResponseCreate, equalTo(200));
        boolean isUserCreated = response.extract().path("success");
        assertTrue("User is not created", isUserCreated);
        ValidatableResponse responseUserLogged = userClient.login(new UserCredentials(user.email, user.password));
        String refreshToken = responseUserLogged.extract().path("refreshToken");
        assertNotNull("Пустой refreshToken", refreshToken);
        accessToken = responseUserLogged.extract().path("accessToken");
        assertNotNull("Пустой accessToken", accessToken);
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых пользователей")
    public void creationTwoIdenticalUsersIsForbidden() {

        ValidatableResponse responseFirstUser = userClient.create(user);
        ValidatableResponse responseSecondUser = userClient.create(user);
        accessToken = responseFirstUser.extract().path("accessToken");
        int statusCodeNegativeResponse = responseSecondUser.extract().statusCode();

        if (statusCodeNegativeResponse != 403) {
            String accessToken2 = responseFirstUser.extract().path("accessToken");
            userClient.delete(accessToken2);
        }

        assertThat(statusCodeNegativeResponse, equalTo(403));
        boolean isSuccess = responseSecondUser.extract().path("success");
        assertFalse(isSuccess);
        String message = responseSecondUser.extract().path("message");
        assertThat("Создан ещё один пользователь с одинаковыми данными", message, (equalTo("User already exists")));
    }
}
