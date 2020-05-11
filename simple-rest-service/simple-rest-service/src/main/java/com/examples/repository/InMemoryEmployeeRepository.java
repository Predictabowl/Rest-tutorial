package com.examples.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import com.examples.model.Employee;

/**
 * An example repository for employees.
 * 
 * In a real application this should be handled by a database.
 * @author findus
 *
 */
public class InMemoryEmployeeRepository implements EmployeeRepository{

	private Map<String, Employee> employees;
	
	@Inject
	public InMemoryEmployeeRepository(Map<String, Employee> employees) {
		this.employees = employees;
		// initialize some contents
		mapPut(new Employee("ID1", "Tizio", 1000));
		mapPut(new Employee("ID2", "Caio", 2000));
		mapPut(new Employee("ID3", "Sempronio", 3000));
	}
	
	private void mapPut(Employee employee) {
		employees.put(employee.getEmployeeId(),employee);
	}
	
	public synchronized List<Employee> findAll(){
		return new ArrayList<>(employees.values());
	}
	
	public synchronized Optional<Employee> findOne(String id) {
		return Optional.ofNullable(employees.get(id));
	}
	
	public synchronized Employee save(Employee employee) {
		if (employee.getEmployeeId() == null) {
			employee.setEmployeeId("ID"+(employees.size()+1)); // never do this unless is for learning and testing
		}
		mapPut(employee);
		return employee;
	}
}
