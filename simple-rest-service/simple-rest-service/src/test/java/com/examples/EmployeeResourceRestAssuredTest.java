package com.examples;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.examples.EmployeeResource;
import com.examples.model.Employee;
import com.examples.service.EmployeeService;

import io.restassured.RestAssured;

public class EmployeeResourceRestAssuredTest extends JerseyTest{
	private static final String EMPLOYEES = "employees";
	
	@Mock
	private EmployeeService employeeService;
	
	@Override
	protected Application configure() {
		MockitoAnnotations.initMocks(this);
		// we register only the EmployeeResource class
		return new ResourceConfig(EmployeeResource.class)
				// for mock injection, similar to Guice
				.register(new AbstractBinder() {
					@Override
					protected void configure() {
						// in Guice the bind is in reverse
						bind(employeeService).to(EmployeeService.class);
					}
				});
	}

	@Before
	public void configureRestAssured() {
		RestAssured.baseURI = getBaseUri().toString();
	}
	
	private List<Employee> populateList() {
		LinkedList<Employee> list = new LinkedList<Employee>();
		list.add(new Employee("ID1","First Employee",1500));
		list.add(new Employee("ID2","An Employee",2000));
		return list;
	}
	
	@Test
	public void test_get_all_employees_JSON() {
		when(employeeService.allEmployees())
			.thenReturn(populateList());
		
		given()
			.accept(MediaType.APPLICATION_JSON)
		.when()
			.get(EMPLOYEES)
		.then()
			.statusCode(200)
			.assertThat()
			.body("id[0]",equalTo("ID1"),
				"name[0]",equalTo("First Employee"),
				"salary[0]", equalTo(1500),
				"id[1]",equalTo("ID2"),
				"name[1]",equalTo("An Employee"),
				"salary[1]", equalTo(2000)
			);
	}
	
	@Test
	public void test_get_all_employees_with_root_paths() {
		when(employeeService.allEmployees())
			.thenReturn(populateList());
		
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
					"name", equalTo("First Employee"),
					"salary",equalTo("1500"))
				.root("employees.employee[1]")
				.body("id", equalTo("ID2"),
					"name", equalTo("An Employee"),
					"salary",equalTo("2000"));
	}
	
	@Test
	public void test_get_all_employees_XML() {
		when(employeeService.allEmployees())
		.thenReturn(populateList());
		
		given()
			.accept(MediaType.APPLICATION_XML)
			.get(EMPLOYEES)
		.then()
			.statusCode(200)
			.assertThat()
				.body("employees.employee[0].id",equalTo("ID1"),
					"employees.employee[0].name",equalTo("First Employee"),
					"employees.employee[0].salary",equalTo("1500"),
					"employees.employee[1].id",equalTo("ID2"),
					"employees.employee[1].name",equalTo("An Employee"),
					"employees.employee[1].salary",equalTo("2000")
				);
	}
	
	@Test
	public void test_Get_one_employee_JSON() {
		when(employeeService.getEmployeeById("ID2"))
			.thenReturn(new Employee("ID2","An Employee",2000));
		
		given()
			.accept(MediaType.APPLICATION_JSON)
		.when()
			.get(EMPLOYEES+"/ID2")
		.then()
			.statusCode(200)
			.assertThat()
				.body("id",equalTo("ID2"),
					"name",equalTo("An Employee"),
					"salary",equalTo(2000)
				);
	}


	@Test
	public void test_get_one_employee_XML() {
		when(employeeService.getEmployeeById("ID3"))
			.thenReturn(new Employee("ID3","Third Employee",2500));
		
		given()
			.accept(MediaType.APPLICATION_XML)
		.when()
			.get(EMPLOYEES+"/ID3")
		.then()
			.statusCode(200)
			.assertThat()
				.body("employee.id",equalTo("ID3"),
					"employee.name",equalTo("Third Employee"),
					"employee.salary",equalTo("2500")
				);
	}
	
	@Test
	public void just_for_demo_cannot_access_also_MyResource() {
		given().
			accept(MediaType.TEXT_PLAIN)
		.when()
			.get("myresource")
		.then()
			.statusCode(404);
	}
	
	@Test
	public void test_count() {
		List<Employee> employees = Arrays.asList(new Employee(), new Employee());
		when(employeeService.allEmployees()).thenReturn(employees);
		
		when()
			.get(EMPLOYEES+"/count")
		.then()
			.statusCode(200)
			.assertThat()
				.body(equalTo(String.valueOf(employees.size())));
	}
	
	@Test
	public void test_post_new_employee() {
		JsonObject jsonObj = Json.createObjectBuilder()
				.add("name", "passed name")
				.add("salary", 1000)
				.build();
		when(employeeService.addEmployee(new Employee(null, "passed name", 1000)))
			.thenReturn(new Employee("ID", "returned name", 1250));
		
		given()
			.contentType(MediaType.APPLICATION_JSON)
			.body(jsonObj.toString())
		.when()
			.post(EMPLOYEES)
		.then()
			.statusCode(Status.CREATED.getStatusCode())
			.assertThat()
				.body("id",equalTo("ID"),
					"name",equalTo("returned name"),
					"salary",equalTo(1250))
			.header("Location", r -> Matchers.endsWith(EMPLOYEES+"/ID"));
	}
	
	@Test
	public void tes_put_new_employee(){
		JsonObject jsonObj = Json.createObjectBuilder()
				.add("name", "passed name")
				.add("salary", 1000)
				.build();
		when(employeeService.replaceEmployee("ID", new Employee(null, "passed name", 1000)))
			.thenReturn(new Employee("ID", "returned name", 1250));
		
		given()
			.contentType(MediaType.APPLICATION_JSON)
			.body(jsonObj.toString())
		.when()
			.put(EMPLOYEES+"/ID")
		.then()
			.statusCode(Status.OK.getStatusCode())
			.assertThat()
				.body("id",equalTo("ID"),
					"name",equalTo("returned name"),
					"salary",equalTo(1250));
	}
	
	@Test
	public void test_delete_existing_employee() {
		when(employeeService.deleteEmployeeById("ID"))
			.thenReturn(new Employee("ID","deleted employee",1000))
			.thenReturn(null);
		
		when()
			.delete(EMPLOYEES+"/ID")
		.then()
			.statusCode(Status.ACCEPTED.getStatusCode())
			.assertThat()
				.body("id", equalTo("ID"),
					"name", equalTo("deleted employee"),
					"salary", equalTo(1000));
		
		//idempotence
		when()
			.delete(EMPLOYEES+"/ID")
		.then()
			.statusCode(Status.ACCEPTED.getStatusCode())
			.assertThat()
				.body(Matchers.isEmptyString());

	}
}
