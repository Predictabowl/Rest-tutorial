package com.examples;

import com.examples.model.Employee;
import com.examples.repository.EmployeeRepository;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * root resource (exposed at "employee" path)
 * 
 * @author findus
 *
 */
@Path("employees")
public class EmployeeResource {

	@GET
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	public List<Employee> getAllEmployees() {
		return EmployeeRepository.instance.findAll();
	}
	
	@GET
	@Path("{id}")
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	public Employee getOnEmployee(@PathParam("id") String id) {
		return EmployeeRepository.instance.findOne(id).orElseThrow(() ->
				new NotFoundException("Employee id not found: "+id));
	}
}
