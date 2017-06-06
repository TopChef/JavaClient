package ca.uwaterloo.iqc.topchef.test.unit.adapters.com.fasterxml.jackson.core;

import ca.uwaterloo.iqc.topchef.test.unit.adapters.AbstractAdaptersTestCase;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.java.lang.StringGenerator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * Base class for {@link ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core}
 */
public abstract class AbstractJacksonTestCase extends AbstractAdaptersTestCase {

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
     * A more complicated JSON object containing a diversity of data types that need to
     * be serialized correctly
     */
    @Data
    protected static final class OneOfEverythingJSON {
        /**
         * A random string
         */
        private String aString;

        /**
         * An integer
         */
        private Integer anInteger;

        /**
         * A floating-point number
         */
        private Number aNumber;

        /**
         * A boolean value
         */
        private Boolean aBoolean;

        /**
         * A list of strings
         */
        private List<String> aListOfStrings;

        /**
         * A nested JSON object
         */
        private SimpleJSONObject aNestedJSON;
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
    protected static final class ComplexJSONGenerator extends Generator<OneOfEverythingJSON> {
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
            super(OneOfEverythingJSON.class);
        }

        /**
         *
         * @param rng A source of random variables
         * @param status The generation status
         * @return A randomly-generated JSON object
         */
        @Override
        public OneOfEverythingJSON generate(SourceOfRandomness rng, GenerationStatus status){
            OneOfEverythingJSON object = new OneOfEverythingJSON();

            object.setABoolean(rng.nextBoolean());
            object.setANumber(rng.nextDouble());
            object.setAnInteger(rng.nextInt());
            object.setAListOfStrings(getAListOfStrings(rng, status));
            object.setANestedJSON(objectGenerator.generate(rng, status));

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
}
