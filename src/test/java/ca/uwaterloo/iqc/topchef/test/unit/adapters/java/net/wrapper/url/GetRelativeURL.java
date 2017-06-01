package ca.uwaterloo.iqc.topchef.test.unit.adapters.java.net.wrapper.url;

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
 * Contains unit tests for {@link ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URL#getRelativeURL(String)}
 */
@RunWith(JUnitQuickcheck.class)
public final class GetRelativeURL extends AbstractURLTestCase {
    private static final Logger log = LoggerFactory.getLogger(GetRelativeURL.class);

    @Property
    public void noLeadingSlash(
            @From(AppendedURLGenerator.class) TestParameters parameters
    ) throws RuntimeException {
        URL relativeURL;
        try {
            relativeURL = parameters.getBaseURL().getRelativeURL(parameters.getStringToAppendToURL());
        } catch (MalformedURLException error){
            log.error("Test threw error.", error);
            throw new RuntimeException(error);
        }

        assertEquals(parameters.getExpectedURL().toString(), relativeURL.toString());
    }

    @Property
    public void leadingSlash(
        @From(LeadingSlashURLGenerator.class) TestParameters parameters
    ) throws RuntimeException {
        URL relativeURL;
        try {
            relativeURL = parameters.getBaseURL().getRelativeURL(parameters.getStringToAppendToURL());
        } catch (MalformedURLException error){
            log.error("Test threw error.", error);
            throw new RuntimeException(error);
        }

        assertEquals(parameters.getExpectedURL().toString(), relativeURL.toString());
    }
}
