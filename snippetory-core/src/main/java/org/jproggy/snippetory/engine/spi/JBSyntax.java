package org.jproggy.snippetory.engine.spi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.util.RegExSyntax;
import org.jproggy.snippetory.util.Token.TokenType;

public class JBSyntax extends RegExSyntax {
    protected static final String START_TOKEN = ":((?:" + NAME + ")?" + ATTRIBUTES + ")\\s*";
    protected static final String END_TOKEN = ":(" + NAME + ")?";

    @Override
    public RegexParser parse(CharSequence data, TemplateContext ctx) {
        Map<Pattern, TokenType> patterns = new LinkedHashMap<>();

        patterns.put(SYNTAX_SELECTOR, TokenType.Syntax);

        addRegions(patterns);
        addLocations(patterns);
        addComments(patterns);

        return new RegexParser(data, ctx, patterns);
    }

    private void addLocations(Map<Pattern, TokenType> patterns) {
        Pattern field = Pattern.compile("\\{" + loc() +":(" + NAME + ATTRIBUTES + ")[ \\t]*}", Pattern.MULTILINE);
        patterns.put(field, TokenType.Field);

        Pattern nameless = Pattern.compile("\\{" + loc() + ":\\s*(" + ATTRIBUTE + ATTRIBUTES + ")\\s*}", Pattern.MULTILINE);
        patterns.put(nameless, TokenType.Field);
    }

    protected void addComments(Map<Pattern, TokenType> patterns) {
        Pattern comment = Pattern.compile(LINE_START + "///.*" + LINE_END, Pattern.MULTILINE);
        patterns.put(comment, TokenType.Comment);
    }

    protected String loc() {
        return "v";
    }

    protected String region() {
        return "t";
    }

    protected String regionPrefix() {
        return "<";
    }

    protected String regionSuffix() {
        return ">";
    }

    protected String endSign() {
        return "/";
    }

    public void addRegions(Map<Pattern, TokenType> patterns) {
        Pattern start = Pattern.compile(LINE_START + regionPrefix() + region() + START_TOKEN + regionSuffix()
                + LINE_END, Pattern.MULTILINE);
        patterns.put(start, TokenType.BlockStart);

        start = Pattern.compile(regionPrefix() + region() + START_TOKEN + regionSuffix(), Pattern.MULTILINE);
        patterns.put(start, TokenType.BlockStart);

        Pattern end = Pattern.compile(LINE_START + regionPrefix() + endSign() + region() + END_TOKEN + regionSuffix()
                + LINE_END, Pattern.MULTILINE);
        patterns.put(end, TokenType.BlockEnd);

        end = Pattern.compile(regionPrefix() + endSign() + region() + END_TOKEN + regionSuffix(), Pattern.MULTILINE);
        patterns.put(end, TokenType.BlockEnd);
    }

}
