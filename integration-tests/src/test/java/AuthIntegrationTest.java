import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class AuthIntegrationTest {
    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost:4004";
    }

    @Test
    public void shouldReturnOkWithValidToken(){
        // 1. Arrange
        // 2. act
        // 3. assert
        String loginPayload = """
                    {
                        "email" : "testuser@test.com",
                        "password" : "password123"
                    }
                """;

        Response response = given().contentType("application/json")
                            .body(loginPayload).when().post("/auth/login")
                            .then().statusCode(200)
                            .body("token",notNullValue()).extract().response();
        System.out.println("Generated token:"+response.jsonPath().getString("token"));
    }

    @Test
    public void shouldReturnUnauthorizedWithInvalidLogin(){
        // 1. Arrange
        // 2. act
        // 3. assert
        String loginPayload = """
                    {
                        "email" : "invalid_user@test.com",
                        "password" : "wrongpassword123"
                    }
                """;

                given().contentType("application/json")
                .body(loginPayload).when().post("/auth/login")
                .then().statusCode(401);
    }

}
