package ca.uwaterloo.iqc.topchef.test.integration.endpoints.services_endpoint;

import ca.uwaterloo.iqc.topchef.endpoints.Service;
import ca.uwaterloo.iqc.topchef.endpoints.ServiceEndpoint;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Contains a test to check that data can be retrieved from the server.
 */
public final class GetDataFromServer extends AbstractServicesEndpointTestCase {
    @Test
    public void nonEmptyServiceList() throws Exception {
        assertTrue(client.getServices().size() > 0);
    }

    @Test
    public void canGetIndividualService() throws Exception {
        List<Service> services = client.getServices();
        Service service = services.get(0);

        Service constructedService = new ServiceEndpoint(client, service.getServiceID());

        assertEquals(service.getServiceID(), constructedService.getServiceID());
    }
}
