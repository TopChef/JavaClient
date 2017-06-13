package ca.uwaterloo.iqc.topchef.endpoints;

import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.Endpoint;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPException;

import java.io.IOException;
import java.util.UUID;

/**
 * Describes a job. A job represents a unit of work that a client can execute. A
 * job is uniquely-identified by its UUID. Each job has a set of parameters, and a set of results.
 * The job parameters must match the job registration schema. The results must match the job result schema
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
     * @throws HTTPException If the server does something weird
     * @throws IOException If the server cannot be contacted
     */
    <T> T getParameters() throws HTTPException, IOException;

    /**
     *
     * @param parameters The new job parameters
     * @throws HTTPException If the server does something weird
     * @throws IOException If the server cannot be contacted
     */
    <T> void setParameters(T parameters) throws HTTPException, IOException;

    /**
     *
     * @return The current status of this job
     * @throws HTTPException If the server does something weird
     * @throws IOException If the server cannot be contacted
     */
    Status getStatus() throws HTTPException, IOException;

    /**
     *
     * @param status The desired status to which this job is to be set
     * @throws HTTPException If the server does something weird
     * @throws IOException If the server cannot be contacted
     */
    void setStatus(Status status) throws HTTPException, IOException;

    /**
     *
     * @param <T> The type to which the result is to be marshalled
     * @return The job results
     * @throws HTTPException If the server does something weird
     * @throws IOException If the server cannot be contacted, or there is a problem marshalling JSON
     */
    <T> T getResult() throws HTTPException, IOException;

    /**
     *
     * @param result The job result
     * @param <T> The type for marshalling the job result
     * @throws HTTPException If the server returns an unexpected response code. This includes the server returning a
     * bad request error if the result doesn't match the result schema
     * @throws IOException If the server cannot be contacted, or if there is a problem marshalling JSON.
     */
    <T> void setResult(T result) throws HTTPException, IOException;

    /**
     * If the incoming result is a string, parse that string as JSON before
     * setting the job result.
     *
     * @param result The job result
     * @throws HTTPException If the server does something weird
     * @throws IOException If the server cannot be contacted
     */
    void setResult(String result) throws HTTPException, IOException;

    /**
     * The execution status of a particular job
     */
    enum Status {
        REGISTERED,
        WORKING,
        COMPLETED
    }
}
