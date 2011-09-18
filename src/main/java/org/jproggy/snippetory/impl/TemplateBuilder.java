package org.jproggy.snippetory.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.spi.Syntax;


public class TemplateBuilder {
	private Syntax tempSyntax;
	private Syntax.Tokenizer _parser;
	private TemplateContext _ctx;

	public Template parse(TemplateContext ctx) {
		_ctx = ctx;
		tempSyntax = ctx.getSyntax();
		_parser = getSyntax().parse(ctx.getData());
		Location root = new Location(null, null, ctx.getBaseAttribs(), "", ctx.getLocale());
		Template template = parse(root);
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
					if (children.containsKey(t.getName())) {
						throw new ParseError("duplicate child template " +
								t.getName(), t);
					}
					Location var = new Location(parent, t.getName(), 
							t.getAttributes(), "", getLocale());
					parts.add(var);
					Syntax.Tokenizer old = null;
					if (t.getAttributes().get("syntax") != null) {
						old = _parser;
						String s = t.getAttributes().remove("syntax");
						_parser = Syntax.REGISTRY.byName(s).takeOver(old);
					}
					Template template = parse(var);
					children.put(var.getName(), template);
					var.setTemplate(template);
					if (old != null) {
						old.jumpTo(_parser.getPosition());
						_parser = old;
					}
					break;
				}
				case BlockEnd:
					if (parent.getName() == null || !parent.getName().equals(t.getName())) {
						throw new ParseError(t.getName() + " found but " + 
								(parent == null ? "file end" : parent.getName()) + " expected", t);
					}
					return new Region(parent, parts, children);
				case Field:
					parts.add(new Location(parent, t.getName(), 
							t.getAttributes(), t.getContent(), getLocale()));
					break;
				case TemplateData:
					parts.add(t.getContent());
					break;
				case Syntax:
					setSyntax(Syntax.REGISTRY.byName(t.getName()));
					_parser = getSyntax().takeOver(_parser);
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
