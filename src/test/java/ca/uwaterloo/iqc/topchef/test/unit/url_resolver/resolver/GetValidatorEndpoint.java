package ca.uwaterloo.iqc.topchef.test.unit.url_resolver.resolver;

import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.url_resolver.Resolver;
import ca.uwaterloo.iqc.topchef.url_resolver.URLResolver;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.MalformedURLException;

/**
 * Contains unit tests for {@link Resolver#getValidatorEndpoint()}
 */
public class GetValidatorEndpoint {
    private static final Mockery context = new Mockery();

    private static final URL baseURL = context.mock(URL.class);

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private URLResolver resolver;

    @Before
    public void getResolver(){
        resolver = new Resolver(baseURL);
    }

    @Test
    public void getValidatorEndpointNoError() throws Exception {
        context.checking(new ExpectationsForHappyPath());
        resolver.getValidatorEndpoint();
        context.assertIsSatisfied();
    }

    @Test
    public void getValidatorEndpointWithError() throws Exception {
        context.checking(new ExpectationsForError());

        exception.expect(RuntimeException.class);

        resolver.getValidatorEndpoint();

        context.assertIsSatisfied();
    }

    private final class ExpectationsForHappyPath extends Expectations {
        private ExpectationsForHappyPath() throws Exception {
            oneOf(baseURL).getRelativeURL("/validator");
        }
    }

    private final class ExpectationsForError extends Expectations {
        private ExpectationsForError() throws Exception {
            oneOf(baseURL).getRelativeURL("/validator");
            will(throwException(new MalformedURLException("Kaboom")));
        }
    }
}
