package ca.uwaterloo.iqc.topchef.test.integration.endpoints.service_endpoint;

import ca.uwaterloo.iqc.topchef.endpoints.Service;
import ca.uwaterloo.iqc.topchef.exceptions.ServiceNotFoundException;
import ca.uwaterloo.iqc.topchef.test.integration.endpoints.services_endpoint.AbstractServicesEndpointTestCase;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests that the server can check in and reset the timeout
 */
public final class CheckIn extends AbstractServicesEndpointTestCase {

    @Test
    public void checkIn() throws Exception{
        Service service;
        try {
            service = client.getService(serviceId);
        } catch (ServiceNotFoundException error){
            fail(String.format("Could not find service with the ID %s. Find one that is alive", serviceId));
            throw error;
        } catch (Exception error){
            error.printStackTrace();
            fail(String.format("Checking in service %s threw error %s", serviceId, error));
            throw error;
        }

        assertTrue(service.hasTimedOut());

        service.checkIn();

        assertFalse(service.hasTimedOut());
    }
}
