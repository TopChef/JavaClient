package ca.uwaterloo.iqc.topchef.test.integration.endpoints;

import ca.uwaterloo.iqc.topchef.Client;
import ca.uwaterloo.iqc.topchef.TopChefClient;
import ca.uwaterloo.iqc.topchef.test.integration.AbstractIntegrationTestCase;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

/**
 * Base class for {@link ca.uwaterloo.iqc.topchef.endpoints}
 */
public abstract class AbstractEndpointModelsTestCase extends AbstractIntegrationTestCase {
    private static final Logger log = LoggerFactory.getLogger(AbstractEndpointModelsTestCase.class);

    protected Client client;

    @Before
    public void setBaseURL() throws RuntimeException {
        try {
            client = new TopChefClient("http://localhost:5000");
        } catch (MalformedURLException error) {
            log.error("Bad URL for integration test", error);
            throw new RuntimeException(error);
        }
    }
}
