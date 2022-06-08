package praktikum.info;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.javafaker.Faker;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InfoForCreateNewUser {

    public String email;
    public String password;
    public String name;

    public InfoForCreateNewUser() {

    }

    public InfoForCreateNewUser(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public static InfoForCreateNewUser getRandom() {
        Faker faker = new Faker();

        final String emailUser = faker.internet().emailAddress();
        final String passwordUser = faker.internet().password(6, 10);
        final String nameUser = faker.name().firstName();
        return new InfoForCreateNewUser(emailUser, passwordUser, nameUser);
    }

    public static InfoForCreateNewUser getWithPasswordAndName() {
        Faker faker = new Faker();

        return new InfoForCreateNewUser().setPassword(faker.internet().password(6, 10))
                .setName(faker.name().firstName());
    }

    public static InfoForCreateNewUser getWithEmailAndName() {
        Faker faker = new Faker();
        return new InfoForCreateNewUser().setEmail(faker.internet().emailAddress())
                .setName(faker.name().firstName());
    }

    public static InfoForCreateNewUser getWithEmailAndPassword() {
        Faker faker = new Faker();
        return new InfoForCreateNewUser().setEmail(faker.internet().emailAddress())
                .setPassword(faker.internet().password(6, 10));
    }

    public static InfoForCreateNewUser getEmail() {
        Faker faker = new Faker();
        return new InfoForCreateNewUser().setEmail(faker.internet().emailAddress());
    }

    public static InfoForCreateNewUser getPassword() {
        Faker faker = new Faker();
        return new InfoForCreateNewUser().setPassword(faker.internet().password(6,10));
    }

    public static InfoForCreateNewUser getName() {
        Faker faker = new Faker();
        return new InfoForCreateNewUser().setName(faker.name().firstName());
    }

    public InfoForCreateNewUser setEmail (String email) {
        this.email = email;
        return this;
    }

    public InfoForCreateNewUser setPassword (String password) {
        this.password = password;
        return this;
    }

    public InfoForCreateNewUser setName (String name) {
        this.name = name;
        return this;
    }
}