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

    /**
     * A URL for an API that I can contact and make requests using the adapter.
     */
    protected static final String goodURLString = "https://api.github.com";

    /**
     * Makes bad URLs.
     */
    protected static final class BadURLGenerator extends Generator<String> {

        /**
         * The maximum length that a random string should be
         */
        private static final Integer MAXIMUM_RANDOM_STRING_LENGTH = 100;

        /**
         * Default constructor
         */
        public BadURLGenerator(){
            super(String.class);
        }

        /**
         *
         * @param rng The random number generator
         * @param status The current status of generating the variable
         * @return A random string that is not a URL
         */
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

        /**
         *
         * @param rng The random number generator
         * @return A random string
         */
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
