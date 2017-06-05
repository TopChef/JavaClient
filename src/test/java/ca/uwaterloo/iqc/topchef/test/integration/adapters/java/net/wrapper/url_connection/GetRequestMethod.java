package ca.uwaterloo.iqc.topchef.test.integration.adapters.java.net.wrapper.url_connection;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import com.github.dmstocking.optional.java.util.Optional;
import com.github.dmstocking.optional.java.util.function.Consumer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ProtocolException;

import static org.junit.Assert.*;

/**
 * Contains integration tests for
 * {@link ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URLConnection#getRequestMethod()}
 */
public final class GetRequestMethod extends AbstractURLConnectionTestCase {
    private static final Logger log = LoggerFactory.getLogger(GetRequestMethod.class);

    @Test
    public void getDefaultRequestMethod(){
        assertEquals(HTTPRequestMethod.GET, connection.getRequestMethod());
    }

    @Test
    public void testGet(){
        testMethod(HTTPRequestMethod.GET);
    }

    @Test
    public void testPost(){
        testMethod(HTTPRequestMethod.POST);
    }

    @Test
    public void testPatch(){
        testMethod(HTTPRequestMethod.PATCH);
        Optional<String> headerValue = connection.getRequestProperty(METHOD_OVERRIDE_HEADER_NAME);
        assertTrue(headerValue.isPresent());
        headerValue.ifPresent(new EqualMatcher("PATCH"));
    }

    @Test
    public void testNullGet(){
        assertNotNull(connection.getRequestProperty("Invalid-Header"));
    }

    @Test
    public void testPut(){
        testMethod(HTTPRequestMethod.PUT);
    }

    private void testMethod(HTTPRequestMethod method){
        try {
            connection.setRequestMethod(method);
        } catch (ProtocolException error) {
            log.error(error.getClass().getName(), error);
            fail(
                    String.format("Attempting to set Request method threw exception %s", error)
            );
        }

        assertEquals(method, connection.getRequestMethod());
    }

    private static class EqualMatcher implements Consumer<String> {
        private final String expectedValue;

        public EqualMatcher(String expectedValue){
            this.expectedValue = expectedValue;
        }

        @Override
        public void accept(String input){
            assertEquals(expectedValue, input);
        }
    }
}
