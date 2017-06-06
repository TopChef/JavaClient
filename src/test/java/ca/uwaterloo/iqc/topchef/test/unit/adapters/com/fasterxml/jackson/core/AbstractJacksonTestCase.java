package ca.uwaterloo.iqc.topchef.test.unit.adapters.com.fasterxml.jackson.core;

import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.ObjectMapper;
import ca.uwaterloo.iqc.topchef.test.unit.adapters.AbstractAdaptersTestCase;
import lombok.Data;
import org.junit.Before;

import java.util.LinkedList;
import java.util.List;

/**
 * Base class for {@link ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core}
 */
public abstract class AbstractJacksonTestCase extends AbstractAdaptersTestCase {

    protected MarshalledJSON stringJSONinstance = new MarshalledJSON();

    protected NestedJSON nestedJSON = new NestedJSON();

    protected ObjectMapper mapper = new ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.wrapper
            .ObjectMapper();

    @Before
    public void setStringJSONinstance(){
        stringJSONinstance.setSomeData("foo");
    }

    @Before
    public void setNestedJSON(){
        nestedJSON.setDataKey(stringJSONinstance);

        List<MarshalledJSON> listType = new LinkedList<MarshalledJSON>();
        listType.add(stringJSONinstance);
        nestedJSON.setDataArray(listType);

    }

    @Data
    protected static final class MarshalledJSON {
        private String someData;
    }

    @Data
    protected static final class NestedJSON {
        private List<MarshalledJSON> dataArray;
        private MarshalledJSON dataKey;
    }
}
