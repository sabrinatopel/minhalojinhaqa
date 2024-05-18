package test;

import com.github.javafaker.Faker;
import entidade.Usuario;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.internal.common.assertion.Assertion;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UsuarioTest {

    Usuario usuario;
    @BeforeAll
    public static void setupTest(){
        RestAssured.baseURI = "https://serverest.dev";
    }
    @BeforeEach
    public void before(){
        usuario = getNewUser();
    }

    @Test
    public void criarUsuarioComSucesso(){
        Usuario usuario = getNewUser();


        Response response = RestAssured
                .given()
                    .contentType(ContentType.JSON)
                    .log().all()
                .when()
                    .body(usuario)
                    .post("usuarios")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .log().all()
                .and().extract().response();
        JsonPath jsonPath = response.jsonPath();
        String message = jsonPath.get("message");
        Assertions.assertEquals(message,"Cadastro realizado com sucesso");
    }
    @Test
    public void criarUsuarioJaCadastrado() {
        Usuario usuario = getNewUser();

        RestAssured
            .given()
                .contentType(ContentType.JSON)
            .when()
                .body(usuario)  .post("usuarios").then().log().all();
        RestAssured
            .given()
                .contentType(ContentType.JSON)
                .log().all()
            .when()
                .body(usuario)
                .post("usuarios")
            .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .log().all();
    }

    @Test
    public void buscarUsuarioPorIdComSucesso() {
        Usuario usuario = getNewUser();
        Response response = RestAssured
            .given().contentType(ContentType.JSON)
            .when()
                .body(usuario)
                .post("usuarios")
            .then()
                .and()
                .extract()
                .response();
        JsonPath jsonPath = response.jsonPath();
        String id =jsonPath.get("_id");

       RestAssured
            .given().contentType(ContentType.JSON)
            .when()
                .get("usuarios/{_id}", id)
            .then()
                .statusCode(HttpStatus.SC_OK)
                .log().all();

        Assertions.assertEquals("","");
    }

    public Usuario getNewUser(){
        Faker faker = new Faker();
        return new Usuario(
                faker.name().fullName(),
                faker.internet().emailAddress(),
                "teste123",
                "true");
    }
}
