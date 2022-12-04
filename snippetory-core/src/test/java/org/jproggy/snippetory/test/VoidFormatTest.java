package org.jproggy.snippetory.test;

import static org.jproggy.snippetory.Syntaxes.FLUYT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.Test;

import org.jproggy.snippetory.Template;

class VoidFormatTest {
    @Test
    void defaultTest() {
        Template val = FLUYT.parse("$(default='&uml;' enc='html' pad='8' pad.fill='\"')");
        assertEquals("&uml;\"\"\"", val.toString());
    }

    @Test
    void propertyTest() {
        Template val = FLUYT.parse("$(property='gibberish' default='not found')");
        assertEquals("not found", val.toString());
        val = FLUYT.parse("$(property='gibberish' null='not found')");
        assertEquals("not found", val.toString());
        val = FLUYT.parse("$(property='user.name' default='abc')");
        assertEquals(System.getProperty("user.name"), val.toString());
    }

    @Test
    void envTest() {
        assumeTrue(System.getenv("JAVA_HOME") != null);
        Template val = FLUYT.parse("$(property='JAVA_HOME')");
        assertEquals(System.getenv("JAVA_HOME"), val.toString());
    }
}
