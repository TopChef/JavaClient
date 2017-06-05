package ca.uwaterloo.iqc.topchef.endpoint_models;

import ca.uwaterloo.iqc.topchef.exceptions.ServiceNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Maps endpoints for /services
 */
public interface Services extends Endpoint {
    List<Service> getServices() throws IOException;

    Service getServiceByUUID(UUID serviceID) throws IOException, ServiceNotFoundException;

    Service getServiceByUUID(String serviceID) throws IOException, ServiceNotFoundException;
}
