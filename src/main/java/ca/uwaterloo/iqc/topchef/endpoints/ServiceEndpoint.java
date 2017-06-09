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
    @Getter
    private final UUID serviceID;

    public ServiceEndpoint(Client client, UUID id){
        super(client.getURLResolver().getServiceEndpoint(id));
        this.serviceID = id;
    }

    public ServiceEndpoint(Client client, String id) throws IllegalArgumentException {
        super(client.getURLResolver().getServiceEndpoint(UUID.fromString(id)));
        this.serviceID = UUID.fromString(id);
    }

    @Override
    public Object getJobRegistrationSchema() throws HTTPException, IOException {
        return getServiceData().getJob_registration_schema();
    }

    @Override
    public void checkIn() throws HTTPException, IOException {
        @Cleanup URLConnection connection = getPatchRequestForCheckIn(this.getURL());

        try {
            connection.connect();
            assertGoodResponseCode(connection);
        } finally {
            connection.disconnect();
        }
    }

    private ServiceData getServiceData() throws IOException, HTTPException {
        return this.getJSON(ServiceData.class);
    }

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
