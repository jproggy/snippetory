package org.jproggy.snippetory.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.jproggy.snippetory.Syntaxes.FLUYT_CC;
import static org.jproggy.snippetory.Syntaxes.XML_ALIKE;
import static org.jproggy.snippetory.test.BasicTest.containsStrings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.util.ParseError;

public class DislocationTest {
    @Nested
    class Backward {
        @Test
        void directHit() {
            Template t = FLUYT_CC.parse("12345 // $test(backward='3')");
            assertEquals("12345 ", t.toString());
            t.set("test", "X");
            assertEquals("12X45 ", t.toString());
            t.append("test", "y");
            assertEquals("12Xy45 ", t.toString());
            t = FLUYT_CC.parse("12345 // $test(backward='3' default='4')");
            assertEquals("12445 ", t.toString());
            t.set("test", "3");
            assertEquals("12345 ", t.toString());
        }
        @Test
        void groupedHit() {
            Template t = FLUYT_CC.parse("12345 // $test(backward='2(3)4')");
            assertEquals("12345 ", t.toString());
            t.set("test", "X");
            assertEquals("12X45 ", t.toString());
            t.append("test", "y");
            assertEquals("12Xy45 ", t.toString());
            t = FLUYT_CC.parse("$x$12345 // $test(backward='2(3)4' default='4')");
            assertEquals("$x$12445 ", t.toString());
            t.set("test", "3");
            assertEquals("$x$12345 ", t.toString());
        }

        @Test
        void twoHits() {
            Template t2 = XML_ALIKE.parse(
                    "Hello world{v:x backward='Hello' default='Liahallo'}{v:x backward='world' default='Welt'}"
            );
            assertEquals("Liahallo Welt", t2.toString());
        }

        @Test
        void miss() {
            ParseError e = assertThrows(ParseError.class, () -> FLUYT_CC.parse("12345 // $test(backward='6')"));
            assertThat(e.getMessage(), containsStrings("not found", "<6>"));
            e = assertThrows(ParseError.class, () -> FLUYT_CC.parse("12345$x // $test(backward='3')"));
            assertThat(e.getMessage(), containsStrings("not found", "<3>"));
            e = assertThrows(ParseError.class, () -> FLUYT_CC.parse("12345 // $test(backward='1(3)')"));
            assertThat(e.getMessage(), containsStrings("not found", "<1(3)>"));
        }
    }
}
