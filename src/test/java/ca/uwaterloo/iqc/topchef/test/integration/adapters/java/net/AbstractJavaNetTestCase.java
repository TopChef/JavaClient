package ca.uwaterloo.iqc.topchef.test.integration.adapters.java.net;

import ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URL;
import ca.uwaterloo.iqc.topchef.test.integration.adapters.AbstractAdaptersTestCase;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.net.MalformedURLException;

/**
 * Base class for {@link ca.uwaterloo.iqc.topchef.adapters.java.net}
 */
public abstract class AbstractJavaNetTestCase extends AbstractAdaptersTestCase {
    protected static final String goodURLString = "https://api.github.com";

    protected static final class BadURLGenerator extends Generator<String> {

        private static final Integer MAXIMUM_RANDOM_STRING_LENGTH = 100;

        public BadURLGenerator(){
            super(String.class);
        }

        @Override
        public String generate(SourceOfRandomness rng, GenerationStatus status){
            String randomString = generateRandomString(rng);

            try {
                new URL(randomString);
                generate(rng, status);
            } catch (MalformedURLException error) {
                return randomString;
            }


            return randomString;
        }

        private static String generateRandomString(SourceOfRandomness rng){
            int length = rng.nextInt(0, MAXIMUM_RANDOM_STRING_LENGTH);
            char[] randomCharacterArray = new char[length];

            for (int index = 0; index < length; index++){
                randomCharacterArray[index] = rng.nextChar('0', 'Z');
            }

            return new String(randomCharacterArray);
        }
    }
}
