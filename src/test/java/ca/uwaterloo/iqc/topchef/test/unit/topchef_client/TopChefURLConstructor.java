package ca.uwaterloo.iqc.topchef.test.unit.topchef_client;

import ca.uwaterloo.iqc.topchef.Client;
import ca.uwaterloo.iqc.topchef.TopChefClient;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.url_resolver.URLResolver;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * Contains unit tests for {@link ca.uwaterloo.iqc.topchef.TopChefClient#TopChefClient(URL)}
 */
public final class TopChefURLConstructor extends AbstractTopChefClientTestCase {
    private static final Mockery context = new Mockery();

    private static final URL url = context.mock(URL.class);

    @Test
    public void testConstructor() throws Exception {

        context.checking(new ExpectationsForTest());

        Client client = new TopChefClient(url);
        URLResolver resolver = client.getURLResolver();
        resolver.getValidatorEndpoint();

        context.assertIsSatisfied();
    }

    private class ExpectationsForTest extends Expectations {
        public ExpectationsForTest() throws Exception {
            oneOf(url).getRelativeURL(with(any(String.class)));
        }
    }
}
