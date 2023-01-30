package user;

import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.*;
import utility.ConfigFileReader;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

public class UserIT {

    static ConfigFileReader configReader = new ConfigFileReader();

    private static String token;

    private static String email;

    @BeforeClass
    static void setUp() {
        //Set up the base URI
        RestAssured.baseURI = configReader.getBaseUrlUser();

    }


    @Test(priority = 1)
    void testAddUser() {
        //generate random email when creating a new user
        email = generateEmail();

        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("firstName", "test_firstName");
        jsonObj.addProperty("lastName", "test_lastName");
        jsonObj.addProperty("password", "qwerty1234");
        jsonObj.addProperty("phoneNumber", "0742000000");
        jsonObj.addProperty("email", email);
        jsonObj.addProperty("deliveryAddress", "street, no. 1");

        //create a new user
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

        //generate login token for the next tests
        token = given().
                contentType(ContentType.JSON)
                .header("Content-Type", "application/json")
                .body(jsonObj.toString()).when().post(configReader.getBaseUrlLogin())
                .then().statusCode(200).extract().header("Authorization");
    }

    @Test(dependsOnMethods = {"testAddUser"}, priority = 2)
    void testUpdateUserByEmail() {

        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("firstName", "updated_firstName");
        jsonObj.addProperty("lastName", "updated_lastName");
        jsonObj.addProperty("phoneNumber", "0742000002");
        jsonObj.addProperty("deliveryAddress", "street, no. 2");

        given().header("Authorization", token)
                .header("Content-Type", "application/json")
                .pathParam("email", email)
                .body(jsonObj.toString())
                .when()
                .put("{email}")
                .then()
                .assertThat().body("firstName", is("updated_firstName"))
                .and().body("lastName", is("updated_lastName"))
                .and().body("phoneNumber", is("0742000002"))
                .and().body("deliveryAddress", is("street, no. 2"))
                .and().statusCode(200);
    }

    @Test(dependsOnMethods = {"testAddUser"}, priority = 2)
    void testGetUserByEmail() {

        given().header("Authorization", token)
                .queryParam("email", email)
                .when().get()
                .then().assertThat().statusCode(200);
    }

    @Test(dependsOnMethods = {"testAddUser"}, priority = 2)
    void testGetAllUsers() {

        given().header("Authorization", token)
                .when().get("/users")
                .then().assertThat().statusCode(200)
                .and().body("size()", greaterThan(3));
    }

    @Test(dependsOnMethods = {"testAddUser"}, priority = 3)
    void testDeleteUserByEmail() {

        given().contentType(ContentType.JSON)
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .pathParam("email", email)
                .when()
                .delete("{email}")
                .then()
                .assertThat().statusCode(200);
    }

    private static String generateEmail() {
        return RandomStringUtils.randomAlphanumeric(25) + "@springstore-test.com";
    }
}
