package com.examples;


import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.*;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.*;

public class BadRequestMapperTest extends JerseyTest{

	/**
	 * A mock REST resource with a GET end-point we stub for throwing
	 * a {@link BadRequestException}
	 * @author findus
	 *
	 */
	@Path("testpath")
	public static class MockResource {
		@GET
		@Path("badrequest")
		public String testEndPoint() {
			throw new BadRequestException("an error message");
		}
	}

	@Override
	protected Application configure() {
		return new ResourceConfig()
				.register(BadRequestMapper.class)
				.register(MockResource.class);
	}
	
	@Before
	public void configureRestAssured() {
		// we retrieve the base URI with JerseyTest inherited methods
		RestAssured.baseURI = getBaseUri().toString();
	}
	
	@Test
	public void test_badRequest_response() {
		when()
			.get("testpath/badrequest")
		.then()
			.statusCode(Status.BAD_REQUEST.getStatusCode())
			.contentType(MediaType.TEXT_PLAIN)
			.body(equalTo("an error message"));
	}
	
	
}
