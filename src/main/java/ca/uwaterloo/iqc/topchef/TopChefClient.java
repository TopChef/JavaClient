package ca.uwaterloo.iqc.topchef;

import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.url_resolver.Resolver;
import ca.uwaterloo.iqc.topchef.url_resolver.URLResolver;

import java.net.MalformedURLException;

/**
 * Base class for interacting with the TopChef API
 */
public class TopChefClient implements Client {
    private final URL url;

    public TopChefClient(URL url){
        this.url = url;
    }

    public TopChefClient(String url) throws MalformedURLException {
        this.url = new ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URL(url);
    }

    @Override
    public URLResolver getURLResolver(){
        return new Resolver(this.url);
    }
}
