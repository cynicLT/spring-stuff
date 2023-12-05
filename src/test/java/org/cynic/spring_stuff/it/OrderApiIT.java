package org.cynic.spring_stuff.it;

import io.restassured.RestAssured;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.hamcrest.text.IsEmptyString;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;


public class OrderApiIT extends BaseIT {

    @Test
    void givenNotAuthorizationHeaderWhenOrdersThenError() {
        RestAssured.given()
            .when()
            .get("/orders")
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .and()
            .assertThat()
            .body("size()", Is.is(1))
            .body("code", Is.is("error.access.denied"));
    }

    @Test
    void givenBadAuthorizationHeaderWhenOrdersThenError() {
        RestAssured.given()
            .when()
            .header(HttpHeaders.AUTHORIZATION, "bearer " + RandomStringUtils.random(50))
            .get("/orders")
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .and()
            .assertThat()
            .body("size()", Is.is(2))
            .body("code", Is.is("error.authentication"))
            .body("values.size()", Is.is(1))
            .body("values[0]", Is.is("Bearer token is malformed"));
    }

    @Test
    void givenGoodAuthorizationHeaderWhenOrdersThenOK() {
        RestAssured.given()
            .when()
            .header(HttpHeaders.AUTHORIZATION, "bearer " + TOKEN)
            .get("/orders")
            .then()
            .statusCode(HttpStatus.OK.value())
            .and()
            .assertThat()
            .body("size()", Matchers.anyOf(Is.is(7), Is.is(8)))
            .body("[0].id", Is.is(1))
            .body("[0].closed", Is.is(false))
            .body("[0].name", Is.is("Deliver bananas"))
            .body("[0].description", Is.is(
                "Passenger secure ways supporting consulting null shipped, moment magazine britannica beads movie key welcome, situations firms emotions overall editorials."))
            .body("[0].owner.size()", Is.is(4))
            .body("[0].owner.name", Is.is("Jonas Jonaitis"))
            .body("[0].owner.phone", Is.is("+370 674 58 789"))
            .body("[0].owner.email", Is.is("smart@girteka.lt"))
            .body("[0].owner.organization.size()", Is.is(4))
            .body("[0].owner.organization.name", Is.is("UAB Girteka"))
            .body("[0].owner.organization.email", Is.is("info@girteka.lt"))
            .body("[0].owner.organization.phone", Is.is("+370 612 34 567"))
            .body("[0].owner.organization.address", Is.is("Verkiu g. 5, Vilnius"));
    }

    @Test
    void givenGoodAuthorizationHeaderWhenOrderByIdThenOK() {
        RestAssured.given()
            .pathParam("id", 7L)
            .when()
            .header(HttpHeaders.AUTHORIZATION, "bearer " + TOKEN)
            .get("/orders/{id}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .and()
            .assertThat()
            .body("size()", Is.is(11))
            .body("id", Is.is(7))
            .body("dateTime", IsNot.not(IsEmptyString.emptyOrNullString()))
            .body("name", Is.is(
                "Notifications poems renaissance application suffer tribal alexandria, importance arranged proven combination vertical prospects butler, commissioners stickers gray rides bless teddy advanced, tooth democracy field polished wireless marked exhaust, guilty."))
            .body("currency", Is.is("EUR"))
            .body("closed", Is.is(true))
            .body("owner.size()", Is.is(4))
            .body("owner.name", Is.is("Jonas Jonaitis"))
            .body("owner.phone", Is.is("+370 674 58 789"))
            .body("owner.email", Is.is("smart@girteka.lt"))
            .body("owner.organization.size()", Is.is(4))
            .body("owner.organization.name", Is.is("UAB Girteka"))
            .body("owner.organization.email", Is.is("info@girteka.lt"))
            .body("owner.organization.phone", Is.is("+370 612 34 567"))
            .body("owner.organization.address", Is.is("Verkiu g. 5, Vilnius"))
            .body("manager.size()", Is.is(4))
            .body("manager.name", Is.is("Gzegos Ivanauskas"))
            .body("manager.phone", Is.is("+370 678 88 911"))
            .body("manager.email", Is.is("kiril@zenitech.co.uk"))
            .body("manager.organization.size()", Is.is(4))
            .body("manager.organization.name", Is.is("UAB Spedlita"))
            .body("manager.organization.email", Is.is("info@spedlita.lt"))
            .body("manager.organization.phone", Is.is("+370 676 54 321"))
            .body("manager.organization.address", Is.is("Bebru g. 12, Kaunas"))
            .body("expenses.size()", Is.is(2))
            .body("expenses.total", Is.is(0))
            .body("expenses.covered", Is.is(0))
            .body("earnings.size()", Is.is(2))
            .body("earnings.total", Is.is(0))
            .body("earnings.covered", Is.is(0))
            .body("documents.size()", Is.is(2))
            .body("documents[0].id", Matchers.anyOf(Is.is(1), Is.is(4)))
            .body("documents[0].fileName", Matchers.anyOf(Is.is("profile.pdf"), Is.is("pienas.pdf")))
            .body("documents[1].id", Matchers.anyOf(Is.is(1), Is.is(4)))
            .body("documents[1].fileName", Matchers.anyOf(Is.is("profile.pdf"), Is.is("pienas.pdf")))
            .body("items.size()", Is.is(1))
            .body("items[0].id", Is.is(9))
            .body("items[0].name", Is.is("Chairs for Southbey"));
    }

    @Test
    void giverGoodAuthorizationHeaderWhenOrderByIdThenError() {
        Integer id = 75;

        RestAssured.given()
            .pathParam("id", id)
            .when()
            .header(HttpHeaders.AUTHORIZATION, "bearer " + TOKEN)
            .get("/orders/{id}")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .and()
            .assertThat()
            .body("size()", Is.is(2))
            .body("code", Is.is("error.order.not-found"))
            .body("values.size()", Is.is(1))
            .body("values[0]", Is.is(id));
    }

    @Test
    void giverGoodAuthorizationHeaderWhenOrderByIdCLoseThenOk() {
        RestAssured.given()
            .pathParam("id", 2)
            .when()
            .header(HttpHeaders.AUTHORIZATION, "bearer " + TOKEN)
            .patch("/orders/{id}")
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void giverGoodAuthorizationHeaderWhenOrderByIdDeleteThenOk() {
        RestAssured.given()
            .pathParam("id", 3)
            .when()
            .header(HttpHeaders.AUTHORIZATION, "bearer " + TOKEN)
            .delete("/orders/{id}")
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }
}