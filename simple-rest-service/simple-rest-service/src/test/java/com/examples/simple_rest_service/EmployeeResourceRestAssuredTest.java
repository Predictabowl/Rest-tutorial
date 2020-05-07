package com.examples.simple_rest_service;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.examples.Main;

import io.restassured.RestAssured;
import javax.ws.rs.core.MediaType;

public class EmployeeResourceRestAssuredTest {
	private static final String EMPLOYEES = "employees";
	private HttpServer server;
	
	@BeforeClass
	public static void configureRestAssured() {
		RestAssured.baseURI = Main.BASE_URI;
	}
	
	@Before
	public void setUp() throws Exception{
		server = Main.startServer();
	}
	
	@After
	public void tearDown() throws Exception{
		server.shutdown();
	}
	
	@Test
	public void test_Get_all_employees() {
		given()
			.accept(MediaType.APPLICATION_XML)
		.when()
			.get(EMPLOYEES)
		.then()
			.statusCode(200)
			.assertThat()
				.body("employees.employee[0].id",equalTo("ID1"),
					"employees.employee[0].name",equalTo("Tizio"),
					"employees.employee[0].salary",equalTo("1000"),
					"employees.employee[1].id",equalTo("ID2"),
					"employees.employee[1].name",equalTo("Caio"),
					"employees.employee[1].salary",equalTo("2000"),
					"employees.employee[2].id",equalTo("ID3"),
					"employees.employee[2].name",equalTo("Sempronio"),
					"employees.employee[2].salary",equalTo("3000")
				);
	}
	
	@Test
	public void test_Get_one_employee() {
		given()
			.accept(MediaType.APPLICATION_XML)
		.when()
			.get(EMPLOYEES+"/ID2")
		.then()
			.statusCode(200)
			.assertThat()
				.body("employee.id",equalTo("ID2"),
					"employee.name",equalTo("Caio"),
					"employee.salary",equalTo("2000")
				);
	}
	
	@Test
	public void test_get_one_employee_with_no_existing_id() {
		given()
			.accept(MediaType.APPLICATION_XML)
		.when()
			.get(EMPLOYEES+"/notAnId")
		.then()
			.statusCode(404);
	}
	
	@Test
	public void test_get_all_employees_with_root_paths() {
		// This is a variation to show alternative use of XML
		given()
			.accept(MediaType.APPLICATION_XML)
		.when()
			.get(EMPLOYEES)
		.then()
			.statusCode(200)
			.assertThat()
				.root("employees.employee[0]")
				.body("id", equalTo("ID1"),
					"name", equalTo("Tizio"),
					"salary",equalTo("1000"))
				.root("employees.employee[1]")
				.body("id", equalTo("ID2"),
					"name", equalTo("Caio"),
					"salary",equalTo("2000"))
				.root("employees.employee[2]")
				.body("id", equalTo("ID3"),
					"name", equalTo("Sempronio"),
					"salary",equalTo("3000")
				);
	}
}
