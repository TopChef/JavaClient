package ca.uwaterloo.iqc.topchef.test.integration.adapters.java.net.wrapper.url;

import ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URL;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.net.MalformedURLException;

/**
 * Contains unit tests for {@link ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URL#URL(String)}
 */
@RunWith(JUnitQuickcheck.class)
public final class Constructor extends AbstractURLTestCase {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Property
    public void badURLThrowsMalformedException(
            @From(BadURLGenerator.class) String badURL
        ) throws Exception {
        exception.expect(MalformedURLException.class);
        new URL(badURL);
    }
}
