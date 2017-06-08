package ca.uwaterloo.iqc.topchef.test.unit;

import ca.uwaterloo.iqc.topchef.adapters.java.net.wrapper.URL;
import ca.uwaterloo.iqc.topchef.test.AbstractTestCase;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.java.lang.AbstractStringGenerator;
import com.pholser.junit.quickcheck.generator.java.lang.StringGenerator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import lombok.Data;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Base class for all unit tests in the project.
 */
public abstract class AbstractUnitTestCase extends AbstractTestCase {

    /**
     * The log to use for writing down interesting things related to tests
     */
    private static final Logger log = LoggerFactory.getLogger(AbstractUnitTestCase.class);

    /**
     * A simple JSON object that contains a string entry
     */
    @Data
    protected static final class SimpleJSONObject {
        /**
         * A string representing some data associated with this JSON object
         */
        private String someData;
    }


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
                throw new IllegalStateException(error);
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

    /**
     * A more complicated JSON object containing a diversity of data types that need to
     * be serialized correctly
     */
    @Data
    protected static final class ComplexJSON {
        /**
         * A random string
         */
        private String string;

        /**
         * An integer
         */
        private Integer integer;

        /**
         * A floating-point number
         */
        private Number number;

        /**
         * A boolean value
         */
        private Boolean boolean_;

        /**
         * A list of strings
         */
        private List<String> listOfStrings;

        /**
         * A nested JSON object
         */
        private SimpleJSONObject nestedJSON;
    }

    /**
     * Generate a simple JSON object
     */
    protected static final class SimpleJSONGenerator extends Generator<SimpleJSONObject> {

        /**
         * I need a random string generator. Fortunately, JUnit-Quickcheck provides one free of charge
         */
        private static final Generator<String> randomStringGenerator = new StringGenerator();

        /**
         * Register this generator with Quickcheck, letting it know that I'm generating this type
         */
        public SimpleJSONGenerator(){
            super(SimpleJSONObject.class);
        }

        /**
         *
         * @param rng A random variable generator
         * @param status The current generation status
         * @return A randomly-generated simple JSON object
         */
        @Override
        public SimpleJSONObject generate(SourceOfRandomness rng, GenerationStatus status){
            SimpleJSONObject object = new SimpleJSONObject();
            object.setSomeData(randomStringGenerator.generate(rng, status));
            return object;
        }
    }

    /**
     * Generate some more complicated JSON
     */
    protected static final class ComplexJSONGenerator extends Generator<ComplexJSON> {
        /**
         * A generator of random strings
         */
        private static final Generator<String> randomStringGenerator = new StringGenerator();

        /**
         * A generator of a simple JSON object to nest inside this more complicated JSON object
         */
        private static final Generator<SimpleJSONObject> objectGenerator = new SimpleJSONGenerator();

        /**
         * Let Quickcheck know of this generator
         */
        public ComplexJSONGenerator(){
            super(ComplexJSON.class);
        }

        /**
         *
         * @param rng A source of random variables
         * @param status The generation status
         * @return A randomly-generated JSON object
         */
        @Override
        public ComplexJSON generate(SourceOfRandomness rng, GenerationStatus status){
            ComplexJSON object = new ComplexJSON();

            object.setBoolean_(rng.nextBoolean());
            object.setNumber(rng.nextDouble());
            object.setInteger(rng.nextInt());
            object.setListOfStrings(getAListOfStrings(rng, status));
            object.setNestedJSON(objectGenerator.generate(rng, status));

            return object;
        }

        /**
         *
         * @param rng A source of random values
         * @param status The current generation status
         * @return A list of random strings
         */
        private List<String> getAListOfStrings(SourceOfRandomness rng, GenerationStatus status){
            List<String> stringList = new LinkedList<String>();

            Integer numberOfStringsToAdd = rng.nextInt(0, 100);

            for (Integer index = 0; index < numberOfStringsToAdd; index++){
                stringList.add(randomStringGenerator.generate(rng, status));
            }

            return stringList;
        }
    }

    protected static final class UUIDGenerator extends Generator<UUID>{
        public UUIDGenerator(){
            super(UUID.class);
        }

        @NotNull
        @Override
        public UUID generate(SourceOfRandomness rng, GenerationStatus status){
            return UUID.randomUUID();
        }
    }
}
