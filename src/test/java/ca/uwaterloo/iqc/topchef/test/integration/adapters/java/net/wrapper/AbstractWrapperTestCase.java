package ca.uwaterloo.iqc.topchef.test.integration.adapters.java.net.wrapper;

import ca.uwaterloo.iqc.topchef.test.integration.adapters.java.net.AbstractJavaNetTestCase;

/**
 * Base class for tests of the {@link java.net} URL connection wrapper
 */
public abstract class AbstractWrapperTestCase extends AbstractJavaNetTestCase{
    protected static final String METHOD_OVERRIDE_HEADER_NAME = "X-HTTP-Method-Override";
}
