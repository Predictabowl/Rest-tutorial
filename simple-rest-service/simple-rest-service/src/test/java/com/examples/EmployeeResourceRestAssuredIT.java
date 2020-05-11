package com.examples;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.examples.Main;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class EmployeeResourceRestAssuredIT {
	private static final String EMPLOYEES = "employees";
	private HttpServer server;
	private static final int THREADS_NUM = 20;

	@BeforeClass
	public static void configureRestAssured() {
		RestAssured.baseURI = Main.BASE_URI;
	}

	@Before
	public void setUp() throws Exception {
		server = Main.startServer();
	}

	@After
	public void tearDown() throws Exception {
		server.shutdown();
	}

	@Test
	public void test_Get_all_employees() {
		given().accept(MediaType.APPLICATION_XML).when().get(EMPLOYEES).then().statusCode(200).assertThat().body(
				"employees.employee[0].id", equalTo("ID1"), "employees.employee[0].name", equalTo("Tizio"),
				"employees.employee[0].salary", equalTo("1000"), "employees.employee[1].id", equalTo("ID2"),
				"employees.employee[1].name", equalTo("Caio"), "employees.employee[1].salary", equalTo("2000"),
				"employees.employee[2].id", equalTo("ID3"), "employees.employee[2].name", equalTo("Sempronio"),
				"employees.employee[2].salary", equalTo("3000"));
	}

	@Test
	public void test_Get_one_employee_XML() {
		given().accept(MediaType.APPLICATION_XML).when().get(EMPLOYEES + "/ID2").then().statusCode(200).assertThat()
				.body("employee.id", equalTo("ID2"), "employee.name", equalTo("Caio"), "employee.salary",
						equalTo("2000"));
	}

	@Test
	public void test_get_one_employee_with_no_existing_id_XML() {
		given().accept(MediaType.APPLICATION_XML).when().get(EMPLOYEES + "/notAnId").then().statusCode(404)
				.contentType(MediaType.TEXT_PLAIN).body(equalTo("Employee id not found: notAnId"));
	}

	@Test
	public void test_get_all_employees_with_root_paths() {
		// This is a variation to show alternative use of XML
		given().accept(MediaType.APPLICATION_XML).when().get(EMPLOYEES).then().statusCode(200).assertThat()
				.root("employees.employee[0]")
				.body("id", equalTo("ID1"), "name", equalTo("Tizio"), "salary", equalTo("1000"))
				.root("employees.employee[1]")
				.body("id", equalTo("ID2"), "name", equalTo("Caio"), "salary", equalTo("2000"))
				.root("employees.employee[2]")
				.body("id", equalTo("ID3"), "name", equalTo("Sempronio"), "salary", equalTo("3000"));
	}

	@Test
	public void test_get_all_employees_JSON() {
		given()
			.accept(MediaType.APPLICATION_JSON)
		.when()
			.get(EMPLOYEES)
		.then()
			.statusCode(200)
			.assertThat().body("id[0]", equalTo("ID1"),
					"name[0]", equalTo("Tizio"),
					"salary[0]", equalTo(1000),
					"id[1]", equalTo("ID2"),
					"name[1]", equalTo("Caio"),
					"salary[1]", equalTo(2000),
					"id[2]", equalTo("ID3"),
					"name[2]", equalTo("Sempronio"),
					"salary[2]", equalTo(3000));
	}

	@Test
	public void test_Get_one_employee_JSON() {
		given().accept(MediaType.APPLICATION_JSON).when().get(EMPLOYEES + "/ID2").then().statusCode(200).assertThat()
				.body("id", equalTo("ID2"), "name", equalTo("Caio"), "salary", equalTo(2000));
	}

	@Test
	public void test_get_one_employee_with_no_existing_id_JSON() {
		given().accept(MediaType.APPLICATION_JSON).when().get(EMPLOYEES + "/notAnId").then().statusCode(404)
				.contentType(MediaType.TEXT_PLAIN).body(equalTo("Employee id not found: notAnId"));
	}

	@Test
	public void just_for_demo_can_access_also_MyResource() {
		given().accept(MediaType.TEXT_PLAIN).when().get("myresource").then().statusCode(200).assertThat()
				.contentType(MediaType.TEXT_PLAIN).and().body(equalTo("Got it!"));
	}

	@Test
	public void test_post_new_employee() {
		JsonObject jsonObj = Json.createObjectBuilder().add("name", "test employee").add("salary", 1100).build();
		Response response = given()
				.contentType(MediaType.APPLICATION_JSON)
				.body(jsonObj.toString())
			.when()
				.post(EMPLOYEES);

		String id = response.body().path("id");
		String uri = response.header("Location");

		assertThat(uri, endsWith(id));

		given().contentType(MediaType.APPLICATION_JSON).when().get(uri).then().statusCode(Status.OK.getStatusCode())
				.assertThat().body("id", equalTo(id), "name", equalTo("test employee"), "salary", equalTo(1100));
	}

	@Test
	public void test_post_new_employee_concurrent() {
		JsonObject jsonObj = Json.createObjectBuilder().add("name", "test employee").add("salary", 1100).build();

		Collection<String> ids = new ConcurrentLinkedQueue<>();
		ExecutorService executor = Executors.newFixedThreadPool(THREADS_NUM);

		List<Future<?>> futures = IntStream.range(0, THREADS_NUM).mapToObj(i -> 
			executor.submit(() -> {
				Response response = given().contentType(MediaType.APPLICATION_JSON)
					.body(jsonObj.toString()).when()
					.post(EMPLOYEES);
				ids.add(response.path("id"));
			})).collect(Collectors.toList());

		Awaitility.await().atMost(10, TimeUnit.SECONDS).until(() -> futures.stream().allMatch(f -> f.isDone()));

		Assertions.assertThat(ids).doesNotHaveDuplicates();
	}
	
	@Test
	public void test_put_replace_employee() {
		JsonObject jsonObj = Json.createObjectBuilder()
				.add("name","modified employee")
				.add("salary", 1750)
				.build();
		
		given()
			.contentType(MediaType.APPLICATION_JSON)
			.body(jsonObj.toString())
		.when()
			.put(EMPLOYEES+"/ID1")
		.then()
			.statusCode(Status.OK.getStatusCode())
			.assertThat()
				.body("id",equalTo("ID1"),
					"name", equalTo("modified employee"),
					"salary", equalTo(1750)
				);
		
		given()
			.accept(MediaType.APPLICATION_JSON)
		.when()
			.get(EMPLOYEES+"/ID1")
		.then()
			.statusCode(Status.OK.getStatusCode())
			.assertThat()
				.body("id",equalTo("ID1"),
					"name", equalTo("modified employee"),
					"salary", equalTo(1750)
				);
	}
	
	@Test public void test_put_BadRequest_when_id_is_part_of_the_body() {
		JsonObject jsonObj = Json.createObjectBuilder()
				.add("id", "ID1")
				.add("name", "modified employee")
				.add("salary", 2000)
				.build();
		
		given()
			.contentType(MediaType.APPLICATION_JSON)
			.body(jsonObj.toString())
		.when()
			.put(EMPLOYEES+"/ID1")
		.then()
			.statusCode(Status.BAD_REQUEST.getStatusCode())
			.assertThat()
				.contentType(MediaType.TEXT_PLAIN)
				.body(equalTo("Expecting id Specification in Employee to be absent, but was: ID1"));
	}
}
