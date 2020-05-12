package com.examples;

import java.util.logging.Logger;
import static io.restassured.RestAssured.*;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * These tests assume that the server is already running.
 * By default it uses the base URI http://localhost:8080/myapp/.
 * If set, it uses the system properties "server.host" (default "http://localhost")
 * and "server.port" (default "8080") to connect to the server
 * 
 * @author findus
 *
 */

public class EmployeeResourceRestAssuredE2E {
	private static final String BASE_URI = System.getProperty("server.host","http://localhost")
			+":"+ Integer.parseInt(System.getProperty("server.port","8080"))+"/myapp";
	
	private static final String EMPLOYEES = "employees";
	
	@BeforeClass
	public static void configureRestAssured() {
		Logger.getLogger(EmployeeResourceRestAssuredE2E.class.toString())
			.info("Using URL: " + BASE_URI);
		RestAssured.baseURI = BASE_URI;
	}
	
	@Test
	public void test_rest_end_points() {
		JsonObject jBody = Json.createObjectBuilder()
				.add("name", "test employee")
				.add("salary", 1200)
				.build();
		
		Response response = given()
				.contentType(MediaType.APPLICATION_JSON)
				.body(jBody.toString())
			.when()
				.post(EMPLOYEES);
		
		//get the id of the new employee
		String id = response.body().path("id");
		String employeeURI = response.header("location");
		
		assertThat(employeeURI, endsWith(id));
		
		//read the saved employee
		given()
			.accept(MediaType.APPLICATION_JSON)
		.when()
			.get(employeeURI)
		.then()
			.statusCode(Status.OK.getStatusCode())
			.assertThat()
				.body("id",equalTo(id),
					"name",equalTo("test employee"),
					"salary",equalTo(1200));
		
		//replace employee
		jBody = Json.createObjectBuilder()
				.add("name", "replacement employee")
				.add("salary", 3000)
				.build();
		
		given()
			.contentType(MediaType.APPLICATION_JSON)
			.body(jBody.toString())
		.when()
			.put(employeeURI)
		.then()
			.statusCode(Status.OK.getStatusCode())
			.assertThat()
				.body("id",equalTo(id),
					"name",equalTo("replacement employee"),
					"salary",equalTo(3000));
		
		//delete employee
		when()
			.delete(employeeURI)
		.then()
			.statusCode(Status.ACCEPTED.getStatusCode())
			.assertThat()
				.body("id", equalTo(id),
					"name",equalTo("replacement employee"),
					"salary",equalTo(3000));

		//check if the deleted employee can't be found
		when()
			.get(employeeURI)
		.then()
			.statusCode(Status.NOT_FOUND.getStatusCode());
	}
}
