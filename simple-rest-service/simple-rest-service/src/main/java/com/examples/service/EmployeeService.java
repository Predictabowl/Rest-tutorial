package com.exmaples.service;

import java.util.List;

import com.examples.model.Employee;

public interface EmployeeService {
	List<Employee> allEmployees();
	Employee getEmployeeById(String id);
	Employee addEmployee(Employee employee);
	Employee replaceEmployee(String id, Employee employee);
}
