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
public class InMemoryEmployeeRepository implements EmployeeRepository{

	private List<Employee> employees = new LinkedList<Employee>();
	
	public InMemoryEmployeeRepository() {
		// initialize some contents
		employees.add(new Employee("ID1", "Tizio", 1000));
		employees.add(new Employee("ID2", "Caio", 2000));
		employees.add(new Employee("ID3", "Sempronio", 3000));
	}
	
	public synchronized List<Employee> findAll(){
		return employees;
	}
	
	public synchronized Optional<Employee> findOne(String id) {
		return employees.stream().filter(p -> p.getEmployeeId().equals(id)).findFirst();
	}
	
	public synchronized Employee save(Employee employee) {
		employee.setEmployeeId("ID"+(employees.size()+1)); // never do this unless is for learning and testing
		employees.add(employee);
		return employee;
	}
}
