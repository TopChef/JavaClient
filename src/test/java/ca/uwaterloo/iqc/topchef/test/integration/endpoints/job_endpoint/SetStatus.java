package ca.uwaterloo.iqc.topchef.test.integration.endpoints.job_endpoint;

import ca.uwaterloo.iqc.topchef.endpoints.Job;
import ca.uwaterloo.iqc.topchef.endpoints.JobEndpoint;
import ca.uwaterloo.iqc.topchef.endpoints.JobStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Contains integration tests for {@link ca.uwaterloo.iqc.topchef.endpoints.JobEndpoint#setStatus(JobStatus)}
 */
public final class SetStatus extends AbstractJobEndpointTestCase {
    private JobStatus status;

    @Before
    public void getStatus() throws Exception {
        Job job = new JobEndpoint(client, jobID);
        status = job.getStatus();
    }

    @Test
    public void setStatus() throws Exception {
        Job job = new JobEndpoint(client, jobID);
        job.setStatus(JobStatus.WORKING);

        assertEquals(JobStatus.WORKING, job.getStatus());
    }

    @After
    public void setStatusBack() throws Exception {
        Job job = new JobEndpoint(client, jobID);
        job.setStatus(status);
    }
}
