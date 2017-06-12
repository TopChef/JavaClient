package ca.uwaterloo.iqc.topchef.test.unit.endpoints.job_endpoint;

import ca.uwaterloo.iqc.topchef.endpoints.Job;
import ca.uwaterloo.iqc.topchef.endpoints.JobEndpoint;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.runner.RunWith;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Contains unit tests for {@link JobEndpoint#getID()}
 */
@RunWith(JUnitQuickcheck.class)
public final class GetID extends AbstractJobEndpointTestCase {
    @Property
    public void getID(@From(UUIDGenerator.class) UUID uuid){
        Mockery context = new Mockery();
        MockPackage mocks = new MockPackage(context);

        context.checking(new ExpectationsForGetID(mocks, uuid));

        Job job = new JobEndpoint(mocks.getClient(), uuid);

        assertEquals(uuid, job.getID());

        context.assertIsSatisfied();
    }

    private static final class ExpectationsForGetID extends Expectations {
        public ExpectationsForGetID(MockPackage mocks, UUID jobID){
            oneOf(mocks.getClient()).getURLResolver();
            will(returnValue(mocks.getResolver()));

            oneOf(mocks.getResolver()).getJobEndpoint(jobID);
            will(returnValue(mocks.getUrl()));
        }
    }
}
