package ca.uwaterloo.iqc.topchef.test.integration.endpoints.job_endpoint;

import ca.uwaterloo.iqc.topchef.endpoints.Job;
import ca.uwaterloo.iqc.topchef.endpoints.JobEndpoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Contains integration tests for {@link ca.uwaterloo.iqc.topchef.endpoints.JobEndpoint#setStatus(Job.Status)}
 */
public final class SetStatus extends AbstractJobEndpointTestCase {
    private Job.Status status;

    @Before
    public void getStatus() throws Exception {
        Job job = new JobEndpoint(client, jobID);
        status = job.getStatus();
    }

    @Test
    public void setStatus() throws Exception {
        Job job = new JobEndpoint(client, jobID);
        job.setStatus(Job.Status.WORKING);

        assertEquals(Job.Status.WORKING, job.getStatus());
    }

    @After
    public void setStatusBack() throws Exception {
        Job job = new JobEndpoint(client, jobID);
        job.setStatus(status);
    }
}
