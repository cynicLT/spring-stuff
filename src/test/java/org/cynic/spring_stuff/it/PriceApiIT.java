package org.cynic.spring_stuff.it;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Set;
import org.cynic.spring_stuff.domain.http.price.CreatePriceHttp;
import org.cynic.spring_stuff.domain.http.price.CreatePriceHttp.CreatePriceFractionHttp;
import org.cynic.spring_stuff.domain.model.type.PriceType;
import org.cynic.spring_stuff.domain.model.type.ReferenceType;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class PriceApiIT extends BaseIT {

    @Test
    void givenGoodAuthorizationHeaderWhenPricesThenOK() {
        RestAssured.given()
            .when()
            .header(HttpHeaders.AUTHORIZATION, "bearer " + TOKEN)
            .get("/prices")
            .then()
            .statusCode(HttpStatus.OK.value())
            .and()
            .assertThat()
            .body("size()", Matchers.in(List.of(8, 9)))
            .body("[0].size()", Is.is(9))
            .body("[0].id", Is.is(1))
            .body("[0].dateTime", Is.is("2023-11-09T08:50:11Z"))
            .body("[0].currency", Is.is("USD"))
            .body("[0].value", Is.is(1.99f))
            .body("[0].type", Is.is("INCOME"))
            .body("[0].referenceType", Is.is("ORDER"))
            .body("[0].fractions.size()", Is.is(3))
            .body("[0].fractions.fraction", Matchers.containsInAnyOrder(0.7f, 1f, 0.7f))
            .body("[0].fractions.comment", Matchers.containsInAnyOrder("Comment XX", "Comment 2", "Comment 1"))
            .body("[0].documents.size()", Is.is(4))
            .body("[0].documents.id", Matchers.containsInAnyOrder(1, 2, 3, 4))
            .body("[0].documents.fileName", Matchers.containsInAnyOrder("katalogas.pdf", "pienas.pdf", "profile.pdf", "document.txt"))
            .body("[0].covered", Is.is(false));
    }

    @Test
    void givenGoodAuthorizationHeaderWhenPricesOfOrderThenOK() {
        RestAssured.given()
            .when()
            .header(HttpHeaders.AUTHORIZATION, "bearer " + TOKEN)
            .queryParam("orderId", 1)
            .get("/prices")
            .then()
            .statusCode(HttpStatus.OK.value())
            .and()
            .assertThat()
            .body("size()", Matchers.anyOf(Is.is(5), Is.is(4)))
            .body("[0].size()", Is.is(9))
            .body("[0].id", Is.is(1))
            .body("[0].dateTime", Is.is("2023-11-09T08:50:11Z"))
            .body("[0].currency", Is.is("USD"))
            .body("[0].value", Is.is(1.99f))
            .body("[0].type", Is.is("INCOME"))
            .body("[0].referenceType", Is.is("ORDER"))
            .body("[0].fractions.size()", Is.is(1))
            .body("[0].fractions[0].fraction", Is.is(1f))
            .body("[0].fractions[0].comment", Is.is("Comment 1"))
            .body("[0].documents.size()", Is.is(4))
            .body("[0].documents[0].id", Matchers.in(List.of(1, 2, 3, 4)))
            .body("[0].covered", Is.is(false));
    }

    @Test
    void giverGoodAuthorizationHeaderWhenPriceByIdDeleteThenOk() {
        RestAssured.given()
            .pathParam("id", 3)
            .when()
            .header(HttpHeaders.AUTHORIZATION, "bearer " + TOKEN)
            .delete("/prices/{id}")
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }


    @Test
    void giverGoodAuthorizationHeaderWhenCreatePriceByItemsThenOk() {
        RestAssured.given()
            .when()
            .header(HttpHeaders.AUTHORIZATION, "bearer " + TOKEN)
            .contentType(ContentType.JSON)
            .body(new CreatePriceHttp(
                BigDecimal.ONE,
                ZonedDateTime.now(clock),
                ZonedDateTime.now(clock).plusDays(10),
                Currency.getInstance("EUR"),
                PriceType.INCOME,
                false,
                ReferenceType.ITEM,
                Set.of(
                    new CreatePriceFractionHttp(BigDecimal.ONE, "comment", 1L),
                    new CreatePriceFractionHttp(BigDecimal.ONE, "comment", 2L)
                )
            ))
            .post("/prices")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(Matchers.oneOf("11", "12")); 
    }

    @Test
    void giverGoodAuthorizationHeaderWhenCreatePriceByOrdersThenOk() {
        RestAssured.given()
            .when()
            .header(HttpHeaders.AUTHORIZATION, "bearer " + TOKEN)
            .contentType(ContentType.JSON)
            .body(new CreatePriceHttp(
                BigDecimal.ONE,
                ZonedDateTime.now(clock),
                ZonedDateTime.now(clock).plusDays(10),
                Currency.getInstance("EUR"),
                PriceType.SPENT,
                false,
                ReferenceType.ORDER,
                Set.of(
                    new CreatePriceFractionHttp(BigDecimal.ONE, "comment", 5L),
                    new CreatePriceFractionHttp(BigDecimal.ONE, "comment", 6L)
                )
            ))
            .post("/prices")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(Matchers.oneOf("11", "12"));
    }
}
