package ca.uwaterloo.iqc.topchef.test.integration;

import ca.uwaterloo.iqc.topchef.test.AbstractTestCase;

/**
 * Base class for all integration tests.
 */
public abstract class AbstractIntegrationTestCase extends AbstractTestCase {
    protected static final String apiUrl = "http://localhost:5000";

    protected static final String serviceId = "3e2c3b6e-4d38-11e7-a611-3c970e7271f5";

    protected static final String expectedServiceName = "TestService";

    protected static final String jobID = "5caedeca-4d38-11e7-a611-3c970e7271f5";
}
