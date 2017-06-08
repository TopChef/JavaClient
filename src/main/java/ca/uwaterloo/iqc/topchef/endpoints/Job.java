package ca.uwaterloo.iqc.topchef.endpoints;

import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.Endpoint;

import java.util.UUID;

/**
 * Describes a job
 */
public interface Job extends Endpoint {
    UUID getID();
}
