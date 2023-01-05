package org.jproggy.snippetory.engine.spi;

import java.util.Map;
import java.util.regex.Pattern;

import org.jproggy.snippetory.util.Token.TokenType;

public class UnderUnderScorySyntax extends FluytCCSyntax {
    protected static final String NAME_START_CHAR = "[\\p{javaJavaIdentifierStart}&&[^_$]]";
    protected static final String NAME_CHAR = "[\\p{javaJavaIdentifierPart}.\\-&&[^_]]";
    protected static final String NAME = NAME_START_CHAR + NAME_CHAR + "*(?:_" + NAME_CHAR + "+)*";
    protected static final Pattern NAMED_LOC = Pattern.compile("__(" + NAME + ")(?:__)?", Pattern.MULTILINE);

    @Override
    protected Map<Pattern, TokenType> createPatterns() {
        Map<Pattern, TokenType> patterns = super.createPatterns();

        patterns.put(NAMED_LOC, TokenType.Field);

        return patterns;
    }

    @Override
    protected String coatStart(TokenType type, SyntaxVariant variant) {
        if (type == TokenType.BlockEnd) return super.coatStart(type, variant);
        return "(?:/\\*|//)[ \t]*";
    }
}
