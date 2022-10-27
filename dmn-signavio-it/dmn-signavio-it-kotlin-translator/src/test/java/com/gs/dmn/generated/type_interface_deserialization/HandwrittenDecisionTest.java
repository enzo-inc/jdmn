package com.gs.dmn.generated.type_interface_deserialization;

import com.gs.dmn.generated.AbstractHandwrittenDecisionTest;
import org.junit.Assert;

import java.util.LinkedHashMap;
import java.util.Map;


public class HandwrittenDecisionTest extends AbstractHandwrittenDecisionTest {
    private final EsmaEquityOptionIndicator decision = new EsmaEquityOptionIndicator();

    @org.junit.Test
    public void testDeserializedDecisionInput() {
        com.gs.dmn.generated.type_interface_deserialization.type.MarginEquityOptionTradableProfiles marginEquityOptionTradableProfiles = new com.gs.dmn.generated.type_interface_deserialization.type.MarginEquityOptionTradableProfilesImpl(decision.asList(new com.gs.dmn.generated.type_interface_deserialization.type.MarginEquityOptionTradableProfileImpl(Boolean.TRUE, "abc")));

        String esmaEquityOptionIndicator = decision.apply(marginEquityOptionTradableProfiles, context.getAnnotations(), context.getEventListener(), context.getExternalFunctionExecutor(), context.getCache());

        checkValues("Yes", esmaEquityOptionIndicator);
    }

    @org.junit.Test
    public void testSerializedDecisionInput() throws Exception {
        com.gs.dmn.generated.type_interface_deserialization.type.MarginEquityOptionTradableProfiles marginEquityOptionTradableProfiles = new com.gs.dmn.generated.type_interface_deserialization.type.MarginEquityOptionTradableProfilesImpl(decision.asList(new com.gs.dmn.generated.type_interface_deserialization.type.MarginEquityOptionTradableProfileImpl(Boolean.TRUE, "abc")));
        String serializedInput = com.gs.dmn.serialization.JsonSerializer.OBJECT_MAPPER.writeValueAsString(marginEquityOptionTradableProfiles);

        String esmaEquityOptionIndicator = applyDecision(serializedInput);

        checkValues("Yes", esmaEquityOptionIndicator);
    }


    @Override
    protected void applyDecision() {
        com.gs.dmn.generated.type_interface_deserialization.type.MarginEquityOptionTradableProfiles marginEquityOptionTradableProfiles = new com.gs.dmn.generated.type_interface_deserialization.type.MarginEquityOptionTradableProfilesImpl(decision.asList(new com.gs.dmn.generated.type_interface_deserialization.type.MarginEquityOptionTradableProfileImpl(Boolean.TRUE, "abc")));

        decision.apply(marginEquityOptionTradableProfiles, context.getAnnotations(), context.getEventListener(), context.getExternalFunctionExecutor(), context.getCache());
    }

    private String applyDecision(String marginEquityOptionTradableProfiles) {
        Map<String, String> input = new LinkedHashMap<>();
        input.put("Margin Equity Option Tradable Profiles", marginEquityOptionTradableProfiles);
        return decision.apply(input, context);
    }

    private void checkValues(Object expected, Object actual) {
        Assert.assertEquals(expected == null ? null : expected.toString(), actual == null ? null : actual.toString());
    }
}
