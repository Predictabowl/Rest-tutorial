package com.examples.simple_rest_service;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.RestAssured;
import jakarta.ws.rs.core.MediaType;

public class MyResourceRestAssuredTest {
	private HttpServer server;
	
	@BeforeClass
	public static void configureRestAssured() {
		RestAssured.baseURI = Main.BASE_URI;
	}
	
	@Before
	public void setUp() {
		server = Main.startServer();
	}
	
	@After
	public void tearDown() {
		server.shutdown();
	}
	
	@Test
	public void testGetIt_Text() {
		given()
			.accept(MediaType.TEXT_PLAIN)
		.when()
			.get("myresource")
		.then()
			.statusCode(200)
			.assertThat().contentType(MediaType.TEXT_PLAIN)
				.and()
				.body(equalTo("Got it!"));
	}
	
	@Test
	public void testGetIt_XML() {
		given()
			.accept(MediaType.TEXT_XML)
		.when()
			.get("myresource")
		.then()
			.statusCode(200)
			.assertThat().contentType(MediaType.TEXT_XML)
				.and()
			 	.body("hello", equalTo("Got it (XML)!"));
	}
	
	@Test
	public void testGetIt_HTML() {
		given()
			.accept(MediaType.TEXT_HTML)
		.when()
			.get("myresource")
		.then()
			.statusCode(200)
			.assertThat().contentType(MediaType.TEXT_HTML)
				.and()
			 	.body("html.head.title", equalTo("Hello Jersey"))
			 	.body("html.body", equalTo("Got it (HTML)!"));
	}

}
