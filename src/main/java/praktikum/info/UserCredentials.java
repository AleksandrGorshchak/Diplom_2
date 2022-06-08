package praktikum.info;

import com.github.javafaker.Faker;

public class UserCredentials {

    public String email;
    public String password;

    public UserCredentials() {

    }

    public UserCredentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserCredentials setEmail(String email) {
        this.email = email;
        return this;
    }

    public UserCredentials setPassword(String password) {
        this.password = password;
        return this;
    }

    public static UserCredentials getWithDoNotReallyEmailAndPassword(InfoForCreateNewUser user) {
        Faker faker = new Faker();
        return new UserCredentials().setEmail(faker.internet().emailAddress())
                .setPassword(faker.internet().password(6,10));
    }
}