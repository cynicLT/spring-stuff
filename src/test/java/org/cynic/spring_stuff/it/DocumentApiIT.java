package org.cynic.spring_stuff.it;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class DocumentApiIT extends BaseIT {

    @Test
    public void givenGoodAuthorizationHeaderWhenItemByIdThenOK() {
        Integer id = 1;

        RestAssured.given()
            .pathParam("id", id)
            .when()
            .header(HttpHeaders.AUTHORIZATION, "bearer " + TOKEN)
            .get("/documents/{id}")
            .then()
            .statusCode(HttpStatus.OK.value())
            .and()
            .assertThat()
            .header(HttpHeaders.CONTENT_TYPE, Matchers.is("application/pdf"))
            .header(HttpHeaders.CONTENT_DISPOSITION, Matchers.is("application/pdf; filename=\"profile.pdf\""))
            .header(HttpHeaders.CONTENT_LENGTH, Matchers.is("68812"));
    }

    @Test
    public void givenGoodAuthorizationHeaderWhenItemByIdThenError() {
        Integer id = 20;

        RestAssured.given()
            .header(HttpHeaders.AUTHORIZATION, "bearer " + TOKEN)
            .pathParam("id", id)
            .when()
            .get("/documents/{id}")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .assertThat()
            .body("size()", Is.is(2))
            .body("code", Is.is("error.document.not-found"))
            .body("values.size()", Is.is(1))
            .body("values[0]", Is.is(id));
    }
}
