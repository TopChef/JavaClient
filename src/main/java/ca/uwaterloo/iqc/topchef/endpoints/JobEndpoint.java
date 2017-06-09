package ca.uwaterloo.iqc.topchef.endpoints;

import ca.uwaterloo.iqc.topchef.Client;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.AbstractMutableJSONEndpoint;
import lombok.Getter;

import java.util.UUID;

/**
 * Basic stub endpoint for a job. This will become much better.
 */
public class JobEndpoint extends AbstractMutableJSONEndpoint implements Job {

    @Getter
    private final UUID ID;

    public JobEndpoint(Client client, UUID jobId){
        super(client.getURLResolver().getJobEndpoint(jobId));
        this.ID = jobId;
    }

    public JobEndpoint(Client client, String jobId) throws IllegalStateException {
        this(client, UUID.fromString(jobId));
    }
}
