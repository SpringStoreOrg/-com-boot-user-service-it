package user;

import com.google.gson.JsonObject;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utility.ConfigFileReader;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("REST API testing for User Favorite endpoints")
public class UserFavoriteIT {

    static ConfigFileReader configReader = new ConfigFileReader();

    private static String token;

    private static String email;

    private static long userId;

    public static String YELLOW_CORE_WOOD_CHAIR = "Yellow core wood chair";

    public static String RED_CORE_WOOD_CHAIR = "Red core wood chair";

    public static String BLUE_CORE_WOOD_CHAIR = "Blue core wood chair";

    public static String ORANGE_CORE_WOOD_CHAIR = "Orange core wood chair";


    @BeforeClass
    static void setUp() {
        //Set up the base URI
        RestAssured.baseURI = configReader.getBaseUrlUserFavorites();

        //create a new user for testing the user favorites endpoints
        createNewUser();
    }

    @Feature("Add product to user favorites")
    @Description("Testing 'addProductToUserFavorites' endpoint, using default value")
    @Test(priority = 1)
    void addProductToUserFavorites() {

        given().filter(new AllureRestAssured())
                .header("Authorization", token)
                .header("userId", userId)
                .pathParam("productName", YELLOW_CORE_WOOD_CHAIR)
                .when()
                .post("{productName}")
                .then()
                .body("[0].name", is(YELLOW_CORE_WOOD_CHAIR))
                .and().statusCode(200);
    }

    @Feature("Add products to user favorites")
    @Description("Testing 'addProductsToUserFavorites' endpoint, using default values")
    @Test(priority = 2)
    void addProductsToUserFavorites() {

        List<String> productNames = List.of(RED_CORE_WOOD_CHAIR, BLUE_CORE_WOOD_CHAIR, ORANGE_CORE_WOOD_CHAIR);

        given().filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .header("userId", userId)
                .body(productNames)
                .when()
                .put()
                .then().assertThat().statusCode(200)
                .and().body("$", hasSize(greaterThan(2)));
    }

    @Feature("Remove product from user favorites")
    @Description("Testing 'removeProductFromUserFavorites' endpoint, using default value")
    @Test(dependsOnMethods = {"addProductToUserFavorites"}, priority = 3)
    void removeProductFromUserFavorites() {

        given().filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .header("Authorization", token)
                .header("userId", userId)
                .header("Content-Type", "application/json")
                .pathParam("productName", YELLOW_CORE_WOOD_CHAIR)
                .when()
                .delete("{productName}")
                .then()
                .assertThat().statusCode(200);
    }

    @Feature("getAllUsers")
    @Description("Testing 'getAllUsers' endpoint")
    @Test(dependsOnMethods = {"addProductsToUserFavorites"}, priority = 4)
    void getAllUsers() {

        given().filter(new AllureRestAssured())
                .header("Authorization", token)
                .header("userId", userId)
                .when().get()
                .then().assertThat().statusCode(200)
                .and().body("size()", is(3));
    }

    @AfterClass
    public void afterClass() {

        //delete the user created for testing
        given().filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
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

