package ca.uwaterloo.iqc.topchef.endpoints;

import ca.uwaterloo.iqc.topchef.Client;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.AbstractMutableJSONEndpoint;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.ImmutableJSONEndpoint;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPConnectionCastException;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPException;
import com.github.dmstocking.optional.java.util.Optional;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Describes an endpoint with service data, to which jobs can be submitted
 */
public class ServiceEndpoint extends AbstractMutableJSONEndpoint implements Service {
    private static final Logger log = LoggerFactory.getLogger(ServiceEndpoint.class);

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
     * @return The name of the service
     * @throws HTTPException If the server returns an unexpected response code
     * @throws IOException If there is a client-side error
     */
    @Override
    public String getName() throws HTTPException, IOException {
        return getServiceData().getName();
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

    /**
     *
     * @return The list of jobs that have been registered with this service
     * @throws HTTPException If the server returns an unexpected response
     * @throws IOException If there is an error on the client side
     */
    @Override
    public List<Job> getJobs() throws HTTPException, IOException {
        @Cleanup URLConnection connection = getConnectionForGettingJobs(this.client, this.serviceID);
        connection.connect();
        assertGoodResponseCode(connection);
        return readResponseFromJobsEndpoint(
                this.getMapper().readValue(connection.getInputStream(), JobsEndpointResponse.class),
                client
        );
    }

    /**
     *
     * @return The next job, if it exists
     * @throws HTTPException If the server returns an unexpected response
     * @throws IOException If there is an error on the client side
     */
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

    /**
     *
     * @return {@link Boolean#TRUE} if the service has timed out, otherwise {@link Boolean#FALSE}
     * @throws HTTPException If the server returns an unexpected response
     * @throws IOException If there is an error on the client side
     */
    @Override
    public Boolean hasTimedOut() throws HTTPException, IOException {
        return getServiceData().getHas_timed_out();
    }

    /**
     *
     * @param other The instance against which this service is to be compared for equality
     * @param <T> The type of the other thing against which this service is being compared
     * @return whether the UUID of this service and the other service are equal
     */
    @Override
    public <T extends Service> Boolean equals(T other){
        return this.getServiceID().equals(other.getServiceID());
    }

    /**
     *
     * @param serverResponse The server response to parse
     * @return The next job retrieved from that server
     * @throws IOException If the response cannot be parsed
     */
    @NotNull
    private Optional<Job> getNextJobFromResponse(InputStream serverResponse) throws IOException {
        JobsEndpointResponse response = this.getMapper().readValue(serverResponse, JobsEndpointResponse.class);
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
        DataResponse<ServiceData, Object, Object> response = this.getJSON(ServicesResponse.class);
        return response.getData();
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

    /**
     *
     * @param client The client for which the connection is to be retrieved
     * @param serviceID The ID of this service
     * @return A connection to the endpoint for getting jobs for a particular service.
     * @throws IOException If the endpoint cannot be resolved
     */
    private static URLConnection getConnectionForGettingJobs(Client client, UUID serviceID) throws IOException {
        URL jobsURL = client.getURLResolver().getJobsEndpointForService(serviceID);
        return openURLConnectionForJSONGet(jobsURL);
    }

    /**
     *
     * @param client The client for which the connection is to be retrieved
     * @param serviceID The ID of this service
     * @return A connection to the endpoint for getting the next job in the queue for the service
     * @throws IOException If the endpoint cannot be resolved
     */
    private static URLConnection getConnectionForGettingNextJob(Client client, UUID serviceID) throws IOException {
        URL queueURL = client.getURLResolver().getQueueEndpointForService(serviceID);
        return openURLConnectionForJSONGet(queueURL);
    }

    /**
     *
     * @param url The URL for which the connection is to be opened
     * @return The open connection
     * @throws IOException If the connection cannot be opened
     */
    private static URLConnection openURLConnectionForJSONGet(URL url) throws IOException {
        URLConnection connection = openURLConnection(url);

        connection.setDoOutput(Boolean.FALSE);
        connection.setRequestMethod(HTTPRequestMethod.GET);
        connection.setRequestProperty("Content-Type", "application/json");
        return connection;
    }

    /**
     *
     * @param url The URL for which the connection is to be opened
     * @return The open connection
     * @throws IOException If the connection cannot be opened
     */
    private static URLConnection openURLConnection(URL url) throws IOException {
        try {
            return url.openConnection();
        } catch (HTTPConnectionCastException error){
            throw new IOException("Could not cast connection to an HTTP connection");
        }
    }

    /**
     *
     * @param response The response from the server
     * @param client The client to use in constructing the Job endpoints for the list
     * @return The list of jobs, built from parsing the response given into this function
     */
    private static List<Job> readResponseFromJobsEndpoint(JobsEndpointResponse response, Client client){
        List<Job> jobList = new LinkedList<Job>();
        List<JobsEndpointData> jobsEndpointData = response.getData();

        for (JobsEndpointData data: jobsEndpointData){
            jobList.add(new JobEndpoint(client, data.id));
        }

        return jobList;
    }

    /**
     * The response from the services endpoint
     */
    public static class ServicesResponse extends ImmutableJSONEndpoint.DataResponse<ServiceData, Object, Object>{}

    /**
     * The template for the response JSON from a GET request to /services/(service_id)
     * @param <J> The job detail type
     */
    public static class ServiceData<J> {
        @Getter
        @Setter
        private String name;

        @Getter
        @Setter
        private String description;

        @Getter
        @Setter
        private Object job_registration_schema;

        @Getter
        @Setter
        private Boolean has_timed_out;

        @Getter
        @Setter
        private UUID id;

        @Getter
        @Setter
        private Object job_result_schema;

        @Getter
        @Setter
        private Boolean is_service_available;

        @Getter
        @Setter
        private J jobs;

        @Getter
        @Setter
        private Integer timeout;
    }

    /**
     * The raw response from the jobs endpoint
     */
    public static class JobsEndpointResponse extends ImmutableJSONEndpoint.DataResponse<
            List<JobsEndpointData>,Object, Object>{}

    /**
     * The template for the data contained in the ``data`` keyword of the response from the ``/jobs`` endpoint of
     * this service
     *
     * @param <P> The type of parameters
     * @param <R> The type of results
     */
    public static class JobsEndpointData<P, R> {
        @Getter
        @Setter
        private String date_submitted;

        @Getter
        @Setter
        private String id;

        @Getter
        @Setter
        private String status;

        @Getter
        @Setter
        private P parameters;

        @Getter
        @Setter
        private R results;
    }
}
