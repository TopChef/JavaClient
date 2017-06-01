package ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper;

import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.Optional;
import java.util.function.Function;

/**
 * Wraps {@link java.net.HttpURLConnection} as a URL connection. I doubt we'll be connecting to other jars via
 * URL anytime soon.
 */
public final class URLConnection implements ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection {

    /**
     * Since {@link java.net} doesn't support PATCH requests, use the method override.
     * This is the name of the header to set with the actual desired method.
     */
    private static final String METHOD_OVERRIDE_HEADER_NAME = "X-HTTP-Method-Override";

    /**
     * The connection that is managed by this wrapper
     */
    private HttpURLConnection connection;

    /**
     *
     * @param connection The connection to wrap
     */
    public URLConnection(HttpURLConnection connection){
        this.connection = connection;
    }

    /**
     * @return The request method that this connection is making
     * @implNote This method will need to be updated if another HTTP method needs to be
     *  added
     */
    @Override
    public HTTPRequestMethod getRequestMethod(){
        String method = this.connection.getRequestMethod().toUpperCase();

        switch (method) {
            case "GET":
                return HTTPRequestMethod.GET;
            case "POST":
                if (isPatchMethod(connection)) {
                    return HTTPRequestMethod.PATCH;
                } else {
                    return HTTPRequestMethod.POST;
                }
            case "PUT":
                return HTTPRequestMethod.PUT;
            default:
                throw new RuntimeException(
                        String.format("Method %s not defined", method)
                );
        }
    }

    /**
     * @param method The desired HTTP method to set
     * @throws ProtocolException if the underlying setter throws a {@link ProtocolException} as well
     * @implNote Make sure to keep this updated as well, as a method changes
     */
    @Override
    public void setRequestMethod(HTTPRequestMethod method) throws ProtocolException {
        switch (method) {
            case GET:
                this.connection.setRequestMethod("GET");
                break;
            case POST:
                this.connection.setRequestMethod("POST");
                break;
            case PATCH:
                this.connection.setRequestMethod("POST");
                this.connection.setRequestProperty(METHOD_OVERRIDE_HEADER_NAME, "PATCH");
                break;
            case PUT:
                this.connection.setRequestMethod("PUT");
                break;
            default:
                throw new ProtocolException("Method not recognized");
        }
    }

    /**
     *
     * @return The appropriate response code for the request. If no code was found, returns the
     * {@link HTTPResponseCode#INTERNAL_SERVER_ERROR} code
     * @throws IOException if the underlying connection is unable to receive the return code
     */
    @Override
    @NotNull
    public HTTPResponseCode getResponseCode() throws IOException {
        Integer response = this.connection.getResponseCode();

        switch (response) {
            case HttpURLConnection.HTTP_OK:
                return HTTPResponseCode.OK;
            case HttpURLConnection.HTTP_CREATED:
                return HTTPResponseCode.CREATED;
            case HttpURLConnection.HTTP_ACCEPTED:
                return HTTPResponseCode.ACCEPTED;
            case HttpURLConnection.HTTP_NO_CONTENT:
                return HTTPResponseCode.NO_CONTENT;
            case HttpURLConnection.HTTP_BAD_REQUEST:
                return HTTPResponseCode.BAD_REQUEST;
            case HttpURLConnection.HTTP_NOT_FOUND:
                return HTTPResponseCode.NOT_FOUND;
            case HttpURLConnection.HTTP_BAD_METHOD:
                return HTTPResponseCode.METHOD_NOT_ALLOWED;
            case HttpURLConnection.HTTP_INTERNAL_ERROR:
                return HTTPResponseCode.INTERNAL_SERVER_ERROR;
            default:
                return HTTPResponseCode.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     *
     * @param key The key for the header
     * @return The value of the header.
     */
    @Override
    @NotNull
    public Optional<String> getRequestProperty(String key){
        return Optional.ofNullable(this.connection.getRequestProperty(key));
    }

    @Override
    public void setRequestProperty(@NotNull String key, String value){
        this.connection.setRequestProperty(key, value);
    }

    /**
     *
     * @throws IllegalStateException If the {@link ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection} has
     * already connected
     * @throws IOException If the connection cannot be done
     */
    @Override
    public void connect() throws IllegalStateException, IOException {
        this.connection.connect();
    }

    /**
     * Close the connection
     */
    @Override
    public void disconnect(){
        this.connection.disconnect();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.connection.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return this.connection.getOutputStream();
    }

    /**
     *
     * Checks if this is a PATCH request by looking for a method override header set to PATCH
     *
     * @param connection The connection to check for being a patch method
     * @return {@link Boolean#TRUE} if the connection is a PATCH request, otherwise {@link Boolean#FALSE}
     */
    @NotNull
    @Contract(pure = true)
    private static Boolean isPatchMethod(HttpURLConnection connection){
        Optional<String> value = Optional.ofNullable(connection.getRequestProperty(METHOD_OVERRIDE_HEADER_NAME));
        return value.map(new IsPatchMethod()).orElse(Boolean.FALSE);
    }

    /**
     * Matcher to determine whether the value of the method override header is PATCH
     */
    private static final class IsPatchMethod implements Function<String, Boolean> {
        /**
         *
         * @param value The value of the header to check
         * @return {@link Boolean#TRUE} if the value is "PATCH", else {@link Boolean#FALSE}.
         */
        @NotNull
        @Contract(pure = true)
        @Override
        public Boolean apply(String value){
            return value.equals("PATCH");
        }
    }
}
