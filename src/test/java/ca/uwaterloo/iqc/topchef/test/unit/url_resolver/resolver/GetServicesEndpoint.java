package ca.uwaterloo.iqc.topchef.test.unit.url_resolver.resolver;

import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.test.unit.url_resolver.AbstractURLResolverTestCase;
import ca.uwaterloo.iqc.topchef.url_resolver.Resolver;
import ca.uwaterloo.iqc.topchef.url_resolver.URLResolver;
import lombok.Getter;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Contains unit tests for {@link Resolver#getServicesEndpoint()}
 */
public final class GetServicesEndpoint extends AbstractURLResolverTestCase {
    @Test
    public void getServicesEndpoint() throws Exception {
        Mockery context = new Mockery();
        MockPackage mocks = new MockPackage(context);

        context.checking(new ExpectationsForTest(mocks));

        URLResolver resolver = new Resolver(mocks.getBaseURL());
        assertEquals(mocks.getRelativeURL(), resolver.getServicesEndpoint());

        context.assertIsSatisfied();
    }

    private static final class MockPackage {
        @Getter
        private URL baseURL;

        @Getter
        public URL relativeURL;

        public MockPackage(Mockery context){
            baseURL = context.mock(URL.class);
            relativeURL = context.mock(URL.class, "relativeURL");
        }
    }

    private static final class ExpectationsForTest extends Expectations {
        public ExpectationsForTest(MockPackage mocks) throws Exception {
            oneOf(mocks.getBaseURL()).getRelativeURL("/services");
            will(returnValue(mocks.getRelativeURL()));
        }
    }
}
