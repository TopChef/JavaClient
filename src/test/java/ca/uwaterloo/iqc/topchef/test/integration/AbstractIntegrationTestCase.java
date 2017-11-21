package ca.uwaterloo.iqc.topchef.test.integration;

import ca.uwaterloo.iqc.topchef.test.AbstractTestCase;

/**
 * Base class for all integration tests. Modify the values here to change the service and job
 * against which the integration suite is to be tested
 */
public abstract class AbstractIntegrationTestCase extends AbstractTestCase {
    protected static final String apiUrl = "http://localhost:5000";

    protected static final String serviceId = "8dbb916e-e389-458f-8706-f0b62947545b";

    protected static final String jobID = "a93f1c28-e3a5-4f74-ace4-4f85a2bc26d8";
}
