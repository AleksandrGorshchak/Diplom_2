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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class LoginUserTests {

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
    @DisplayName("Если поля email и пароль заполнены корректно, то пользователь умпешно авторизовывается")
    public void successfulAuthorization() {

        ValidatableResponse createResponse = userClient.create(user);
        assertThat(createResponse.extract().statusCode(), equalTo(200));
        ValidatableResponse response = userClient.login(new UserCredentials(user.email, user.password));
        int statusCodeResponse = response.extract().statusCode();
        assertThat(statusCodeResponse, equalTo(200));
        boolean isUserLogged = response.extract().path("success");
        assertTrue("Пользователь не авторизовался", isUserLogged);
        accessToken = response.extract().path("accessToken");
        assertNotNull(accessToken);
        String refreshToken = response.extract().path("refreshToken");
        assertNotNull(refreshToken);
        String actualEmail = response.extract().path("user.email");
        assertThat("Пользователь авторизовался под другим email", actualEmail, equalTo(user.email));
        String actualName = response.extract().path("user.name");
        assertThat("Пользователь авторизовался под другим name", actualName, equalTo(user.name));
    }

    @Test
    @DisplayName("Пользователь не может успешно авторизоваться, если поля email и пароль заполнены некорретно")
    public void unsuccessfulAuthorizationOfUserWithIncorrectEmailAndPassword() {

        ValidatableResponse response = userClient.login(UserCredentials.getWithDoNotReallyEmailAndPassword(user));
        int statusCodeResponse = response.extract().statusCode();
        boolean isUserUnLogged = response.extract().path("success");
        String message = response.extract().path("message");
        assertThat(statusCodeResponse, equalTo(401));
        assertFalse(isUserUnLogged);
        assertThat(message, equalTo("email or password are incorrect"));
    }
}
