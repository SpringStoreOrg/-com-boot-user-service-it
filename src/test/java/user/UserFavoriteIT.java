package user;

import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utility.ConfigFileReader;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class UserFavoriteIT {

    static ConfigFileReader configReader = new ConfigFileReader();

    private static String token;

    private static String email;

    private static long userId;

    @BeforeClass
    static void setUp() {
        //Set up the base URI
        RestAssured.baseURI = configReader.getBaseUrlUserFavorites();

        //create a new user for testing the user favorites endpoints
        createNewUser();
    }

    @Test
    void addProductToUserFavorites() {

        given().header("Authorization", token)
                .header("userId", userId)
                .pathParam("productName", "Yellow core wood chair")
                .when()
                .post("{productName}")
                .then()
                .body("[0].name", is("Yellow core wood chair"))
                .and().statusCode(200);

    }

    @AfterClass
    public void afterClass() {

        //delete the user created for testing
        given().contentType(ContentType.JSON)
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .pathParam("email", email)
                .when()
                .delete(configReader.getBaseUrlUser() + "{email}")
                .then()
                .assertThat().statusCode(200);
    }

    private static void createNewUser() {

        email = RandomStringUtils.randomAlphanumeric(25) + "@springstore-test.com";

        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("firstName", "test_firstName");
        jsonObj.addProperty("lastName", "test_lastName");
        jsonObj.addProperty("password", "qwerty1234");
        jsonObj.addProperty("phoneNumber", "0742000000");
        jsonObj.addProperty("email", email);
        jsonObj.addProperty("deliveryAddress", "street, no. 1");

        //create a new user
        Response response = given().contentType(ContentType.JSON)
                .body(jsonObj.toString())
                .when()
                .post(configReader.getBaseUrlUser());

        int id = response.getBody().jsonPath().get("id");
        userId = id;

        //generate login token for the next tests
        token = given().
                contentType(ContentType.JSON)
                .header("Content-Type", "application/json")
                .body(jsonObj.toString()).when().post(configReader.getBaseUrlLogin())
                .then().statusCode(200).extract().header("Authorization");
    }
}

