package ca.uwaterloo.iqc.topchef.endpoints;

import ca.uwaterloo.iqc.topchef.Client;
import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.RequiresJSONMapper;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.AbstractMutableJSONEndpoint;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPConnectionCastException;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPException;
import lombok.Cleanup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

/**
 * Basic stub endpoint for a job. This will become much better.
 */
public class JobEndpoint extends AbstractMutableJSONEndpoint implements Job, RequiresJSONMapper {

    /**
     * The log to which notes about application state will be written
     */
    private static final Logger log = LoggerFactory.getLogger(JobEndpoint.class);

    /**
     * The Job ID
     *
     * -- GETTER --
     *
     * @return the Job ID
     */
    @Getter
    private final UUID ID;

    /**
     *
     * @param client The TopChef client
     * @param jobId The ID of the job for which this endpoint is being initialized
     */
    public JobEndpoint(Client client, UUID jobId){
        super(client.getURLResolver().getJobEndpoint(jobId));
        this.ID = jobId;
    }

    /**
     *
     * @param client The TopChef client from which this was called
     * @param jobId The job ID
     * @throws IllegalStateException If the job ID cannot be parsed as a UUID
     */
    public JobEndpoint(Client client, String jobId) throws IllegalStateException {
        this(client, UUID.fromString(jobId));
    }

    /**
     *
     * @param url The URL of the TopChef API endpoint where this job is located. If the URL is known at construction,
     *            this constructor allows one to avoid having to construct an entire TopChef client.
     * @param jobID The job ID
     */
    public JobEndpoint(URL url, UUID jobID){
        super(url);
        this.ID = jobID;
    }

    /**
     *
     * @param url The URL endpoint where the Job is located. {@link JobEndpoint#JobEndpoint(URL, UUID)}
     * @param jobID The Job ID as a string
     * @throws IllegalArgumentException If the Job ID cannot be parsed as a UUID
     */
    public JobEndpoint(URL url, String jobID) throws IllegalArgumentException {
        this(url, UUID.fromString(jobID));
    }

    /**
     *
     * @param <T> The desired return type of the job parameters.
     * @return The job parameters
     * @throws HTTPException If the server does not return the correct HTTP response code
     * @throws IOException If there is an error contacting the server, or parsing the returning JSON.
     */
    @Override
    public <T> T getParameters() throws HTTPException, IOException {
        ResponseToJobDetailsGetRequest rawResponse = getJSON(ResponseToJobDetailsGetRequest.class);
        ResponseToJobDetailsGetRequest<T, Object> response = dangerouslyCastParametersToType(rawResponse);
        return response.getData().getParameters();
    }

    /**
     *
     * @param newParameters The parameters that will be set
     * @param <T> The type of the new parameters
     * @throws HTTPException If the server does not return the correct HTTP response code
     * @throws IOException If there is an error contacting the server, or writing down the desired new parameters as
     *      JSON.
     */
    @Override
    public <T> void setParameters(T newParameters) throws HTTPException, IOException {
        ResponseToJobDetailsGetRequest rawResponse = getJSON(ResponseToJobDetailsGetRequest.class);
        ResponseToJobDetailsGetRequest<T, Object> response = dangerouslyCastParametersToType(rawResponse);
        response.getData().setParameters(newParameters);
        putJobData(response.getData());
    }

    /**
     *
     * @return The current job status
     * @throws IOException If the server cannot be contacted
     * @throws HTTPException If the server does not return the correct response codes
     */
    @Override
    public Status getStatus() throws IOException, HTTPException {
        ResponseToJobDetailsGetRequest<Object, Object> response = getJSON(GenericResponse.class);
        String status = response.getData().getStatus();
        return getStatusForString(status);
    }

    /**
     *
     * @param status The desired status to which this job is to be set
     * @throws IOException If the server cannot be contacted or if there is an error processing JSON on the client side
     * @throws HTTPException If the server does not return the expected response
     */
    @Override
    public void setStatus(Status status) throws IOException, HTTPException {
        String newStatus = getStringForStatus(status);
        JobDetails<Object, Object> jobData = getJSON(GenericResponse.class).getData();
        jobData.setStatus(newStatus);
        putJobData(jobData);
    }

    /**
     *
     * @param <T> The type to which the job result is to be marshalled
     * @return The job result
     * @throws IOException If the server cannot be contacted or if there is an error processing JSON on the client side
     * @throws HTTPException If the server does not return the expected response
     */
    @Override
    public <T> T getResult() throws IOException, HTTPException {
        ResponseToJobDetailsGetRequest<Object, T> response = dangerouslyCastParametersToType(
                getJSON(ResponseToJobDetailsGetRequest.class)
        );
        return response.getData().getResult();
    }

    /**
     *
     * @param result The result to which the job is to be set
     * @param <T> The type of the result
     * @throws IOException If the server cannot be contacted or if there is an error processing JSON on the client side
     * @throws HTTPException If the server does not return the expected response
     */
    @Override
    public <T> void setResult(T result) throws IOException, HTTPException {
        ResponseToJobDetailsGetRequest<Object, T> response = dangerouslyCastParametersToType(
                getJSON(ResponseToJobDetailsGetRequest.class)
        );
        response.getData().setResult(result);
        putJobData(response.getData());
    }

    /**
     * If the result to set is a string, parse the string as JSON
     * @param result The result to set.
     * @throws IOException If the server cannot be contacted or if there is an error processing JSON on the client side
     * @throws HTTPException If the server does not return the expected response
     */
    @Override
    public void setResult(String result) throws IOException, HTTPException {
        ResponseToJobDetailsGetRequest<Object, Object> response = dangerouslyCastParametersToType(
                getJSON(ResponseToJobDetailsGetRequest.class)
        );
        response.getData().setResult(getMapper().readValue(result));
        putJobData(response.getData());
    }

    /**
     *
     * @param data The new job details that will be sent to the server
     * @param <P> The type that the job parameters have
     * @param <R> The type to which the job results belong
     * @throws IOException If the server cannot be contacted or if there is an error processing JSON on the client side
     * @throws HTTPException If the server does not return the expected response
     */
    private <P, R> void putJobData(JobDetails<P, R> data) throws IOException, HTTPException {
        @Cleanup URLConnection connection = getPutConnection(this.getURL());
        connection.connect();
        this.getMapper().writeValue(connection.getOutputStream(), data);
        assertGoodResponseCode(connection);
    }

    /**
     *
     * @param status The string for which the status is to be generated
     * @return The status for the string argument
     * @throws IOException If the string cannot be mapped to a status
     */
    @NotNull
    @Contract(pure = true)
    private static Status getStatusForString(String status) throws IOException {
        String comparison = status.toUpperCase();

        if ("REGISTERED".equals(comparison)){
            return Status.REGISTERED;
        } else if ("COMPLETED".equals(comparison)) {
            return Status.COMPLETED;
        } else if ("WORKING".equals(comparison)) {
            return Status.WORKING;
        } else {
            throw new IOException(String.format("Cannot resolve status %s", comparison));
        }
    }

    /**
     *
     * @param url The URL for which the connection is to be made
     * @return a connection for a PUT request to the API at the desired URL
     * @throws IOException If the connection cannot be established
     */
    private static URLConnection getPutConnection(URL url) throws IOException {
        URLConnection connection;
        try {
            connection = url.openConnection();
        } catch (HTTPConnectionCastException error){
            throw new IllegalStateException(error);
        }

        connection.setRequestMethod(HTTPRequestMethod.PUT);
        connection.setDoOutput(Boolean.TRUE);
        connection.setRequestProperty("Content-Type", "application/json");

        return connection;
    }

    /**
     *
     * @param status The status for which a string is to be retrieved
     * @return The string matching this job execution status
     * @throws IOException If a match for this {@link ca.uwaterloo.iqc.topchef.endpoints.Job.Status} is not defined
     */
    @NotNull
    private static String getStringForStatus(Status status) throws IOException {
        switch (status) {
            case COMPLETED:
                return "COMPLETED";
            case WORKING:
                return "WORKING";
            case REGISTERED:
                return "REGISTERED";
            default:
                throw new IOException(String.format("Unable to get string for status %s", status.toString()));
        }
    }

    /**
     *
     * @param rawResponse The original response that is to be cast
     * @param <P> The type to which the job parameters are to be cast
     * @param <R> The type to which the job result is to be cast
     * @return The response, cast to the appropriate type
     * @throws IOException If the casting cannot be done
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    private <P, R> ResponseToJobDetailsGetRequest<P, R> dangerouslyCastParametersToType(
            ResponseToJobDetailsGetRequest rawResponse
    ) throws IOException {
        try {
            return (ResponseToJobDetailsGetRequest<P, R>)(rawResponse);
        } catch (ClassCastException error){
            log.error("Unable to cast job response to desired type", error);
            throw new IOException(error);
        }
    }

    /**
     * The JSON template for a response to the jobs endpoint of this service
     * @param <P> The type of the job parameter schema
     * @param <R> The type of the job result schema
     */
    public static class ResponseToJobDetailsGetRequest<P, R> {
        @Getter
        @Setter
        private JobDetails<P, R> data;
    }

    /**
     * The JSON template for what should be contained in the ``data`` keyword inside the returned
     * JSON. This template describes the data returned by the service
     * @param <P> The type for the job parameter schema
     * @param <R> The type for the job result schema
     */
    @EqualsAndHashCode
    public static class JobDetails<P, R> {
        @Getter
        @Setter
        private String date_submitted;

        @Getter
        @Setter
        private String id;

        @Getter
        @Setter
        private P parameters;

        @Getter
        @Setter
        private String status;

        @Getter
        @Setter
        private R result;
    }

    /**
     * A response with the parameter and result schema type set to {@link Object}
     */
    private static class GenericResponse extends ResponseToJobDetailsGetRequest<Object, Object>{}
}
