package ca.uwaterloo.iqc.topchef.endpoints;

import ca.uwaterloo.iqc.topchef.Client;
import ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints.AbstractEndpoint;

import java.util.UUID;

/**
 * Created by mkononen on 02/06/17.
 */
public class ServiceEndpoint extends AbstractEndpoint implements Service {
    private final UUID id;

    public ServiceEndpoint(Client client, UUID id){
        super(client.getURLResolver().getServiceEndpoint(id));
        this.id = id;
    }

    public ServiceEndpoint(Client client, String id) throws IllegalArgumentException {
        super(client.getURLResolver().getServiceEndpoint(UUID.fromString(id)));
        this.id = UUID.fromString(id);
    }

    @Override
    public UUID getServiceID(){
        return this.id;
    }
}
