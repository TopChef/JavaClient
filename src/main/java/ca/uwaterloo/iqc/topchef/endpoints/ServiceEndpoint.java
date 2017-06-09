package ca.uwaterloo.iqc.topchef.endpoints;

import ca.uwaterloo.iqc.topchef.Client;
import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.ObjectMapper;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.AbstractMutableJSONEndpoint;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPConnectionCastException;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPException;
import com.github.dmstocking.optional.java.util.Optional;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Describes an endpoint with service data, to which jobs can be submitted
 */
public class ServiceEndpoint extends AbstractMutableJSONEndpoint implements Service {
    private static final Logger log = LoggerFactory.getLogger(ServiceEndpoint.class);

    private static final ObjectMapper mapper = new ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core
            .wrapper.ObjectMapper();

    /**
     * The UUID of this service, used to uniquely refer to this service
     *
     * -- GETTER --
     *
     *  @return the service ID
     */
    @Getter
    private final UUID serviceID;

    /**
     * The client used to access the TopChef API
     */
    private final Client client;

    /**
     *
     * @param client The client under which this service was created
     * @param id The service ID
     */
    public ServiceEndpoint(Client client, UUID id){
        super(client.getURLResolver().getServiceEndpoint(id));

        this.client = client;
        this.serviceID = id;
    }

    /**
     *
     * @param client The client under which this service was created
     * @param id The service ID
     * @throws IllegalArgumentException If the string provided is not a valid UUID
     */
    public ServiceEndpoint(Client client, String id) throws IllegalArgumentException {
        this(client, UUID.fromString(id));
    }

    /**
     *
     * @return The schema that must be matched in order to allow posting a new job
     * @throws HTTPException If the server does something weird
     * @throws IOException If I/O from this machine to the server does something weird
     */
    @Override
    public Object getJobRegistrationSchema() throws HTTPException, IOException {
        return getServiceData().getJob_registration_schema();
    }

    /**
     * Let the server know that this service has not timed out yet
     * @throws HTTPException If the server screws up
     * @throws IOException If I screw up
     */
    @Override
    public void checkIn() throws HTTPException, IOException {
        @Cleanup URLConnection connection = getPatchRequestForCheckIn(this.getURL());
        connection.connect();
        assertGoodResponseCode(connection);
    }

    @Override
    public List<Job> getJobs() throws HTTPException, IOException {
        @Cleanup URLConnection connection = getConnectionForGettingJobs(this.client, this.serviceID);
        connection.connect();
        assertGoodResponseCode(connection);
        return readResponseFromJobsEndpoint(
                mapper.readValue(connection.getInputStream(), JobsEndpointResponse.class),
                client
        );
    }

    @Override
    public Optional<Job> getNextJob() throws HTTPException, IOException {
        Optional<Job> nextJob;

        @Cleanup URLConnection connection = getConnectionForGettingNextJob(this.client, this.serviceID);
        connection.connect();
        HTTPResponseCode code = connection.getResponseCode();

        if (code == HTTPResponseCode.NO_CONTENT) {
            nextJob = Optional.empty();
        } else {
            nextJob = getNextJobFromResponse(connection.getInputStream());
        }

        return nextJob;
    }

    @NotNull
    private Optional<Job> getNextJobFromResponse(InputStream serverResponse) throws IOException {
        JobsEndpointResponse response = mapper.readValue(serverResponse, JobsEndpointResponse.class);
        JobsEndpointData firstJobFromResponse;

        try {
            firstJobFromResponse = response.getData().get(0);
        } catch (IndexOutOfBoundsException error) {
            log.warn("Index exception thrown. Assuming that no next job exists in the queue", error);
            return Optional.empty();
        }

        Job job = new JobEndpoint(client, firstJobFromResponse.getId());
        return Optional.of(job);
    }

    /**
     *
     * @return The data from running a GET request to /services/(service_id)
     * @throws HTTPException If the server does something weird
     * @throws IOException If I/O from this machine to the server does something weird
     */
    private ServiceData getServiceData() throws IOException, HTTPException {
        return this.getJSON(ServiceData.class);
    }

    /**
     * Return a connection configured to make a PATCH request with no request body to the
     * service endpoint. This is needed in order to check a service in with the server
     * @param url The URL where the checkin is to take place
     * @return An open connection to the desired {@link URL} which is properly configured
     * @throws IOException If the connection cannot be opened
     */
    private static URLConnection getPatchRequestForCheckIn(URL url) throws IOException {
        URLConnection connection = openURLConnection(url);
        connection.setRequestMethod(HTTPRequestMethod.PATCH);
        connection.setDoOutput(Boolean.FALSE);

        return connection;
    }

    private static URLConnection getConnectionForGettingJobs(Client client, UUID serviceID) throws IOException {
        URL jobsURL = client.getURLResolver().getJobsEndpointForService(serviceID);
        return openURLConnectionForJSONGet(jobsURL);
    }

    private static URLConnection getConnectionForGettingNextJob(Client client, UUID serviceID) throws IOException {
        URL queueURL = client.getURLResolver().getQueueEndpointForService(serviceID);
        return openURLConnectionForJSONGet(queueURL);
    }

    private static URLConnection openURLConnectionForJSONGet(URL url) throws IOException {
        URLConnection connection = openURLConnection(url);

        connection.setDoOutput(Boolean.FALSE);
        connection.setRequestMethod(HTTPRequestMethod.GET);
        connection.setRequestProperty("Content-Type", "application/json");
        return connection;
    }

    private static URLConnection openURLConnection(URL url) throws IOException {
        try {
            return url.openConnection();
        } catch (HTTPConnectionCastException error){
            throw new IOException("Could not cast connection to an HTTP connection");
        }
    }

    private static List<Job> readResponseFromJobsEndpoint(JobsEndpointResponse response, Client client){
        List<Job> jobList = new LinkedList<Job>();
        List<JobsEndpointData> jobsEndpointData = response.getData();

        for (JobsEndpointData data: jobsEndpointData){
            jobList.add(new JobEndpoint(client, data.id));
        }

        return jobList;
    }

    /**
     * The template for the response JSON from a GET request to /services/(service_id)
     */
    public static class ServiceData {
        @Getter
        @Setter
        private String name;

        @Getter
        @Setter
        private String description;

        @Getter
        @Setter
        private Object job_registration_schema;
    }

    public static class JobsEndpointResponse {
        @Getter
        @Setter
        private Object meta;

        @Getter
        @Setter
        private List<JobsEndpointData> data;
    }

    public static class JobsEndpointData {
        @Getter
        @Setter
        private Date dateSubmitted;

        @Getter
        @Setter
        private String id;

        @Getter
        @Setter
        private String status;
    }
}
