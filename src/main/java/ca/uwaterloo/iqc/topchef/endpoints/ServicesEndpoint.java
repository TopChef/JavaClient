package ca.uwaterloo.iqc.topchef.endpoints;

import ca.uwaterloo.iqc.topchef.Client;
import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.ObjectMapper;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.AbstractImmutableJSONEndpoint;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPConnectionCastException;
import ca.uwaterloo.iqc.topchef.exceptions.ServiceNotFoundException;
import ca.uwaterloo.iqc.topchef.exceptions.UnexpectedResponseCodeException;
import lombok.Data;
import java.io.*;
import java.util.*;

/**
 * Implements the service endpoint for the TopChef API
 */
public class ServicesEndpoint extends AbstractImmutableJSONEndpoint implements Services {
    private static final ObjectMapper mapper = new ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core
            .wrapper.ObjectMapper();

    /**
     * The client for which this endpoint operates
     */
    private final Client client;

    /**
     *
     * @param client The client for the API that this endpoint contacts.
     */
    public ServicesEndpoint(Client client){
        super(client.getURLResolver().getServicesEndpoint());
        this.client = client;
    }

    @Override
    public List<Service> getServices() throws IOException {
        URLConnection connection = openConnectionForGettingServices(this.getURL());

        try {
            connection.connect();
            return readServicesFromConnection(connection);
        } catch (UnexpectedResponseCodeException error) {
            throw new IOException(error);
        } finally {
            connection.disconnect();
        }
    }

    @Override
    public Service getServiceByUUID(UUID serviceID) throws IOException, ServiceNotFoundException {
        Service service = new ServiceEndpoint(client, serviceID);

        Boolean doesServiceExist;

        try {
            doesServiceExist = service.isEndpointUp();
        } catch (HTTPConnectionCastException error){
            throw new RuntimeException(error);
        }

        if (!doesServiceExist){
            throw new ServiceNotFoundException(
                    String.format(
                            "The service with ID %s was not found", serviceID
                    )
            );
        }

        return service;
    }

    @Override
    public Service getServiceByUUID(String serviceID) throws IOException, ServiceNotFoundException {
        return getServiceByUUID(UUID.fromString(serviceID));
    }

    private static URLConnection openConnectionForGettingServices(URL url) throws IOException, RuntimeException {
        URLConnection connection;
        try {
            connection = url.openConnection();
        } catch (HTTPConnectionCastException error){
            throw new RuntimeException(error);
        }

        connection.setDoOutput(Boolean.FALSE);
        connection.setRequestMethod(HTTPRequestMethod.GET);
        connection.setRequestProperty("Content-Type", "application/json");

        return connection;
    }

    private List<Service> readServicesFromConnection(URLConnection connection) throws IOException,
            UnexpectedResponseCodeException {
        assertGoodResponseCode(connection);
        ServiceListResponse data = mapper.readValue(connection.getInputStream(), ServiceListResponse.class);

        List<Service> constructedServiceList = new LinkedList<Service>();

        for (ServiceData service: data.data){
            constructedServiceList.add(new ServiceEndpoint(client, service.getId()));
        }

        return constructedServiceList;
    }

    private static void assertGoodResponseCode(URLConnection connection) throws IOException,
            UnexpectedResponseCodeException {
        HTTPResponseCode code = connection.getResponseCode();

        if (code != HTTPResponseCode.OK) {
            throw new UnexpectedResponseCodeException(code, connection);
        }
    }

    @Data
    public static class ServiceListResponse {
        private List<ServicesEndpoint.ServiceData> data;
        private Object meta;
    }

    @Data
    public static class ServiceData {
        private UUID id;
        private Boolean has_timed_out;
        private String name;
        private String url;
    }
}
