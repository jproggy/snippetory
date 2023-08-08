package org.jproggy.snippetory.engine;

import java.util.HashMap;
import java.util.Map;

import org.jproggy.snippetory.SnippetoryException;
import org.jproggy.snippetory.Syntaxes;
import org.jproggy.snippetory.engine.spi.FluytCCSyntax;
import org.jproggy.snippetory.engine.spi.FluytSyntax;
import org.jproggy.snippetory.engine.spi.FluytXSyntax;
import org.jproggy.snippetory.engine.spi.HiddenBlocksSyntax;
import org.jproggy.snippetory.engine.spi.UnderUnderScorySyntax;
import org.jproggy.snippetory.engine.spi.XMLAlikeSyntax;
import org.jproggy.snippetory.spi.Syntax;
import org.jproggy.snippetory.spi.SyntaxID;

public final class SyntaxRegistry {
    private final Map<String, Syntax> reg = new HashMap<>();

    public SyntaxRegistry() {
        register(Syntaxes.HIDDEN_BLOCKS, new HiddenBlocksSyntax());
        register(Syntaxes.XML_ALIKE, new XMLAlikeSyntax());
        register(Syntaxes.FLUYT, new FluytSyntax());
        register(Syntaxes.FLUYT_CC, new FluytCCSyntax());
        register(Syntaxes.FLUYT_X, new FluytXSyntax());
        register(Syntaxes.__SCORY, new UnderUnderScorySyntax());
    }

    public void register(SyntaxID name, Syntax syntax) {
        reg.put(name.getName(), syntax);
    }

    public Syntax byName(String name) {
        if (!reg.containsKey(name)) {
            throw new SnippetoryException("Unknown syntax: " + name);
        }
        return reg.get(name);
    }

    public Syntax getDefault() {
        return new XMLAlikeSyntax();
    }

    /**
     * To be able to select a syntax via the <a href="/snippetory/Syntax.html#Syntax">syntax selector</a>
     * it has to be registered.
     */
    public static final SyntaxRegistry INSTANCE = new SyntaxRegistry();
}
