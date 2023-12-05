package org.cynic.spring_stuff.it;

import io.restassured.RestAssured;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class OrganizationApiIT extends BaseIT {

    @Test
    void givenGoodAuthorizationHeaderWhenOrganizationSelfThenOK() {
        RestAssured.given()
            .when()
            .header(HttpHeaders.AUTHORIZATION, "bearer " + TOKEN)
            .get("/organizations/self")
            .then()
            .statusCode(HttpStatus.OK.value())
            .and()
            .assertThat()
            .body("size()", Is.is(6))
            .body("id", Is.is(2))
            .body("name", Is.is("UAB Spedlita"))
            .body("email", Is.is("info@spedlita.lt"))
            .body("phone", Is.is("+370 676 54 321"))
            .body("address", Is.is("Bebru g. 12, Kaunas"))
            .body("managers.size()", Is.is(1))
            .body("managers[0].size()", Is.is(4))
            .body("managers[0].id", Is.is(3))
            .body("managers[0].name", Is.is("Gzegos Ivanauskas"))
            .body("managers[0].email", Is.is("kiril@zenitech.co.uk"))
            .body("managers[0].phone", Is.is("+370 678 88 911"));

    }
}
