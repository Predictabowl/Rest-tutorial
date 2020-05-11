package com.exmaples.service;

import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;
import java.util.Optional;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import com.examples.model.Employee;
import com.examples.repository.EmployeeRepository;

public class EmployeeServiceImplTest {

	@Mock
	private EmployeeRepository repository;
	
	private EmployeeServiceImpl service;
	
	@Before
	public void setUp() {
		initMocks(this);
		service = new EmployeeServiceImpl(repository);
	}

	@Test
	public void test_allEmployees() {
		assertThat(service.allEmployees()).isEmpty();
		
		Employee employee1 = new Employee("ID1", "first employee", 1200);
		Employee employee2 = new Employee("ID3", "second employee", 1250);
		
		when(repository.findAll()).thenReturn(Arrays.asList(employee1,employee2));
		
		assertThat(service.allEmployees()).containsExactlyInAnyOrder(employee1,employee2);
	}

	@Test
	public void test_getEmployeeById_success() {
		String id = "ID1";
		Employee employee1 = new Employee(id, "first employee", 1200);
		
		when(repository.findOne(id)).thenReturn(Optional.of(employee1));
		
		assertThat(service.getEmployeeById(id)).isEqualTo(employee1);
	}
	
	@Test
	public void test_getEmployeeById_failure() {
		when(repository.findOne(anyString())).thenReturn(Optional.ofNullable(null));
	
		assertThatExceptionOfType(NotFoundException.class).isThrownBy(() ->
				service.getEmployeeById("ID1")).withMessage("Employee id not found: ID1");
		
	}
	
	@Test
	public void test_addEmployee_success() {
		Employee employeeToSave = new Employee(null, "first employee", 1200);
		Employee savedEmployee = new Employee("ID1", "something else", 1200);
		when(repository.save(employeeToSave)).thenReturn(savedEmployee);
		
		assertThat(service.addEmployee(employeeToSave)).isEqualTo(savedEmployee);
		verify(repository).save(employeeToSave);
	}
	
	@Test
	public void test_addEmployee_with_specified_Id() {
		Employee employeeToSave = new Employee("ID1", "first employee", 1200);
		
		assertThatThrownBy(() -> service.addEmployee(employeeToSave))
			.isInstanceOf(BadRequestException.class).hasMessage("Expecting id Specification in Employee to be absent, but was: ID1");
		verifyNoInteractions(repository);

	}
	
	@Test
	public void test_addEmployee_with_null_Employee() {
		assertThatExceptionOfType(BadRequestException.class).isThrownBy(() ->
			service.addEmployee(null)).withMessage("Missing values for Employee");
		verifyNoInteractions(repository);

	}
	
	@Test
	public void test_replaceEmployee_success() {
		Employee employeeToSave = new Employee(null, "replacement employee", 1250);
		Employee savedEmployee = new Employee("ID1", "replacement employee", 1250);
		Employee oldEmployee = new Employee("ID1", "already present", 2500);
		
		when(repository.findOne(anyString())).thenReturn(Optional.of(oldEmployee));
		when(repository.save(savedEmployee)).thenReturn(savedEmployee);
		InOrder order = inOrder(repository);
		
		assertThat(service.replaceEmployee("ID1",employeeToSave)).isEqualTo(savedEmployee);
		order.verify(repository).findOne("ID1");
		order.verify(repository).save(employeeToSave);
		verifyNoMoreInteractions(repository);
	}
	
	@Test
	public void test_replaceEmployee_when_employee_is_not_present() {
		
		when(repository.findOne(anyString())).thenReturn(Optional.empty());
		
		assertThatExceptionOfType(NotFoundException.class).isThrownBy(()-> 
			service.replaceEmployee("ID2", new Employee(null,"replacement",2000)))
			.withMessage("Employee id not found: ID2");
		verify(repository).findOne("ID2");
		verifyNoMoreInteractions(repository);
	}
	
	@Test
	public void test_replaceEmployee_with_specified_Id() {
		Employee employeeToSave = new Employee("ID1", "first employee", 1200);
		
		assertThatThrownBy(() -> service.replaceEmployee("ID2",employeeToSave))
			.isInstanceOf(BadRequestException.class).hasMessage("Expecting id Specification in Employee to be absent, but was: ID1");
		verifyNoInteractions(repository);

	}
	
	@Test
	public void test_replaceEmployee_with_null_Employee() {
		assertThatExceptionOfType(BadRequestException.class).isThrownBy(() ->
			service.replaceEmployee("ID3",null)).withMessage("Missing values for Employee");
		verifyNoInteractions(repository);
	}
}
