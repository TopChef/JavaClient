package ca.uwaterloo.iqc.topchef.endpoints;

import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.Endpoint;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPException;

import java.io.IOException;
import java.util.UUID;

/**
 * Describes a job
 */
public interface Job extends Endpoint {

    /**
     *
     * @return The ID of this job
     */
    UUID getID();

    /**
     *
     * @param <T> The type to which the job parameters are to be marshalled
     * @return An instance of the provided type that contains the job parameters
     */
    <T> T getParameters() throws HTTPException, IOException;

    /**
     *
     * @param parameters The new job parameters
     */
    <T> void setParameters(T parameters) throws HTTPException, IOException;

    Status getStatus() throws IOException, HTTPException;

    void setStatus(Status status) throws HTTPException, IOException;

    /**
     * The execution status of a particular job
     */
    enum Status {
        REGISTERED,
        WORKING,
        COMPLETED
    }
}
