package org.jproggy.snippetory.engine.spi;

import java.util.Map;
import java.util.regex.Pattern;

import org.jproggy.snippetory.engine.Token.TokenType;

public class FluytXSyntax extends FluytSyntax {

	@Override
	protected Map<Pattern, TokenType> createPatterns() {
		Map<Pattern, TokenType> patterns = super.createPatterns();
		XMLAlikeSyntax.addRegionPatterns(patterns);
		return patterns;
	}

}
