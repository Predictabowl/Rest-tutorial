package com.examples;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.examples.model.Employee;
import com.examples.repository.EmployeeRepository;
import com.examples.repository.InMemoryEmployeeRepository;
import com.examples.service.EmployeeService;
import com.examples.service.EmployeeServiceImpl;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Singleton;

/**
 * Main class.
 *
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/myapp/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * 
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.examples package
        final ResourceConfig rc = new ResourceConfig().packages("com.examples")
        		.register(new AbstractBinder() {
					@Override
					protected void configure() {
						bind(InMemoryEmployeeRepository.class).to(EmployeeRepository.class)
							.in(Singleton.class);
						bind(EmployeeServiceImpl.class).to(EmployeeService.class);
						bindAsContract(new TypeLiteral<LinkedHashMap<String, Employee>>(){})
							.to(new TypeLiteral<Map<String, Employee>>(){});
					}
				});

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.shutdown();
    }
}
