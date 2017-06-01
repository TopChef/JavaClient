package ca.uwaterloo.iqc.topchef.test.unit.topchef_client;

import ca.uwaterloo.iqc.topchef.Client;
import ca.uwaterloo.iqc.topchef.TopChefClient;
import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;

/**
 * Contains unit tests for {@link ca.uwaterloo.iqc.topchef.TopChefClient#TopChefClient(String)}
 */
@RunWith(JUnitQuickcheck.class)
public final class StringConstructor extends AbstractTopChefClientTestCase {
    private static final Logger log = LoggerFactory.getLogger(StringConstructor.class);

    @Property
    public void stringConstructorNoError(
            @From(URLStringGenerator.class) String url
    ) throws RuntimeException {
        Client client;
        URL wrappedURL;

        try {
            client = new TopChefClient(url);
            wrappedURL = new ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URL(url);
        } catch (MalformedURLException error){
            throw new RuntimeException(error);
        }

        assertEquals(wrappedURL.toString(), client.getURL().toString());
    }
}
