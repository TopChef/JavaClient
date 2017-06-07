package ca.uwaterloo.iqc.topchef.endpoints;

import ca.uwaterloo.iqc.topchef.Client;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPRequestMethod;
import ca.uwaterloo.iqc.topchef.adapters.java.net.HTTPResponseCode;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URLConnection;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.AbstractEndpoint;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPConnectionCastException;
import ca.uwaterloo.iqc.topchef.exceptions.ServiceNotFoundException;
import org.jetbrains.annotations.Contract;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Implements the service endpoint for the TopChef API
 */
public class ServicesEndpoint extends AbstractEndpoint implements Services {

    private static final Logger log = LoggerFactory.getLogger(ServicesEndpoint.class);

    /**
     * The client for which this endpoint operates
     */
    private final Client client;

    /**
     *
     * @param client The client for the API that this endpoint contacts.
     */
    public ServicesEndpoint(Client client){
        super(client.getURLResolver().getServicesEndpoint());
        this.client = client;
    }

    /**
     *
     * @return The list of services that the API recognizes
     * @throws IOException If connection to the API could not be established
     */
    @Override
    public List<Service> getServices() throws IOException {
        URLConnection connection = getConnectionToServicesGetter();
        connection.connect();
        assertGoodConnection(connection);
        JSONArray response;

        try {
            response = readDataFromConnection(connection);
        } catch (ParseException error){
            log.error("Reading response from request threw error", error);
            throw new IOException(error);
        }

        connection.disconnect();

        try {
            return parseResponse(response);
        } catch (ParseException error){
            log.error("Parsing response from request threw error", error);
            throw new IOException(error);
        }
    }

    /**
     *
     * @param serviceID The ID of the service to find
     * @return The service
     * @throws ServiceNotFoundException If the service is not found
     * @throws IOException If I/O to the API cannot be established
     */
    @Override
    public Service getServiceByUUID(UUID serviceID) throws ServiceNotFoundException, IOException {
        Service service = new ServiceEndpoint(client, serviceID);
        Boolean doesServiceExist;

        try {
            doesServiceExist = service.isEndpointUp();
        } catch (HTTPConnectionCastException error){
            throw new RuntimeException(error);
        }

        if (!doesServiceExist){
            throw new ServiceNotFoundException(
                    String.format(
                            "The service with id %s could not be found.", serviceID.toString()
                    ));
        } else {
            return service;
        }
    }

    /**
     *
     * @param serviceID The desired service ID
     * @return The service
     * @throws ServiceNotFoundException If the service cannot be found
     * @throws IOException If I/O to the API server cannot be established
     * @throws IllegalArgumentException If the string entered is not a UUID
     */
    @Override public Service getServiceByUUID(String serviceID) throws ServiceNotFoundException, IOException,
            IllegalArgumentException {
        return getServiceByUUID(UUID.fromString(serviceID));
    }

    /**
     *
     * @return An open connection to the API
     * @throws IOException If the connection cannot be established
     */
    private URLConnection getConnectionToServicesGetter() throws IOException {
        URLConnection connection;

        try {
            connection = this.getURL().openConnection();
        } catch (HTTPConnectionCastException error){
            throw new RuntimeException(error);
        }

        connection.setRequestMethod(HTTPRequestMethod.GET);
        connection.setDoOutput(Boolean.FALSE);
        connection.setRequestProperty("Content-Type", "application/json");

        return connection;
    }

    /**
     *
     * @param connection Check that the connection is open
     * @throws IOException If the connection does not return the correct status code
     */
    private static void assertGoodConnection(URLConnection connection) throws IOException {
        HTTPResponseCode code = connection.getResponseCode();

        if (code != HTTPResponseCode.OK){
            throw new IOException(
                    String.format("Expected OK response, but response was actually %s", code)
            );
        }
    }

    /**
     *
     * @param connection The connection to use
     * @return The parsed data
     * @throws IOException If the connection cannot be established
     * @throws ParseException If the JSON cannot be parsed
     */
    private static JSONArray readDataFromConnection(URLConnection connection) throws IOException, ParseException {
        InputStream requestBody = connection.getInputStream();
        Reader requestBodyReader = new BufferedReader(new InputStreamReader(requestBody));

        JSONParser parser = new JSONParser();

        JSONObject data = (JSONObject) parser.parse(requestBodyReader);

        return (JSONArray) data.get("data");
    }

    /**
     *
     * @param response The response
     * @return The service list
     * @throws ParseException If the response cannot be parsed
     */
    @Contract("null -> fail")
    private List<Service> parseResponse(Object response) throws ParseException {
        List<Service> data = new LinkedList<Service>();

        JSONArray serviceList;
        if (response instanceof JSONArray){
            serviceList = (JSONArray) response;
        } else {
            throw new ParseException(ParseException.ERROR_UNEXPECTED_EXCEPTION, response);
        }

        for (Object service: serviceList){
            if (service instanceof JSONObject) {
                JSONObject serviceObject = (JSONObject) service;
                data.add(new ServiceEndpoint(this.client, serviceObject.get("id").toString()));
            } else {
                throw new ParseException(0, ParseException.ERROR_UNEXPECTED_CHAR, service);
            }
        }

        return data;
    }
}
