package ca.uwaterloo.iqc.topchef.test.integration.endpoints.service_endpoint;

import ca.uwaterloo.iqc.topchef.endpoints.Job;
import ca.uwaterloo.iqc.topchef.endpoints.Service;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by mkononen on 12/06/17.
 */
public final class GetJobs extends AbstractServiceEndpointTestCase {
    @Test
    public void getJobs() throws Exception {
        Service service = client.getService(serviceId);
        List<Job> jobs = service.getJobs();
        assertTrue(jobs.size() > 0);
    }
}
