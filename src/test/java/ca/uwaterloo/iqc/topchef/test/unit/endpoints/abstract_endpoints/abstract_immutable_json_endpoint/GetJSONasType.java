package ca.uwaterloo.iqc.topchef.test.unit.endpoints.abstract_endpoints.abstract_immutable_json_endpoint;

import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.ObjectMapper;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.ImmutableJSONEndpoint;
import ca.uwaterloo.iqc.topchef.exceptions.*;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import lombok.Getter;
import lombok.Setter;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Contains unit tests for
 * {@link ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.AbstractImmutableJSONEndpoint#getJSON(Class)}
 */
@RunWith(JUnitQuickcheck.class)
public final class GetJSONasType extends AbstractImmutableJSONEndpointTestCase {
    private static final ObjectMapper mapper = new ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core
            .wrapper.ObjectMapper();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Property
    public void getJSON(
            @From(JSONGenerator.class) JSON json
    ) throws Exception {
        Mockery context = new Mockery();
        MockPackage mocks = new MockPackage(context);
        mocks.setConnectionInputStream(new ByteArrayInputStream(mapper.writeValueAsString(json).getBytes()));

        context.checking(new HappyPathExpectations(mocks));

        ImmutableJSONEndpoint endpoint = new ConcreteImmutableJSONEndpoint(mocks.getUrl());
        JSON jsonFromEndpoint = endpoint.getJSON(JSON.class);

        assertEquals(json, jsonFromEndpoint);

        context.assertIsSatisfied();
    }

    @Test
    public void getJSONResourceNotFound() throws Exception {
        runTest(ResourceNotFoundException.class, ResourceNotFoundExpectations.class);
    }

    @Test
    public void getJSONMethodNotAllowed() throws Exception {
        runTest(MethodNotAllowedException.class, MethodNotAllowedExpectations.class);
    }

    @Test
    public void getJSONInternalError() throws Exception {
        runTest(InternalServerErrorException.class, InternalServerErrorExpectations.class);
    }

    @Test
    public void getJSONNoContent() throws Exception {
        runTest(NoContentException.class, NoContentExpectations.class);
    }

    @Test
    public void connectionError() throws Exception {
        Mockery context = new Mockery();
        MockPackage mocks = new MockPackage(context);

        context.checking(new ExpectationsForError(mocks));
        ImmutableJSONEndpoint endpoint = new ConcreteImmutableJSONEndpoint(mocks.getUrl());

        expectedException.expect(IOException.class);
        assertNull(endpoint.getJSON());

        context.assertIsSatisfied();
    }

    private <T extends TestExpectations> void runTest(Class<? extends Exception> exception, Class<T> expectations)
            throws Exception {
        Mockery context = new Mockery();
        MockPackage mocks = new MockPackage(context);
        Constructor<T> constructor = expectations.getConstructor(MockPackage.class);

        context.checking(constructor.newInstance(mocks));

        ImmutableJSONEndpoint endpoint = new ConcreteImmutableJSONEndpoint(mocks.getUrl());

        expectedException.expect(exception);

        assertNull(endpoint.getJSON(JSON.class));

        context.assertIsSatisfied();
    }

    private final class MockPackage {
        @Getter
        @Setter
        private URL url;

        @Getter
        @Setter
        private URLConnection connection;

        @Getter
        @Setter
        private InputStream connectionInputStream;

        public MockPackage(Mockery context){
            url = context.mock(URL.class);
            connection = context.mock(URLConnection.class);
            connectionInputStream = new ByteArrayInputStream("".getBytes());
        }
    }

    private static abstract class TestExpectations extends Expectations {
        public TestExpectations(MockPackage mocks) throws Exception {
            oneOf(mocks.getUrl()).openConnection();
            will(returnValue(mocks.getConnection()));

            oneOf(mocks.getConnection()).connect();
            oneOf(mocks.getConnection()).disconnect();
            oneOf(mocks.getConnection()).setDoOutput(Boolean.FALSE);
            oneOf(mocks.getConnection()).setRequestMethod(HTTPRequestMethod.GET);
            oneOf(mocks.getConnection()).setRequestProperty("Content-Type", "application/json");

            oneOf(mocks.getConnection()).getResponseCode();
            will(returnValue(getResponseCode()));
            oneOf(mocks.getConnection()).getInputStream();
            will(returnValue(mocks.getConnectionInputStream()));
        }

        protected abstract HTTPResponseCode getResponseCode();
    }

    private static class HappyPathExpectations extends TestExpectations {
        public HappyPathExpectations(MockPackage mocks) throws Exception {
            super(mocks);
        }

        @Override
        protected HTTPResponseCode getResponseCode(){
            return HTTPResponseCode.OK;
        }
    }

    private static class ResourceNotFoundExpectations extends TestExpectations {
        public ResourceNotFoundExpectations(MockPackage mocks) throws Exception {
            super(mocks);
        }

        @Override
        protected HTTPResponseCode getResponseCode(){
            return HTTPResponseCode.NOT_FOUND;
        }
    }

    private static class MethodNotAllowedExpectations extends TestExpectations {
        public MethodNotAllowedExpectations(MockPackage mocks) throws Exception {
            super(mocks);
        }

        @Override
        protected HTTPResponseCode getResponseCode(){
            return HTTPResponseCode.METHOD_NOT_ALLOWED;
        }
    }

    private static class InternalServerErrorExpectations extends TestExpectations {
        public InternalServerErrorExpectations(MockPackage mocks) throws Exception {
            super(mocks);
        }

        @Override
        protected HTTPResponseCode getResponseCode(){
            return HTTPResponseCode.INTERNAL_SERVER_ERROR;
        }
    }

    private static class NoContentExpectations extends TestExpectations {
        public NoContentExpectations(MockPackage mocks) throws Exception {
            super(mocks);
        }

        @Override
        protected HTTPResponseCode getResponseCode(){
            return HTTPResponseCode.NO_CONTENT;
        }
    }

    private static class ExpectationsForError extends Expectations {
        public ExpectationsForError(MockPackage mocks) throws Exception {
            oneOf(mocks.getUrl()).openConnection();
            will(throwException(new HTTPConnectionCastException("kaboom")));
        }
    }
}
