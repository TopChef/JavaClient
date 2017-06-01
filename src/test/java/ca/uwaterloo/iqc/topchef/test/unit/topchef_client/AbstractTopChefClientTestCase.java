package ca.uwaterloo.iqc.topchef.test.unit.topchef_client;

import ca.uwaterloo.iqc.topchef.test.unit.AbstractUnitTestCase;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Base class for unit tests of {@link ca.uwaterloo.iqc.topchef.TopChefClient}
 */
public abstract class AbstractTopChefClientTestCase extends AbstractUnitTestCase {

    protected static class URLGenerator extends Generator<URL> {
        private static final Logger log = LoggerFactory.getLogger(URLGenerator.class);

        private static final Generator<String> generator = new URLStringGenerator();

        public URLGenerator(){
            super(URL.class);
        }

        @Override
        public URL generate(SourceOfRandomness rng, GenerationStatus status){
            String urlString = generator.generate(rng, status);

            try {
                return new URL(urlString);
            } catch (MalformedURLException error){
                throw new RuntimeException(error);
            }
        }
    }
}
