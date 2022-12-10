package org.jproggy.snippetory.test;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jproggy.snippetory.Syntaxes.FLUYT;
import static org.jproggy.snippetory.Syntaxes.XML_ALIKE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.UriResolver;
import org.jproggy.snippetory.spi.LinkedWrapper;
import org.jproggy.snippetory.spi.Metadata;

public class LinkTest {
    private static final String LIB = "$lib{\n" +
            "$parts{\n" +
            "$part1{here goes some $stuff}$\n" +
            "$part2{other $stuff here}$\n" +
            "}$\n}$";

    @BeforeAll
    static void prepare() {
        Metadata.registerAnnotation("test");
    }

    @Test
    void alias() {
        Template t = XML_ALIKE.parse("{v:test alias='best'}<t:best>The very best</t:>");
        assertThat(t.regionNames(), hasItems("test", "best"));
        assertEquals("", t.toString());
        Template x = t.get("test");
        assertEquals(t, x.getParent());
        assertEquals("test", x.metadata().getName());
        assertEquals("The very best",x.toString());
        x.render();
        assertEquals("The very best", t.toString());
        assertEquals("", t.clear().toString());
        t.get("best").render();
        assertEquals("The very best", t.toString());

        t = FLUYT.parse(LIB + "$x(alias='lib/parts/part1')");
        x = t.get("x");
        assertEquals(t, x.getParent());
        assertEquals("x", x.metadata().getName());
        assertEquals("here goes some $stuff", x.toString());
        x.render();
        assertEquals("here goes some $stuff", t.toString());
    }

    @Test
    void aliasNested() {
        Template t = FLUYT.parse(LIB + "$target{$x(alias='../lib/parts/part1')}$");
        Template target = t.get("target");
        Template x = target.get("x");
        assertEquals(target, x.getParent());
        assertEquals("x", x.metadata().getName());
        assertEquals("here goes some $stuff", x.toString());
        x.render();
        assertEquals("here goes some $stuff", target.toString());
    }

    @Test
    void aliasFiles() {
        UriResolver repo =  (uri, ctx) -> {
            if (uri.equalsIgnoreCase("lib")) return LIB;
            if (uri.equalsIgnoreCase("nested")) return "$target{$x(alias='lib/parts/part1' alias.file='lib')}$";
            return "$x(alias='lib/parts/part1' alias.file='lib')";
        };
        Template t = FLUYT.context().uriResolver(repo).getTemplate("x");
        t.get("x").render();
        assertEquals("here goes some $stuff", t.toString());
        t = FLUYT.context().uriResolver(repo).getTemplate("nested");
        assertEquals("here goes some $stuff", t.get("target", "x").toString());
    }

    @Test
    void rendering() throws Exception {
        Template t = FLUYT.parse("$test(alias='best')$$best(test='x'){The very best$y{xx}$}$");
        Template x = t.get("test");
        assertEquals("The very best", x.toCharSequence().toString());
        x.get("y").render();
        StringWriter out = new StringWriter();
        x.render(out);
        assertEquals("The very bestxx", out.toString());
        assertThat(x.get("y"), instanceOf(LinkedWrapper.class));
        t = FLUYT.parse(LIB + "$test(alias='lib')").get("test");
        t.get("parts").set("part1", "notapartanymore").render();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        t.render(new PrintStream(output));
        assertEquals("notapartanymore\n\n", output.toString());
        assertThat(t.get("parts", "part2"), instanceOf(LinkedWrapper.class));
        assertEquals(Set.of("parts"), t.regionNames());
        assertEquals(Set.of("parts"), t.names());
    }

    @Test
    void annotations() {
        Template t = XML_ALIKE.parse("{v:test alias='best'}<t:best test='x'>The very best</t:>");
        assertEquals("x", t.get("test").metadata().annotation("test").get());
        t = XML_ALIKE.parse("{v:test alias='best' test='x'}<t:best>The very best</t:>");
        assertEquals("x", t.get("test").metadata().annotation("test").get());
        t = XML_ALIKE.parse("{v:test alias='best' test='x'}<t:best test='y'>The very best</t:>");
        assertEquals("x", t.get("test").metadata().annotation("test").get());
    }
 }
