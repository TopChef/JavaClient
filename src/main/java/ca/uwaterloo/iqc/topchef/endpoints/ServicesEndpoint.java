package ca.uwaterloo.iqc.topchef.endpoints;

import ca.uwaterloo.iqc.topchef.Client;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.AbstractMutableJSONEndpoint;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPConnectionCastException;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPException;
import ca.uwaterloo.iqc.topchef.exceptions.ServiceNotFoundException;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.*;

/**
 * Implements the service endpoint for the TopChef API
 */
public class ServicesEndpoint extends AbstractMutableJSONEndpoint implements Services {
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

    /**
     *
     * @return A list of services that have been registered with this API
     * @throws IOException If the list could not be retrieved
     */
    @Override
    public List<Service> getServices() throws IOException, HTTPException {
        ServiceListResponse data = this.getJSON(ServiceListResponse.class);
        return readServices(data);
    }

    /**
     *
     * @param serviceID The UUID of the service
     * @return The service
     * @throws IOException If the client could not contact the server
     * @throws ServiceNotFoundException If the server cannot be found
     * @throws ClassCastException If the connection to determine if this server exists cannot be cast to
     *  an HTTP connection
     */
    @Override
    public Service getServiceByUUID(UUID serviceID) throws IOException, ServiceNotFoundException, ClassCastException {
        Service service = new ServiceEndpoint(client, serviceID);

        Boolean doesServiceExist;

        try {
            doesServiceExist = service.isEndpointUp();
        } catch (HTTPConnectionCastException error){
            throw new ClassCastException("Unable to cast connection to HTTP connection");
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

    /**
     *
     * @param serviceID The ID of the service to retrieve
     * @return The service
     * @throws IOException If I/O cannot be established
     * @throws ServiceNotFoundException If the service cannot be found
     */
    @Override
    public Service getServiceByUUID(String serviceID) throws IOException, ServiceNotFoundException {
        return getServiceByUUID(UUID.fromString(serviceID));
    }

    private List<Service> readServices(ServiceListResponse data) {
        List<Service> constructedServiceList = new LinkedList<Service>();

        for (ServiceData service: data.getData()){
            constructedServiceList.add(new ServiceEndpoint(client, service.getId()));
        }

        return constructedServiceList;
    }

    public static class ServiceListResponse extends
            AbstractMutableJSONEndpoint.DataResponse<List<ServicesEndpoint.ServiceData>, Object, Object> {
        @Getter
        @Setter
        private List<ServicesEndpoint.ServiceData> data;

        @Getter
        @Setter
        private Object meta;
    }

    public static class ServiceData {
        @Getter
        @Setter
        private UUID id;

        @Getter
        @Setter
        private String description;

        @Getter
        @Setter
        private String name;
    }
}
