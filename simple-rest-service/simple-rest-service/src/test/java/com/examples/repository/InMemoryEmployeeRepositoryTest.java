package com.examples.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.examples.model.Employee;

public class InMemoryEmployeeRepositoryTest {

	private InMemoryEmployeeRepository repository;
	private HashMap<String, Employee> employees;
	
	@Before
	public void setUp() {
		employees = new HashMap<>();
		repository = new InMemoryEmployeeRepository(employees);
		employees.clear();
	}
	
	private void putMap(Employee employee) {
		employees.put(employee.getEmployeeId(), employee);
	}
	
	@Test
	public void test_findAll() {
		assertThat(repository.findAll()).isEmpty();
		
		putMap(new Employee("ID1", "First employee", 1000));
		putMap(new Employee("ID3", "Third employee", 1250));
		
		assertThat(repository.findAll()).containsExactlyInAnyOrder(
				new Employee("ID1", "First employee", 1000),
				new Employee("ID3", "Third employee", 1250));
	}
	
	@Test
	public void test_findOne() {
		putMap(new Employee("ID1", "First employee", 1000));
		putMap(new Employee("ID3", "Third employee", 1250));
		
		assertThat(repository.findOne("ID3")).hasValue(new Employee("ID3", "Third employee", 1250));
		assertThat(repository.findOne("ID2")).isNotPresent();
	}
	
	@Test
	public void test_save_new_Employee_with_existing_id() {
		repository.save(new Employee("ID2", "a new employee", 2500));
		assertThat(employees.get("ID2")).isEqualTo(new Employee("ID2", "a new employee", 2500));
	}
	
	@Test
	public void test_replace_Employee_with_existing_id() {
		putMap(new Employee("ID2", "a new employee", 2500));
		
		repository.save(new Employee("ID2", "replacement employee", 1750));
		
		assertThat(employees.get("ID2")).isEqualTo(new Employee("ID2", "replacement employee", 1750));
	}
	
	@Test
	public void test_save_Employee_without_id_is_generated_automatically() {
		String expectedId = "ID1";
		Employee newEmployee = repository.save(new Employee(null, "anonimous employee", 3500));
		String generatedId = newEmployee.getEmployeeId();
		
		assertThat(generatedId).isEqualTo(expectedId);
		assertThat(employees).containsKey(generatedId);
		assertThat(employees.get(generatedId)).isEqualTo(new Employee(generatedId, "anonimous employee", 3500));
	}
	
	@Test
	public void test_delete_Employee_success() {
		putMap(new Employee("ID1", "first employee", 1200));
		
		assertThat(repository.delete("ID1")).isEqualTo(Optional.of(new Employee("ID1", "first employee", 1200)));
		assertThat(employees).isEmpty();
	}
	
	@Test
	public void test_delete_Employee_without_existing_id() {
		Employee employee = new Employee("ID1", "first employee", 1200); 
		putMap(employee);
		
		assertThat(repository.delete("ID2")).isNotPresent();
		assertThat(employees.get("ID1")).isEqualTo(employee);
		assertThat(employees.size()).isEqualTo(1);
	}

}
