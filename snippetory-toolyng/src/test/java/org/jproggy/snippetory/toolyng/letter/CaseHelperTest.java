package org.jproggy.snippetory.toolyng.letter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CaseHelperTest {

    @Test void test() {
        assertEquals("IntProp", CaseHelper.convert(CaseFormat.UPPER_CAMEL, "intProp"));
        assertEquals("int-prop", CaseHelper.convert(CaseFormat.LOWER_HYPHEN, "intProp"));
        assertEquals("intProp", CaseHelper.convert(CaseFormat.LOWER_CAMEL, "int-prop"));
    }

}