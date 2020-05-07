package com.examples.repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.examples.model.Employee;

/**
 * An example repository for employees.
 * 
 * In a real application this should be handled by a database.
 * @author findus
 *
 */
public class EmployeeRepository {
	// this is bad, used only for this test prototype
	public static final EmployeeRepository instance = new EmployeeRepository();
	
	private List<Employee> employees = new LinkedList<Employee>();
	
	private EmployeeRepository() {
		// initialize some contents
		employees.add(new Employee("ID1", "Tizio", 1000));
		employees.add(new Employee("ID2", "Caio", 2000));
		employees.add(new Employee("ID3", "Sempronio", 3000));
	}
	
	public List<Employee> findAll(){
		return employees;
	}
	
	public Optional<Employee> findOne(String id) {
		return employees.stream().filter(p -> p.getEmployeeId().equals(id)).findFirst();
	}
}
