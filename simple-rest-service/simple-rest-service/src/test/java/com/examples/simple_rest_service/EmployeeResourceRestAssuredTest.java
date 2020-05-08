package com.examples.simple_rest_service;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.examples.EmployeeResource;
import com.examples.NotFoundMapper;
import com.examples.model.Employee;
import com.examples.repository.EmployeeRepository;

import io.restassured.RestAssured;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

public class EmployeeResourceRestAssuredTest extends JerseyTest{
	private static final String EMPLOYEES = "employees";
	
	@Mock
	private EmployeeRepository employeeRepository;
	
	@Override
	protected Application configure() {
		MockitoAnnotations.initMocks(this);
		// we register only the EmployeeResource class
		return new ResourceConfig(EmployeeResource.class)
				// Jersey need this class without scanning the whole
				// package, so we register it.
				.register(NotFoundMapper.class)
				// for mock injection, similar to Guice
				.register(new AbstractBinder() {
					@Override
					protected void configure() {
						// in Guice the bind is in reverse
						bind(employeeRepository).to(EmployeeRepository.class);
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
		when(employeeRepository.findAll())
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
		when(employeeRepository.findAll())
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
		when(employeeRepository.findAll())
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
		when(employeeRepository.findOne("ID2"))
			.thenReturn(Optional.of(new Employee("ID2","An Employee",2000)));
		
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
	public void test_get_one_employee_with_no_existing_id_JSON() {
		when(employeeRepository.findOne(anyString()))
			.thenReturn(Optional.empty());
		
		given()
			.accept(MediaType.APPLICATION_JSON)
		.when()
			.get(EMPLOYEES+"/notAnId")
		.then()
			.statusCode(404)
			.contentType(MediaType.TEXT_PLAIN)
			.body(equalTo("Employee id not found: notAnId"));
	}
	
	@Test
	public void test_get_one_employee_with_no_existing_id_XML() {
		given()
			.accept(MediaType.APPLICATION_XML)
		.when()
			.get(EMPLOYEES+"/notAnId")
		.then()
			.statusCode(404)
			.contentType(MediaType.TEXT_PLAIN)
			.body(equalTo("Employee id not found: notAnId"));
	}
	
	@Test
	public void test_get_one_employee_XML() {
		when(employeeRepository.findOne("ID3"))
			.thenReturn(Optional.of(new Employee("ID3","Third Employee",2500)));
		
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
}
