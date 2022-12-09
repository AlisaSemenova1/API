package org.example;

import cucumber.api.java.ru.Дано;
import cucumber.api.java.ru.Если;
import cucumber.api.java.ru.Тогда;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.logging.Logger;


public class AccessSystemSteps {

    static String baseUri = "http://localhost:9000/check";

    static String baseUriRooms = "http://localhost:9000/info/rooms";

    static String baseUriUsers = "http://localhost:9000/info/users";
    Logger log = Logger.getLogger(AccessSystemSteps.class.getName());
    int roomId;

    int keyId;

    int start;

    int end;

    @Дано("^вводим \"([^\"]*)\" и \"([^\"]*)\"$")
    public void enterRoomAndKey(String keyId, String roomId) {
        this.keyId = Integer.parseInt(keyId);
        this.roomId = Integer.parseInt(roomId);
        log.info("Номер комнаты " + roomId + " Номер ключа " + keyId);
    }

    public void entrance(String entrance, int status) {
        RestAssured.given()
                .baseUri(baseUri)
                .param("entrance", entrance)
                .param("roomId", roomId)
                .param("keyId", keyId)
                .when()
                .get()
                .then()
                .statusCode(status)
                .extract()
                .response();
    }

    @Если("^номер комнаты делится без остатка на его ключ то пользователю можно \"([^\"]*)\"$")
    public void entranceRoom(String param) {
        entrance(param, 200);
    }


    @Если("^вход успешен, то \"([^\"]*)\" из комнаты$")
    public void exit(String param) {
        entrance(param, 200);
    }

    @Если("^номер комнаты не делится без остатка на его ключ то пользователю нельзя \"([^\"]*)\"$")
    public void accessDenied(String param) {
        entrance(param, 403);
    }

    @Если("^номер комнаты больше разрешонного значения, то пользователю нельзя \"([^\"]*)\"$")
    public void roomNumberIncorrect(String param) {
        entrance(param, 500);
    }

    public void roomsAndUsersCheck() {
        Response response = RestAssured.given()
                .baseUri(baseUriRooms)
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .response();
        var jsonString = response.asString();
        log.info(jsonString);
    }

    @Дано("^вывод информации о всех комнатах и пользователях в них$")
    public void roomAndUsers() {
        roomsAndUsersCheck();
    }

    @Дано("^выводим \"([^\"]*)\" и \"([^\"]*)\"$")
    public void enterParameters(int end, int start) {
        this.end = end;
        this.start = start;
        log.info("Начало отсчета " + start + " Конец отсчета " + end);
    }

    @Тогда("^видим информацию о пользователе и в какой комнате он находится$")
    public void getUserInformation() {
        Response response = RestAssured.given()
                .baseUri(baseUriUsers)
                .param("end", end)
                .param("start", start)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .response();
        var jsonString = response.asString();
        log.info(jsonString);
    }
}