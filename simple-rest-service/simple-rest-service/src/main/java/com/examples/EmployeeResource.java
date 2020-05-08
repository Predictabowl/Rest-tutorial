package com.examples;

import com.examples.model.Employee;
import com.examples.repository.EmployeeRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

/**
 * root resource (exposed at "employee" path)
 * 
 * @author findus
 *
 */
@Path("employees")
public class EmployeeResource {
	
	@Inject
	private EmployeeRepository employeeRepository;

	@GET
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	public List<Employee> getAllEmployees() {
		return employeeRepository.findAll();
	}
	
	@GET
	@Path("{id}")
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	public Employee getOnEmployee(@PathParam("id") String id) {
		return employeeRepository.findOne(id).orElseThrow(() ->
				new NotFoundException("Employee id not found: "+id));
	}
	
	@GET
	@Path("count")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCount() {
		return String.valueOf(employeeRepository.findAll().size());
	}
	
	/**
	 * For simplicity we assume that this operation always succeeds
	 * @throws URISyntaxException 
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addEmployee(Employee employee, @Context UriInfo uriInfo) throws URISyntaxException {
		Employee newEmployee = employeeRepository.save(employee); 
		return Response
				.created(new URI(uriInfo.getAbsolutePath()+"/"+newEmployee.getEmployeeId()))
				.entity(newEmployee)
				.build();
	}

	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Employee replaceEmployee(@PathParam("id") Employee employee) {
		return null;
	}
}
