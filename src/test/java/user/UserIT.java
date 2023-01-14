package user;

import com.google.gson.JsonObject;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import utility.ConfigFileReader;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class UserIT {

    static ConfigFileReader configReader = new ConfigFileReader();

    static String token;

    static String BASE_URL_USER = configReader.getBaseUrlUser();

    static String BASE_URL_LOGIN = configReader.getBaseUrlLogin();

    static String email;

    @BeforeClass
    static void setUp() {

        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("email", "jellofirsthand@gaa1iler.site");
        jsonObj.addProperty("password", "qqqqq111");

        token = given().
                contentType(ContentType.JSON)
                .header("Content-Type", "application/json")
                .body(jsonObj.toString()).when().post(BASE_URL_LOGIN)
                .then().statusCode(200).extract().header("Authorization");

        email = generateEmail();

        JsonObject newUser = new JsonObject();
        newUser.addProperty("firstName", "test_firstName");
        newUser.addProperty("lastName", "test_lastName");
        newUser.addProperty("password", "qwerty1234");
        newUser.addProperty("phoneNumber", "0742000000");
        newUser.addProperty("email", email);
        newUser.addProperty("deliveryAddress", "street, no. 1");

        given().contentType(ContentType.JSON)
                .body(newUser.toString())
                .when()
                .post(BASE_URL_USER)
                .then()
                .statusCode(201);
    }


   @Test
    void testAddUser() {

        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("firstName", "test_firstName");
        jsonObj.addProperty("lastName", "test_lastName");
        jsonObj.addProperty("password", "qwerty1234");
        jsonObj.addProperty("phoneNumber", "0742000000");
        jsonObj.addProperty("email", generateEmail());
        jsonObj.addProperty("deliveryAddress", "street, no. 1");

        given().contentType(ContentType.JSON)
                .body(jsonObj.toString())
                .when()
                .post(BASE_URL_USER)
                .then()
                .assertThat().body("firstName", is("test_firstName"))
                .and().body("lastName", is("test_lastName"))
                .and().body("phoneNumber", is("0742000000"))
                .and().body("deliveryAddress", is("street, no. 1"))
                .and().statusCode(201);
    }

    @Test
    void testDeleteUserByByEmail(){

        given()
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .pathParam("email", email)
                .when()
                .delete(BASE_URL_USER   + "{email}")
                .then()
                .assertThat().statusCode(200);

    }

    @Test
    @Parameters({"email"})
    void testGetUserByEmail(String email) {
        given().header("Authorization", token)
                .queryParam("email", email)
                .when().get(BASE_URL_USER)
                .then().assertThat().statusCode(200);
    }

    private static String generateEmail() {
        return  RandomStringUtils.randomAlphanumeric(25) + "@gmail.com";
    }
}
