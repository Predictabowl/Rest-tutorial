package com.examples;

import com.examples.model.Employee;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * root resource (exposed at "employee" path)
 * 
 * @author findus
 *
 */
@Path("employee")
public class EmployeeResource {

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Employee getItXML() {
		Employee employee = new Employee("E1", "An employee", 1000);
		return employee;
	}
}
