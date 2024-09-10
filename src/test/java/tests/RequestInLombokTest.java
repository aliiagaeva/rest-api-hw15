package tests;

import model.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static specs.LoginSpec.*;

public class RequestInLombokTest extends TestBase {


    @Tag("smoke")
    @Test
    void successfulRegisterTest() {
        Body authData = new Body();
        authData.setEmail("eve.holt@reqres.in");
        authData.setPassword("pistol");
        ResponseModel response = step("Выполнение запроса и преобразование ответа", () -> {
            return given(ReqSpecWithJson)
                    .body(authData)
                    .when()
                    .post("/register")
                    .then()
                    .spec(ResponseSpec200)
                    .extract().as(ResponseModel.class);
        });
        step("Проверка полей в ответе", () -> {
            assertThat(response.getToken())
                    .isNotNull()
                    .hasSizeGreaterThan(10)
                    .matches(t -> t.chars().allMatch(Character::isLetterOrDigit));
            assertEquals("4", response.getId());
        });
    }

    @Tag("regression")
    @Test
    void unsuccessfulRegisterTest() {
        Body authData = new Body();
        authData.setEmail("sydney@fife");
        ResponseModel response = step("Выполнение запроса и преобразование ответа", () -> {
            return given(ReqSpecWithJson)
                    .body(authData)
                    .when()
                    .post("/register")
                    .then()
                    .spec(ResponseSpec400)
                    .extract().as(ResponseModel.class);
        });
        step("Проверка полей в ответе", () -> {
            assertEquals("Missing password", response.getError());
        });
    }

    @Tag("smoke")
    @Test
    void getSingleUserTest() {
        UserResponse responseUser = step("Выполнение запроса и преобразование ответа", () -> {
            return given(ReqSpecWithoutJson)
                    .when()
                    .get("/users/2")
                    .then()
                    .spec(ResponseSpec200)
                    .extract().as(UserResponse.class);
        });
        step("Проверка полей в ответе", () -> {
            assertEquals(2, responseUser.getData().getId());
            assertEquals("janet.weaver@reqres.in", responseUser.getData().getEmail());
            assertEquals("Janet", responseUser.getData().getFirstName());
            assertEquals("Weaver", responseUser.getData().getLastName());
            assertEquals("https://reqres.in/img/faces/2-image.jpg", responseUser.getData().getAvatar());
            assertEquals("https://reqres.in/#support-heading", responseUser.getSupport().getUrl());
            assertEquals("To keep ReqRes free, contributions towards server costs are appreciated!", responseUser.getSupport().getText());
        });
    }

    @Tag("regression")
    @Test
    void getSingleUserNotFoundTest() {
        step("Отправка запроса на получение несуществующего пользователя", () -> {
            return given(ReqSpecWithoutJson)
                    .when()
                    .get("/users/23")
                    .then()
                    .spec(ResponseSpec404)
                    .extract().body().asString();
        });
        step("Проверка тела ответа на наличие пустого JSON", () -> {
            String responseBody = given(ReqSpecWithoutJson)
                    .when()
                    .get("/users/23")
                    .then()
                    .spec(ResponseSpec404)
                    .extract().body().asString();
            assertEquals("{}", responseBody);
        });
    }

    @Tag("smoke")
    @Test
    void successfulDeleteTest() {
        step("Отправка запроса на удаление пользователя", () -> {
            return given(ReqSpecWithoutJson)
                    .when()
                    .delete("/users/2")
                    .then()
                    .spec(ResponseSpec204)
                    .extract().asString();
        });

        step("Проверка, что ответ имеет статус код 204", () -> {
            given(ReqSpecWithoutJson)
                    .when()
                    .delete("/users/2")
                    .then()
                    .spec(ResponseSpec204);
        });
    }

    @Tag("regression")
    @Test
    void testGetUnknown() {
        UnknownResponse response = step("Выполнение запроса и преобразование ответа", () -> {
            return given(ReqSpecWithoutJson)
                    .when()
                    .get("/unknown")
                    .then()
                    .spec(ResponseSpec200)
                    .extract().as(UnknownResponse.class);
        });
        step("Проверка полей в ответе", () -> {
            assertEquals(1, response.getPage());
            assertEquals(6, response.getPerPage());
            assertEquals(12, response.getTotal());
            assertEquals(2, response.getTotalPages());
        });
        step("Проверка первого элемента в массиве data", () -> {
            DataItem firstItem = response.getData().get(0);
            assertEquals(1, firstItem.getId());
            assertEquals("cerulean", firstItem.getName());
            assertEquals(2000, firstItem.getYear());
            assertEquals("#98B2D1", firstItem.getColor());
            assertEquals("15-4020", firstItem.getPantoneValue());
        });
        step("Проверка шестого элемента в массиве data", () -> {
            DataItem sixthItem = response.getData().get(5);
            assertEquals(6, sixthItem.getId());
            assertEquals("blue turquoise", sixthItem.getName());
            assertEquals(2005, sixthItem.getYear());
            assertEquals("#53B0AE", sixthItem.getColor());
            assertEquals("15-5217", sixthItem.getPantoneValue());
            assertEquals("https://reqres.in/#support-heading", response.getSupport().getUrl());
            assertEquals("To keep ReqRes free, contributions towards server costs are appreciated!", response.getSupport().getText());
        });
    }
}

