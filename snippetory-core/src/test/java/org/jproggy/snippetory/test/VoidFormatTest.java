package org.jproggy.snippetory.test;

import static org.jproggy.snippetory.Syntaxes.FLUYT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jproggy.snippetory.Template;
import org.junit.jupiter.api.Test;

class VoidFormatTest {
    @Test
    void defaultTest() {
        Template val = FLUYT.parse("$(default='&uml;' enc='html' pad='8' pad.fill='\"')");
        assertEquals("&uml;\"\"\"", val.toString());
    }

    @Test
    void propertyTest() {
        Template val = FLUYT.parse("$(property='gibberish' null='not found')");
        assertEquals("not found", val.toString());
        val = FLUYT.parse("$(property='user.name' default='abc')");
        assertEquals(System.getProperty("user.name"), val.toString());
        val = FLUYT.parse("$(property='path')");
        assertEquals(System.getenv("path"), val.toString());
    }
}
