package praktikum.user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.client.UserClient;
import praktikum.info.InfoForCreateNewUser;
import praktikum.info.UserCredentials;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;

public class ChangingUserInfoTests {

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
    @DisplayName("Успешное изменение всех полей, когда пользователь авторизован")
    public void successfulChangeAllUserFieldsWithLogin() {

        ValidatableResponse responseCreatedUser = userClient.create(user);
        accessToken = responseCreatedUser.extract().path("accessToken");
        InfoForCreateNewUser newUserData = InfoForCreateNewUser.getRandom();
        ValidatableResponse responseChangeData = userClient.changeData(newUserData, accessToken);
        String actualEmail = responseChangeData.extract().path("user.email");
        String actualName = responseChangeData.extract().path("user.name");
        ValidatableResponse responseLoginWithNewData = userClient.login(new UserCredentials(newUserData.email, newUserData.password));
        int statusCodeResponseChangeData = responseChangeData.extract().statusCode();
        int statusCodeResponseLoginWithNewData = responseLoginWithNewData.extract().statusCode();
        assertThat(statusCodeResponseChangeData, equalTo(200));
        assertThat("У пользователя не изменились данные email", actualEmail, equalTo(newUserData.email));
        assertThat("У пользователя не изменились данные name", actualName, equalTo(newUserData.name));
        assertThat(statusCodeResponseLoginWithNewData, equalTo(200));
    }

    @Test
    @DisplayName("Email можно изменить, когда пользователь авторизован")
    public void successfulChangeEmailFieldOfUserWithLogin() {

        ValidatableResponse responseCreatedUser = userClient.create(user);
        accessToken = responseCreatedUser.extract().path("accessToken");
        InfoForCreateNewUser newUserData = InfoForCreateNewUser.getEmail();
        ValidatableResponse responseChangeData = userClient.changeData(newUserData, accessToken);
        String actualEmail = responseChangeData.extract().path("user.email");
        assertThat("У пользователя не изменились данные email", actualEmail, equalTo(newUserData.email));
        String actualName = responseChangeData.extract().path("user.name");
        assertThat("У пользователя не изменились данные name", actualName, equalTo(user.name));
        ValidatableResponse responseLoginWithNewData = userClient.login(new UserCredentials(newUserData.email, user.password));
        int statusCodeResponseChangeData = responseChangeData.extract().statusCode();
        assertThat(statusCodeResponseChangeData, equalTo(200));
        int statusCodeResponseLoginWithNewData = responseLoginWithNewData.extract().statusCode();
        assertThat(statusCodeResponseLoginWithNewData, equalTo(200));
    }

    @Test
    @DisplayName("Пароль можно изменить, когда пользователь авторизован")
    public void successfulChangePasswordFieldOfUserWithLogin() {

        ValidatableResponse responseCreatedUser = userClient.create(user);
        accessToken = responseCreatedUser.extract().path("accessToken");
        InfoForCreateNewUser newUserData = InfoForCreateNewUser.getPassword();
        ValidatableResponse responseChangeData = userClient.changeData(newUserData, accessToken);
        String actualEmail = responseChangeData.extract().path("user.email");
        assertThat("У пользователя не изменились данные email", actualEmail, equalTo(user.email));
        String actualName = responseChangeData.extract().path("user.name");
        assertThat("У пользователя не изменились данные name", actualName, equalTo(user.name));
        ValidatableResponse responseLoginWithNewData = userClient.login(new UserCredentials(user.email, newUserData.password));
        int statusCodeResponseChangeData = responseChangeData.extract().statusCode();
        assertThat(statusCodeResponseChangeData, equalTo(200));
        int statusCodeResponseLoginWithNewData = responseLoginWithNewData.extract().statusCode();
        assertThat(statusCodeResponseLoginWithNewData, equalTo(200));
    }

    @Test
    @DisplayName("Имя пользователя можно изменить, когда он авторизован")
    public void successfulChangeNameFieldOfUserWithLogin() {

        ValidatableResponse responseCreatedUser = userClient.create(user);
        accessToken = responseCreatedUser.extract().path("accessToken");
        InfoForCreateNewUser newUserData = InfoForCreateNewUser.getName();
        ValidatableResponse responseChangeData = userClient.changeData(newUserData, accessToken);
        String actualEmail = responseChangeData.extract().path("user.email");
        assertThat("У пользователя не изменились данные email", actualEmail, equalTo(user.email));
        String actualName = responseChangeData.extract().path("user.name");
        assertThat("У пользователя не изменились данные name", actualName, equalTo(newUserData.name));
        ValidatableResponse responseLoginWithNewData = userClient.login(new UserCredentials(user.email, user.password));
        int statusCodeResponseChangeData = responseChangeData.extract().statusCode();
        assertThat(statusCodeResponseChangeData, equalTo(200));
        int statusCodeResponseLoginWithNewData = responseLoginWithNewData.extract().statusCode();
        assertThat(statusCodeResponseLoginWithNewData, equalTo(200));
    }

    @Test
    @DisplayName("Если пользователь не авторизован, его данные изменить нельзя")
    public void unsuccessfulChangeAllFieldsWithoutLogin() {

        userClient.create(user);
        InfoForCreateNewUser newUserData = InfoForCreateNewUser.getRandom();
        ValidatableResponse responseChangeDataWithoutToken = userClient.changeDataWithoutToken(newUserData);
        boolean isEmail = responseChangeDataWithoutToken.extract().path("success");
        assertFalse(isEmail);
        String message = responseChangeDataWithoutToken.extract().path("message");
        assertThat(message, equalTo("You should be authorised"));
        ValidatableResponse responseLoginWithNewData = userClient.login(new UserCredentials(newUserData.email, newUserData.password));
        int statusCodeResponseChangeData = responseChangeDataWithoutToken.extract().statusCode();
        assertThat(statusCodeResponseChangeData, equalTo(401));
        int statusCodeResponseLoginWithNewData = responseLoginWithNewData.extract().statusCode();
        assertThat(statusCodeResponseLoginWithNewData, equalTo(401));
    }
}