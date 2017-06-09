package ca.uwaterloo.iqc.topchef.endpoints;

import ca.uwaterloo.iqc.topchef.Client;
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

import java.io.IOException;
import java.util.UUID;

/**
 * Describes an endpoint with service data, to which jobs can be submitted
 */
public class ServiceEndpoint extends AbstractMutableJSONEndpoint implements Service {
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
     *
     * @param client The client under which this service was created
     * @param id The service ID
     */
    public ServiceEndpoint(Client client, UUID id){
        super(client.getURLResolver().getServiceEndpoint(id));
        this.serviceID = id;
    }

    /**
     *
     * @param client The client under which this service was created
     * @param id The service ID
     * @throws IllegalArgumentException If the string provided is not a valid UUID
     */
    public ServiceEndpoint(Client client, String id) throws IllegalArgumentException {
        super(client.getURLResolver().getServiceEndpoint(UUID.fromString(id)));
        this.serviceID = UUID.fromString(id);
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
        URLConnection connection;

        try {
            connection = url.openConnection();
        } catch (HTTPConnectionCastException error){
            throw new IOException("Could not cast connection to an HTTP connection");
        }

        connection.setRequestMethod(HTTPRequestMethod.PATCH);
        connection.setDoOutput(Boolean.FALSE);

        return connection;
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
}
