package de.jproggy.snippetory.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.jproggy.snippetory.Snippetory;
import de.jproggy.snippetory.spi.Encoding;
import de.jproggy.snippetory.spi.Syntax;

public class SnippetBuilder {
	private final Locale locale;
	private String encoding;
	private Syntax syntax;
	private Syntax.Parser parser;

	public Syntax getSyntax() {
		if (syntax == null) return Syntax.REGISTRY.getDefault();
		return syntax;
	}

	public void setSyntax(Syntax syntax) {
		this.syntax = syntax;
	}

	public SnippetBuilder(Locale locale, Encoding encoding) {
		this(locale, encoding.getName());
	}

	public SnippetBuilder(Locale locale, String encoding) {
		super();
		this.locale = locale;
		this.encoding = encoding;
	}

	public Snippetory parse(CharSequence data) {
		parser = getSyntax().parse(data);
		Map<String, String> attribs = Collections.singletonMap("enc", encoding);
		Variable root = new Variable(null, null, attribs, "", getLocale());
		Snippetory template = parse(root);
		root.setTemplate(template);
		return template;
	}

	private Snippetory parse(Variable parent) {
		List<Object> parts = new ArrayList<Object>();
		Map<String, Snippetory> children = new HashMap<String, Snippetory>();
		while (parser.hasNext()) {
			Token t = parser.next();
			
			try {
				switch (t.getType()) {
				case BlockStart: {
					Variable var = new Variable(parent, t.getName(), t.getAttributes(), "", getLocale());
					parts.add(var);
					Syntax.Parser old = null;
					if (t.getAttributes().get("syntax") != null) {
						old = parser;
						String s = t.getAttributes().remove("syntax");
						parser = Syntax.REGISTRY.byName(s).takeOver(old);
					}
					Snippetory template = parse(var);
					children.put(var.getName(), template);
					var.setTemplate(template);
					if (old != null) {
						old.jumpTo(parser.getPosition());
						parser = old;
					}
					break;
				}
				case BlockEnd:
					if (parent.getName() == null || !parent.getName().equals(t.getName())) {
						throw new ParseError(t.getName() + " found but " + 
								(parent == null ? "file end" : parent.getName()) + " expected", t);
					}
					return new SnippetImpl(parent, parts, children);
				case Field:
					parts.add(new Variable(parent, t.getName(), t.getAttributes(), t.getContent(), getLocale()));
					break;
				case TemplateData:
					parts.add(t.getContent());
					break;
				case Syntax:
					setSyntax(Syntax.REGISTRY.byName(t.getName()));
					parser = getSyntax().takeOver(parser);
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
		return new SnippetImpl(parent, parts, children);
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public Locale getLocale() {
		return locale;
	}

	public String getEncoding() {
		return encoding;
	}
}
