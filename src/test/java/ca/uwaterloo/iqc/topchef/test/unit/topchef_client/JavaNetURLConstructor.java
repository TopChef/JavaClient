package ca.uwaterloo.iqc.topchef.test.unit.topchef_client;

import ca.uwaterloo.iqc.topchef.Client;
import ca.uwaterloo.iqc.topchef.TopChefClient;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;

import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * Contains unit tests for {@link ca.uwaterloo.iqc.topchef.TopChefClient#TopChefClient(URL)}
 */
@RunWith(JUnitQuickcheck.class)
public final class JavaNetURLConstructor extends AbstractTopChefClientTestCase {
    @Property
    public void testConstructor(
            @From(URLGenerator.class) URL url
    ){
        Client client = new TopChefClient(url);
        ca.uwaterloo.iqc.topchef.adapters.java.net.URL wrappedURL = new ca.uwaterloo.iqc.topchef.adapters.java.net
                .wrapper.URL(url);

        assertEquals(wrappedURL.toString(), client.getURL().toString());
    }
}
