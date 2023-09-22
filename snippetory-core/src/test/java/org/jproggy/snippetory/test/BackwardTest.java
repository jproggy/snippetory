package org.jproggy.snippetory.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.jproggy.snippetory.Syntaxes.FLUYT;
import static org.jproggy.snippetory.Syntaxes.FLUYT_CC;
import static org.jproggy.snippetory.Syntaxes.XML_ALIKE;
import static org.jproggy.snippetory.test.BasicTest.containsStrings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.util.ParseError;

public class BackwardTest {

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
    void condRegionHop() {
        Template t = FLUYT.parse("before$(default='-'){->$x<-}$after$test(backward='before')");
        assertEquals("before-after", t.toString());
        t.set("test", "blub");
        assertEquals("blub-after", t.toString());
        t.set("x", "x");
        assertEquals("blub->x<-after", t.toString());
        ParseError e = assertThrows(ParseError.class, () ->
                FLUYT.parse("before-after$(backward='-'){->$test<-}$"));
        assertThat(e.getMessage(), containsStrings( "not supported"));
    }

    @Test
    void condRegion() {
        Template t = FLUYT.parse("before$(default='-'){-><-$x(backward='>()<')}$after$test(backward='before')");
        assertEquals("before-after", t.toString());
        t.set("test", "blub");
        assertEquals("blub-after", t.toString());
        t.set("x", "x");
        assertEquals("blub->x<-after", t.toString());
        ParseError e = assertThrows(ParseError.class, () ->
                FLUYT.parse("before-after$(backward='-'){->$test<-}$"));
        assertThat(e.getMessage(), containsStrings( "not supported"));
    }

    @Test
    void groupedHit() {
        Template t = FLUYT_CC.parse("12345 // $test(backward='2(3)4')");
        assertEquals("12345 ", t.toString());
        t.set("test", "X");
        assertEquals("12X45 ", t.toString());
        t.append("test", "y");
        assertEquals("12Xy45 ", t.toString());
        t = FLUYT_CC.parse("$x$12345 // $test(backward='3()4')");
        assertEquals("$x$12345 ", t.toString());
        t.set("test", "-");
        assertEquals("$x$123-45 ", t.toString());
        t = FLUYT_CC.parse("$x$12345 // $test(backward='2(3)4' default='4')");
        assertEquals("$x$12445 ", t.toString());
        t.set("test", "3");
        assertEquals("$x$12345 ", t.toString());
    }

    @Test
    void outOfOrder() {
        Template t = FLUYT_CC.parse("12345$x // $test(backward='3')");
        assertEquals("12345$x ", t.toString());
        t.set("x", "y");
        assertEquals("12345y ", t.toString());
        t.set("test", "77");
        assertEquals("127745y ", t.toString());
        t = XML_ALIKE.parse(
                "Hello world{v:x backward='world' default='Welt'}{v:x backward='Hello' default='Liahallo'}");
        assertEquals("Liahallo Welt", t.toString());
    }

    @Test
    void twoHits() {
        Template t = XML_ALIKE.parse(
                "Hello world{v:x backward='Hello' default='Liahallo'}{v:x backward='world' default='Welt'}"
        );
        assertEquals("Liahallo Welt", t.toString());
    }

    @Test
    void miss() {
        ParseError e = assertThrows(ParseError.class, () -> FLUYT_CC.parse("12345 // $test(backward='6')"));
        assertThat(e.getMessage(), containsStrings("not found", "<6>"));
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
                "Hello Hello world{v:x backward='Hello'}"
        ));
        assertThat(e.getMessage(), containsStrings(
                "Backward target ambiguous",
                "<Hello>",
                "{v:x backward='Hello'}"
        ));

        e = assertThrows(ParseError.class, () -> XML_ALIKE.parse(
                "Hello{v:y} Hello world{v:x backward='Hello'}"
        ));
        assertThat(e.getMessage(), containsStrings(
                "Backward target ambiguous",
                "<Hello>",
                "{v:x backward='Hello'}"
        ));
    }
}
