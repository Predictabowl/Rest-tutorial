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
	public void testGetIt_XML() {
		given()
			.accept(MediaType.APPLICATION_XML)
		.when()
			.get("employee")
		.then()
			.statusCode(200)
			.assertThat()
				.body("employee.id",equalTo("E1"),
					"employee.name",equalTo("An employee"),
					"employee.salary",equalTo("1000"));
	}
}
