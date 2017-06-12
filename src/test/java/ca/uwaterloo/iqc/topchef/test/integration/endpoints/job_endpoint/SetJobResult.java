package ca.uwaterloo.iqc.topchef.test.integration.endpoints.job_endpoint;

import ca.uwaterloo.iqc.topchef.endpoints.Job;
import ca.uwaterloo.iqc.topchef.endpoints.JobEndpoint;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Contains integration tests for {@link ca.uwaterloo.iqc.topchef.endpoints.JobEndpoint#setResult(Object)}
 * and {@link JobEndpoint#getResult()}
 */
public final class SetJobResult extends AbstractJobEndpointTestCase {
    private Object originalJobResult;

    @Before
    public void getOriginalJobResult() throws Exception {
        Job job = new JobEndpoint(client, jobID);
        assertTrue(job.isEndpointUp());

        originalJobResult = job.getResult();
    }

    @Test
    public void setJobResult() throws Exception {
        Result resultToSet = new Result();
        resultToSet.setValue("Hello");

        Job job = new JobEndpoint(client, jobID);

        job.setResult(resultToSet);
        LinkedHashMap<String, Object> result = job.getResult();

        assertEquals(resultToSet.getValue(), result.get("value"));
    }

    @After
    public void setOriginalJobResult() throws Exception {
        Job job = new JobEndpoint(client, jobID);
        job.setResult(originalJobResult);
    }

    @EqualsAndHashCode
    private static final class Result {
        @Getter
        @Setter
        private String value;
    }
}
