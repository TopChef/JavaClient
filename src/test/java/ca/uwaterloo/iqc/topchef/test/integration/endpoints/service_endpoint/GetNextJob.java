package ca.uwaterloo.iqc.topchef.test.integration.endpoints.service_endpoint;

import ca.uwaterloo.iqc.topchef.endpoints.Job;
import ca.uwaterloo.iqc.topchef.endpoints.Service;
import ca.uwaterloo.iqc.topchef.endpoints.ServiceEndpoint;
import com.github.dmstocking.optional.java.util.Optional;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Contains unit tests for {@link ServiceEndpoint#getNextJob()}
 */
public final class GetNextJob extends AbstractServiceEndpointTestCase {
    @Test
    public void getNextJob() throws Exception {
        Service service = new ServiceEndpoint(client, serviceId);
        Optional<Job> nextJob = service.getNextJob();

        assertTrue(nextJob.isPresent());
        assertEquals(nextJob.get().getID().toString(), jobID);
    }
}
