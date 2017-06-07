package ca.uwaterloo.iqc.topchef.endpoints;

import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.Endpoint;

import java.util.UUID;

/**
 * Maps the /services/\<service_id\> endpoint
 */
public interface Service extends Endpoint {
    UUID getServiceID();

}
