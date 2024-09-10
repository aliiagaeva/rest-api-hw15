package tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

public class ReqresInTest {
    @BeforeAll
    public static void setUp() {
        baseURI = "https://reqres.in/";
        basePath = "/api";
    }

    @Test
    void successfulRegisterTest() {
        String authData = "{" +
                "    \"email\": \"eve.holt@reqres.in\",\n" +
                "    \"password\": \"pistol\"" +
                "}";
        String token = given()
                .body(authData)
                .contentType("application/json")
                .log().uri()

                .when()
                .post("/register")
                .then()
                .log().body()
                .log().status()
                .statusCode(200)
                .body("id", is(4))
                .extract().path("token");
        assertThat(token)
                .isNotNull()
                .hasSizeGreaterThan(10)
                .matches(t -> t.chars().allMatch(Character::isLetterOrDigit));
    }

    @Test
    void unsuccessfulRegisterTest() {
        String authData = "{\"email\": \"sydney@fife\"}";
        given()
                .body(authData)
                .contentType(JSON)
                .log().uri()

                .when()
                .post("/register")
                .then()
                .log().body()
                .log().status()
                .statusCode(400)
                .body("error", is("Missing password"));
    }

    @Test
    void getSingleUserTest() {
        given()
                .log().uri()
                .when()
                .get("/users/2")
                .then()
                .log().body()
                .log().status()
                .statusCode(200)
                .body("data.id", is(2))
                .body("data.email", is("janet.weaver@reqres.in"))
                .body("data.first_name", is("Janet"))
                .body("data.last_name", is("Weaver"))
                .body("data.avatar", is("https://reqres.in/img/faces/2-image.jpg"))
                .body("support.url", is("https://reqres.in/#support-heading"))
                .body("support.text", is("To keep ReqRes free, contributions towards server costs are appreciated!"));
    }

    @Test
    void getSingleUserNotFoundTest() {
        given()
                .log().uri()
                .when()
                .get("/users/23")
                .then()
                .log().body()
                .log().status()
                .statusCode(404)
                .body(is("{}"));
    }

    @Test
    void successfulDeleteTest() {
        given()
                .log().uri()
                .when()
                .delete("/users/2")
                .then()
                .log().body()
                .log().status()
                .statusCode(204);
    }

    @Test
    void testGetUnknown() {
        given()
                .log().uri()
                .when()
                .get("/unknown")
                .then()
                .log().body()
                .log().status()
                .statusCode(200)
                .body("page", is(1))
                .body("per_page", is(6))
                .body("total", is(12))
                .body("total_pages", is(2))
                .body("data.size()", is(6))
                .body("data[0].id", is(1))
                .body("data[0].name", is("cerulean"))
                .body("data[0].year", is(2000))
                .body("data[0].color", is("#98B2D1"))
                .body("data[0].pantone_value", is("15-4020"))
                .body("data[5].id", is(6))
                .body("data[5].name", is("blue turquoise"))
                .body("data[5].year", is(2005))
                .body("data[5].color", is("#53B0AE"))
                .body("data[5].pantone_value", is("15-5217"));
    }
}

