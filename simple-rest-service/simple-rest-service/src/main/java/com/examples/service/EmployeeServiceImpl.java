package com.examples.service;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import com.examples.model.Employee;
import com.examples.repository.EmployeeRepository;

public class EmployeeServiceImpl implements EmployeeService {

	private EmployeeRepository employeeRepository;

	@Inject
	public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}

	@Override
	public List<Employee> allEmployees() {
		return employeeRepository.findAll();
	}

	@Override
	public Employee getEmployeeById(String id) {
		return employeeRepository.findOne(id)
				.orElseThrow(() -> new NotFoundException("Employee id not found: " + id));
	}

	@Override
	public Employee addEmployee(Employee employee) {
		checkRequest(employee);
		return employeeRepository.save(employee);
	}

	@Override
	public Employee replaceEmployee(String id, Employee employee) {
		checkRequest(employee);
		if (employeeRepository.findOne(id).isPresent()) {
			employee.setEmployeeId(id);
			return employeeRepository.save(employee);
		}
		throw new NotFoundException("Employee id not found: "+id);
	}

	private void checkRequest(Employee employee) {
		if (employee == null)
			throw new BadRequestException("Missing values for Employee");
		if (employee.getEmployeeId() != null)
			throw new BadRequestException(
					"Expecting id Specification in Employee to be absent, but was: " + employee.getEmployeeId());
	}

	@Override
	public Employee deleteEmployeeById(String id) {
		return employeeRepository.delete(id).orElse(null);
	}

}
