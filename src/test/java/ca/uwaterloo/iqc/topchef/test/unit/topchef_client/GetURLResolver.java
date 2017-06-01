package ca.uwaterloo.iqc.topchef.test.unit.topchef_client;

import ca.uwaterloo.iqc.topchef.Client;
import ca.uwaterloo.iqc.topchef.TopChefClient;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.url_resolver.URLResolver;
import org.jmock.Mockery;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Contains unit tests for {@link TopChefClient#getURLResolver()}
 */
public final class GetURLResolver extends AbstractTopChefClientTestCase {
    private static final Mockery context = new Mockery();

    private static final URL url = context.mock(URL.class);

    /**
     * Tests that the resolver and client have the same URL
     */
    @Test
    public void getURLResolver(){
        Client client = new TopChefClient(url);
        URLResolver resolver = client.getURLResolver();
        assertEquals(resolver.getBaseURL(), client.getURL());
    }
}
