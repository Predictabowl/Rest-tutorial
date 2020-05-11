package com.examples;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BadRequestMapper implements ExceptionMapper<BadRequestException> {

	@Override
	public Response toResponse(BadRequestException exception) {
		return Response.status(Status.BAD_REQUEST.getStatusCode())
				.entity(exception.getMessage())
				.type(MediaType.TEXT_PLAIN)
				.build();
	}

}
