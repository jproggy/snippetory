package org.jproggy.snippetory.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.jproggy.snippetory.Syntaxes.FLUYT_CC;
import static org.jproggy.snippetory.Syntaxes.XML_ALIKE;
import static org.jproggy.snippetory.test.BasicTest.containsStrings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
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

            e = assertThrows(ParseError.class, () -> XML_ALIKE.parse(
                    "Hello world{v:x backward='(Hello)(v)' default='Liahallo'}{v:x backward='world' default='Welt'}"
            ));
            assertThat(e.getMessage(), containsStrings(
                    "Target not found",
                    "<(Hello)(v)>",
                    "{v:x backward='(Hello)(v)' default='Liahallo'}"
            ));

            e = assertThrows(ParseError.class, () -> XML_ALIKE.parse(
                    "Hello world{v:x backward='world' default='Welt'}{v:x backward='Hello' default='Liahallo'}"
            ));
            assertThat(e.getMessage(), containsStrings(
                    "Target not found",
                    "<Hello>",
                    "{v:x backward='Hello' default='Liahallo'}"
            ));
        }
    }
    @Nested
    class Forward {
        @Test
        void directHit() {
            Template t = FLUYT_CC.parse("// $test(forward='3')\n12345");
            assertEquals("\n12345", t.toString());
            t.set("test", "X");
            assertEquals("\n12X45", t.toString());
            t.append("test", "y");
            assertEquals("\n12Xy45", t.toString());
            t = FLUYT_CC.parse("// $test(forward='3' default='4')\n12345");
            assertEquals("\n12445", t.toString());
            t.set("test", "3");
            assertEquals("\n12345", t.toString());
        }
        @Test
        void groupedHit() {
            Template t = FLUYT_CC.parse("// $test(forward='2(3)4')\n12345");
            assertEquals("\n12345", t.toString());
            t.set("test", "X");
            assertEquals("\n12X45", t.toString());
            t.append("test", "y");
            assertEquals("\n12Xy45", t.toString());
            t = FLUYT_CC.parse("// $test(forward='2(3)4' default='4')\n$x$12345");
            assertEquals("\n$x$12445", t.toString());
            t.set("test", "3");
            assertEquals("\n$x$12345", t.toString());
        }
        @Test
        @Disabled
        void region() {
            Template t = XML_ALIKE.parse("<t:test_bw forward=\"href='(.*)'\" enc=\"url\">"
                    + "{v:path delimiter='/'}/{v:file}.html" + "</t:test_bw>" + "<a href='test.html'>Here</a> ");
            assertEquals("<a href='test.html'>Here</a> ", t.toString());
            t.get("test_bw").append("path", "x s").append("path", "xy+z").set("file", "tesst").render();
            assertEquals("<a href='x+s/xy%2Bz/tesst.html'>Here</a> ", t.toString());
        }
        @Test
        void miss() {
            ParseError e = assertThrows(ParseError.class, () -> FLUYT_CC.parse(" // $test(forward='6')\n12345"));
            assertThat(e.getMessage(), containsStrings("not found", "<6>"));

            e = assertThrows(ParseError.class, () -> FLUYT_CC.parse(" // $test(forward='1(3)') \n 12345"));
            assertThat(e.getMessage(), containsStrings("not found", "<1(3)>"));

            e = assertThrows(ParseError.class, () -> XML_ALIKE.parse(
                    "Hello world{v:x backward='(Hello)(v)' default='Liahallo'}{v:x backward='world' default='Welt'}"
            ));
            assertThat(e.getMessage(), containsStrings(
                    "Target not found",
                    "<(Hello)(v)>",
                    "{v:x backward='(Hello)(v)' default='Liahallo'}"
            ));

            e = assertThrows(ParseError.class, () -> XML_ALIKE.parse(
                    "{v:x forward='world' default='Welt'}{v:x forward='Hello' default='Liahallo'}Hello world"
            ));
            assertThat(e.getMessage(), containsStrings(
                    "Target not found",
                    "<Hello>",
                    "{v:x forward='Hello' default='Liahallo'}"
            ));
        }
        @Test
        void illegal() {
            ParseError e = assertThrows(ParseError.class, () -> XML_ALIKE.parse(
                    "Hello world{v:x backward='(Hel)(lo)' default='Liahallo'}{v:x backward='world' default='Welt'}"
            ));
            assertThat(e.getMessage(), containsStrings(
                    "Only one match group allowed",
                    "<(Hel)(lo)>",
                    "{v:x backward='(Hel)(lo)' default='Liahallo'}"
            ));
        }
        @Test
        void ambiguous() {
            ParseError e = assertThrows(ParseError.class, () -> XML_ALIKE.parse(
                    "Hello Hello world{v:x backward='(Hello)' default='Liahallo'}{v:x backward='world' default='Welt'}"
            ));
            assertThat(e.getMessage(), containsStrings(
                    "Backward target ambiguous",
                    "<(Hello)>",
                    "{v:x backward='(Hello)' default='Liahallo'}"
            ));
        }
    }
}
