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
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * Basic stub endpoint for a job. This will become much better.
 */
public class JobEndpoint extends AbstractMutableJSONEndpoint implements Job, RequiresJSONMapper {

    private static final Logger log = LoggerFactory.getLogger(JobEndpoint.class);

    @Getter
    private final UUID ID;

    public JobEndpoint(Client client, UUID jobId){
        super(client.getURLResolver().getJobEndpoint(jobId));
        this.ID = jobId;
    }

    public JobEndpoint(Client client, String jobId) throws IllegalStateException {
        this(client, UUID.fromString(jobId));
    }

    @Override
    public <T> T getParameters() throws HTTPException, IOException {
        ResponseToJobDetailsGetRequest rawResponse = getJSON(ResponseToJobDetailsGetRequest.class);
        ResponseToJobDetailsGetRequest<T, Object> response = dangerouslyCastParametersToType(rawResponse);
        return response.getData().getParameters();
    }

    @Override
    public <T> void setParameters(T newParameters) throws HTTPException, IOException {
        ResponseToJobDetailsGetRequest rawResponse = getJSON(ResponseToJobDetailsGetRequest.class);
        ResponseToJobDetailsGetRequest<T, Object> response = dangerouslyCastParametersToType(rawResponse);
        response.getData().setParameters(newParameters);
        putJobData(response.getData());
    }

    @Override
    public Status getStatus() throws IOException, HTTPException {
        ResponseToJobDetailsGetRequest<Object, Object> response = getJSON(GenericResponse.class);

        String status = response.getData().getStatus();
        return getStatusForString(status);
    }

    @Override
    public void setStatus(Status status) throws IOException, HTTPException {
        String newStatus = getStringForStatus(status);
        JobDetails<Object, Object> jobData = getJSON(GenericResponse.class).getData();
        jobData.setStatus(newStatus);
        putJobData(jobData);
    }

    private void putJobData(JobDetails data) throws IOException, HTTPException {
        @Cleanup URLConnection connection = getPutConnection(this.getURL());
        connection.connect();
        this.getMapper().writeValue(connection.getOutputStream(), data);
        assertGoodResponseCode(connection);
    }

    private static Status getStatusForString(String status) throws IOException {
        String comparison = status.toUpperCase();

        if (comparison.equals("REGISTERED")){
            return Status.REGISTERED;
        } else if (comparison.equals("COMPLETED")) {
            return Status.COMPLETED;
        } else if (comparison.equals("WORKING")) {
            return Status.WORKING;
        } else {
            throw new IOException(String.format("Cannot resolve status %s", comparison));
        }
    }

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

    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    private <T> ResponseToJobDetailsGetRequest<T, Object> dangerouslyCastParametersToType(
            ResponseToJobDetailsGetRequest rawResponse
    ) throws IOException {
        try {
            return (ResponseToJobDetailsGetRequest<T, Object>)(rawResponse);
        } catch (ClassCastException error){
            log.error("Unable to cast job response to desired type", error);
            throw new IOException(error);
        }
    }

    public static class ResponseToJobDetailsGetRequest<P, R> {
        @Getter
        @Setter
        private JobDetails<P, R> data;
    }

    public static final class JobDetails<P, R> {
        @Getter
        @Setter
        private Date date_submitted;

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

    private static class GenericResponse extends ResponseToJobDetailsGetRequest<Object, Object>{}
}
