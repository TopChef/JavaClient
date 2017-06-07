package ca.uwaterloo.iqc.topchef.test.unit.adapters.com.fasterxml.jackson.core;

import ca.uwaterloo.iqc.topchef.test.unit.adapters.AbstractAdaptersTestCase;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.java.lang.StringGenerator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import lombok.Data;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.List;

/**
 * Base class for {@link ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core}
 */
@RunWith(JUnitQuickcheck.class)
public abstract class AbstractJacksonTestCase extends AbstractAdaptersTestCase {
}
