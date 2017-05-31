package ca.uwaterloo.iqc.topchef.test.unit.adapters.java.net;

import ca.uwaterloo.iqc.topchef.adapters.java.net.URL;
import ca.uwaterloo.iqc.topchef.test.unit.adapters.AbstractAdaptersTestCase;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;


/**
 * Base class for unit tests of {@link ca.uwaterloo.iqc.topchef.adapters.java.net}
 */
public abstract class AbstractJavaNetTestCase extends AbstractAdaptersTestCase {

    public interface TestParameters {
        URL getBaseURL();
        URL getExpectedURL();
        String getStringToAppendToURL();
    }

    public static final class GeneratedParameters implements TestParameters {
        private final URL baseURL;
        private final String appendedText;
        private final URL expectedURL;

        public GeneratedParameters(URL baseURL, String appendedText, URL expectedURL){
            this.baseURL = baseURL;
            this.appendedText = appendedText;
            this.expectedURL = expectedURL;
        }

        @Override
        public URL getBaseURL(){
            return this.baseURL;
        }

        @Override
        public URL getExpectedURL(){
            return this.expectedURL;
        }

        @Override
        public String getStringToAppendToURL(){
            return this.appendedText;
        }
    }

    protected static abstract class TestParametersGenerator extends Generator<TestParameters> {
        private static final Logger log = LoggerFactory.getLogger(TestParametersGenerator.class);

        private static final URLStringGenerator urlStringGenerator = new URLStringGenerator();

        public TestParametersGenerator(){
            super(TestParameters.class);
        }

        @NotNull
        protected abstract String getAppendedText(SourceOfRandomness rng, GenerationStatus status);

        @NotNull
        protected abstract String getAppendedURLString(String baseURL, String appendedText);

        @Override
        public TestParameters generate(SourceOfRandomness rng, GenerationStatus status) throws RuntimeException {
            String appendedText = getAppendedText(rng, status);

            URL baseURL;
            URL appendedURL;

            String baseURLString = urlStringGenerator.generate(rng, status);
            String appendedURLString = getAppendedURLString(baseURLString, appendedText);

            try {
                baseURL = new ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URL(baseURLString);
                appendedURL = new ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URL(appendedURLString);
            } catch (MalformedURLException error){
                log.error("Attempting to generate random data threw error", error);
                throw new RuntimeException(error);
            }

            return new GeneratedParameters(baseURL, appendedText, appendedURL);
        }

        protected static String randomString(SourceOfRandomness rng, GenerationStatus status){
            return urlStringGenerator.generate(rng, status);
        }
    }

    public static final class AppendedURLGenerator extends TestParametersGenerator {
        @NotNull
        @Override
        protected String getAppendedText(SourceOfRandomness rng, GenerationStatus status){
            return randomString(rng, status);
        }

        @NotNull
        @Override
        protected String getAppendedURLString(String baseURL, String appendedText){
            return String.format("%s/%s", baseURL, appendedText);
        }
    }

    public static final class LeadingSlashURLGenerator extends TestParametersGenerator {
       @NotNull
       @Override
       protected String getAppendedText(SourceOfRandomness rng, GenerationStatus status){
           return "/".concat(randomString(rng, status));
       }

       @NotNull
       @Override
       protected String getAppendedURLString(String baseURL, String appendedText){
           return String.format("%s%s", baseURL, appendedText);
       }
    }
}
