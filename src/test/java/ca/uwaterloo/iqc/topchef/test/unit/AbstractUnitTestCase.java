package ca.uwaterloo.iqc.topchef.test.unit;

import ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URL;
import ca.uwaterloo.iqc.topchef.test.AbstractTestCase;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.java.lang.AbstractStringGenerator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

/**
 * Base class for all unit tests in the project.
 */
public abstract class AbstractUnitTestCase extends AbstractTestCase {

    /**
     * The log to use for writing down interesting things related to tests
     */
    private static final Logger log = LoggerFactory.getLogger(AbstractUnitTestCase.class);

    /**
     * A generator for making strings that are safe to use in the URL.
     * {@link com.pholser.junit.quickcheck.generator.java.lang.StringGenerator} draws from
     * Unicode, and so includes characters that are not safe to put into an RFC 2986-compliant URI. This generator
     * fixes the issue.
     *
     * Currently, Quicheck chooses the next character based on a random integer. It's not a uniform deviate
     * because, depending on the value of the integer, some special characters are picked. This can be done better
     *
     */
    public static final class WebSafeStringGenerator extends AbstractStringGenerator {
        /**
         * Returns a random web-safe character for concatenation into a string
         * @param random The random number generator to ues for making the character
         * @return The Unicode number of the next character
         */
        @Override
        protected int nextCodePoint(SourceOfRandomness random){
            Character nextCharacter;

            switch (random.nextInt(0, 4)) {
                case 0:
                    nextCharacter = random.nextChar('A', 'Z');
                    break;
                case 1:
                    nextCharacter = random.nextChar('a', 'z');
                    break;
                case 2:
                    nextCharacter = random.nextChar('0', '9');
                    break;
                case 3:
                    nextCharacter = '%';
                    break;
                case 4:
                    nextCharacter = '-';
                    break;
                default:
                    nextCharacter = '0';
                    break;
            }

            return nextCharacter.hashCode();
        }

        /**
         * This has something to do with shrinking the string after a test fails, I think.
         * I set the return value to {@link Boolean#FALSE} so that no shrinking happens after a test
         * fails.
         *
         * @param codePoint the numerical ASCII value of the character
         * @return {@link Boolean#FALSE}
         */
        @Contract(value = "_ -> false", pure = true)
        @Override
        public boolean codePointInRange(int codePoint){
            return false;
        }
    }

    /**
     * Generates web-safe strings formatted into URLs
     */
    public static final class URLStringGenerator extends Generator<String> {
        /**
         * The web-safe string generator to make strings
         */
        private static final AbstractStringGenerator stringGenerator = new WebSafeStringGenerator();

        /**
         *
         */
        public URLStringGenerator(){
            super(String.class);
        }

        /**
         * Make a random URL
         *
         * @param rng A source of randomness that will be used to make random things
         * @param status The current generation status
         * @return A random URL
         */
        @Override
        public String generate(SourceOfRandomness rng, GenerationStatus status){
            String protocol = randomProtocol(rng);
            String host = stringGenerator.generate(rng, status);
            Integer port = rng.nextInt(1, 65535);

            String baseURLString = String.format("%s://%s:%d", protocol, host, port);

            try {
                new URL(baseURLString);
            } catch (MalformedURLException error){
                log.error("Attempting to generate random data threw error", error);
                throw new RuntimeException(error);
            }

            return baseURLString;
        }

        /**
         * Choose between http or https as a random URL protocol
         *
         * @param rng The source of randomness
         * @return The name of the protocol.
         */
        @NotNull
        private static String randomProtocol(SourceOfRandomness rng){
            if (rng.nextBoolean()){
                return "http";
            } else {
                return "https";
            }
        }
    }
}
