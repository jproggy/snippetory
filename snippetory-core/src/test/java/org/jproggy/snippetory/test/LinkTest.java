package org.jproggy.snippetory.test;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jproggy.snippetory.Syntaxes.FLUYT;
import static org.jproggy.snippetory.Syntaxes.XML_ALIKE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.UriResolver;

public class LinkTest {
    private static final String LIB = "$lib{$parts{\n" +
            "$part1{here goes some stuff}$\n" +
            "$part2{other stuff here}$\n" +
            "}$}$";
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
        assertEquals("here goes some stuff", x.toString());
        x.render();
        assertEquals("here goes some stuff", t.toString());
    }

    @Test
    void aliasNested() {
        Template t = FLUYT.parse(LIB + "$target{$x(alias='../lib/parts/part1')}$");
        Template target = t.get("target");
        Template x = target.get("x");
        assertEquals(target, x.getParent());
        assertEquals("x", x.metadata().getName());
        assertEquals("here goes some stuff", x.toString());
        x.render();
        assertEquals("here goes some stuff", target.toString());
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
        assertEquals("here goes some stuff", t.toString());
        t = FLUYT.context().uriResolver(repo).getTemplate("nested");
        assertEquals("here goes some stuff", t.get("target", "x").toString());
    }
}
