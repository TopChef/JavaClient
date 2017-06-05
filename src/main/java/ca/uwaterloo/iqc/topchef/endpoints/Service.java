package ca.uwaterloo.iqc.topchef.endpoints;

import java.util.UUID;

/**
 * Maps the /services/\<service_id\> endpoint
 */
public interface Service extends Endpoint {
    UUID getServiceID();

}
