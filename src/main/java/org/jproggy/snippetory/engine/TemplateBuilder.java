package org.jproggy.snippetory.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.spi.Syntax;

/**
 * Builds a template tree from the token stream provided by the tokenizer.
 *  
 * @author B. Ebertz
 */
public class TemplateBuilder {
	private Syntax tempSyntax;
	private Syntax.Tokenizer _parser;
	private final TemplateContext _ctx;

	private TemplateBuilder(TemplateContext ctx, CharSequence data) {
		_ctx = ctx;
		tempSyntax = ctx.getSyntax();
		_parser = getSyntax().parse(data);
	}
	
	public static Template parse(TemplateContext ctx, CharSequence data) {
		TemplateBuilder builder = new TemplateBuilder(ctx, data);
		Location root = new Location(null, null, ctx.getBaseAttribs(), "", ctx.getLocale());
		Template template = builder.parse(root);
		root.setTemplate(template);
		return template;
	}

	private Template parse(Location parent) {
		List<Object> parts = new ArrayList<Object>();
		Map<String, Template> children = new HashMap<String, Template>();
		while (_parser.hasNext()) {
			Token t = _parser.next();
			
			try {
				switch (t.getType()) {
				case BlockStart: {
					checkNameUnique(children, t);
					String end = handleBackward(parts, t);
					Location placeHolder = placeHolder(parent, t);
					parts.add(placeHolder);
					Template template = parse(placeHolder);
					children.put(placeHolder.getName(), template);
					placeHolder.setTemplate(template);
					if (end != null) parts.add(end);
					break;
				}
				case BlockEnd:
					if (parent.getName() == null || !parent.getName().equals(t.getName())) {
						throw new ParseError(t.getName() + " found but " + 
								(parent == null ? "file end" : parent.getName()) + " expected", t);
					}
					return new Region(parent, parts, children);
				case Field:
					String end = handleBackward(parts, t);
					parts.add(location(parent, t));
					if (end != null) parts.add(end);
					break;
				case TemplateData:
					parts.add(t.getContent());
					break;
				case Syntax:
					setSyntax(Syntax.REGISTRY.byName(t.getName()));
					_parser = getSyntax().takeOver(_parser);
					break;
				case Comment:
					// comments are simply ignored.
					break;
				}
			} catch (ParseError e) {
				throw e;
			} catch (RuntimeException e) {
				throw new ParseError(e, t);
			}
		}
		if (parent.getName() != null)
			throw new RuntimeException("No end element for " + parent.getName());
		return new Region(parent, parts, children);
	}

	private String handleBackward(List<Object> parts, Token t) {
		String end = null;
		if (t.getAttributes().containsKey("backward")) {
			String target = t.getAttributes().get("backward");
			String value = (String)parts.get(parts.size() - 1);
			Matcher m = Pattern.compile(target).matcher(value);
			if (m.find()) {
				int group = 0;
				if (m.groupCount() == 1) {
					group = 1;
				} else if (m.groupCount() > 1) {
					throw new ParseError("only one match group allowed: " + target, t);
				}
				parts.set(parts.size() - 1, value.substring(0, m.start(group)));
				end = value.substring(m.end(group));
				if (m.find()) throw new ParseError("backward target ambigous " + target, t);
			} else {
				throw new ParseError("target not found: " + target, t);
			}
			t.getAttributes().remove("backward");
		}
		return end;
	}

	private Location location(Location parent, Token t) {
		return new Location(parent, t.getName(), 
				t.getAttributes(), t.getContent(), getLocale());
	}

	private void checkNameUnique(Map<String, Template> children, Token t) {
		if (children.containsKey(t.getName())) {
			throw new ParseError("duplicate child template " +
					t.getName(), t);
		}
	}

	private Location placeHolder(Location parent, Token t) {
		Location var = new Location(parent, t.getName(), 
				t.getAttributes(), "", getLocale());
		return var;
	}

	private Locale getLocale() {
		return _ctx.getLocale();
	}


	private void setSyntax(Syntax s) {
		if (s == null) throw new NullPointerException();
		tempSyntax = s;
	}

	private Syntax getSyntax() {
		if (tempSyntax == null) return Syntax.REGISTRY.getDefault();
		return tempSyntax;
	}
}
