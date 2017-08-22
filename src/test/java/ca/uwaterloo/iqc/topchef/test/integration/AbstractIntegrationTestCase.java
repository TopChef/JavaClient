package ca.uwaterloo.iqc.topchef.test.integration;

import ca.uwaterloo.iqc.topchef.test.AbstractTestCase;

/**
 * Base class for all integration tests.
 */
public abstract class AbstractIntegrationTestCase extends AbstractTestCase {
    protected static final String apiUrl = "http://localhost:5000";

    protected static final String serviceId = "495d76fd-044c-4f02-8815-5ec6e7634330";

    protected static final String expectedServiceName = "Testing Service";

    protected static final String jobID = "42094fe4-9c71-4d6e-94fd-7ed6e2b46ce7";
}
