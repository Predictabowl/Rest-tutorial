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
		employees = new HashMap<String, Employee>();
		repository = new InMemoryEmployeeRepository(employees);
	}
	
	@Test
	public void test_findAll() {
		assertThat(repository.findAll()).containsExactlyInAnyOrder(
				new Employee("ID1", "Tizio", 1000),
				new Employee("ID2", "Caio", 2000),
				new Employee("ID3", "Sempronio", 3000));
	}
	
	@Test
	public void test_findOne() {
		assertThat(repository.findOne("ID2")).isEqualTo(Optional.of(new Employee("ID2", "Caio", 2000)));
	}
	
	@Test
	public void test_save_new_Employee() {
		repository.save(new Employee("ID4", "a new employee", 1500));
		assertThat(employees.get("ID4")).isEqualTo(new Employee("ID4", "a new employee", 1500));
	}
	
	@Test
	public void test_save_existing_Employee() {
		repository.save(new Employee("ID2", "replacement employee", 1750));
		assertThat(employees.get("ID2")).isEqualTo(new Employee("ID2", "replacement employee", 1750));
	}

}
