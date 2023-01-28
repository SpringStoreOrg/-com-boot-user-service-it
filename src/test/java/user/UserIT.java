package user;

import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.*;
import utility.ConfigFileReader;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class UserIT {

    static ConfigFileReader configReader = new ConfigFileReader();

    static String token;

    static String userEmail;

    @BeforeClass
    @Parameters({"email", "password"})
    static void setUp(String email, String password) {

        //Set up the base URI
        RestAssured.baseURI = configReader.getBaseUrlUser();

        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("email", email);
        jsonObj.addProperty("password", password);

        //Create a new token based on the email and password of an existing user
        token = given().
                contentType(ContentType.JSON)
                .header("Content-Type", "application/json")
                .body(jsonObj.toString()).when().post(configReader.getBaseUrlLogin())
                .then().statusCode(200).extract().header("Authorization");
    }


   @Test(priority=1)
    void testAddUser() {

       userEmail = generateEmail();

        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("firstName", "test_firstName");
        jsonObj.addProperty("lastName", "test_lastName");
        jsonObj.addProperty("password", "qwerty1234");
        jsonObj.addProperty("phoneNumber", "0742000000");
        jsonObj.addProperty("email", userEmail);
        jsonObj.addProperty("deliveryAddress", "street, no. 1");

        given().contentType(ContentType.JSON)
                .body(jsonObj.toString())
                .when()
                .post()
                .then()
                .assertThat().body("firstName", is("test_firstName"))
                .and().body("lastName", is("test_lastName"))
                .and().body("phoneNumber", is("0742000000"))
                .and().body("deliveryAddress", is("street, no. 1"))
                .and().statusCode(201);
    }

    @Test
    @Parameters({"email"})
    void testGetUserByEmail(String email) {

        given().header("Authorization", token)
                .queryParam("email", email)
                .when().get()
                .then().assertThat().statusCode(200);
    }


    @Test(priority=2)
    void testDeleteUserByEmail(){

        given().contentType(ContentType.JSON)
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .pathParam("email", userEmail)
                .when()
                .delete( "{email}")
                .then()
                .assertThat().statusCode(200);
    }

    private static String generateEmail() {
        return  RandomStringUtils.randomAlphanumeric(25) + "@springstore-test.com";
    }
}
