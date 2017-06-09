package ca.uwaterloo.iqc.topchef.endpoints;

import ca.uwaterloo.iqc.topchef.Client;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.AbstractMutableJSONEndpoint;
import lombok.Getter;

import java.util.UUID;

/**
 * Created by mkononen on 09/06/17.
 */
public class JobEndpoint extends AbstractMutableJSONEndpoint implements Job {
    private final Client client;

    @Getter
    private final UUID ID;

    public JobEndpoint(Client client, UUID jobId){
        super(client.getURLResolver().getJobEndpoint(jobId));
        this.client = client;
        this.ID = jobId;
    }

    public JobEndpoint(Client client, String jobId) throws IllegalStateException {
        this(client, UUID.fromString(jobId));
    }
}
