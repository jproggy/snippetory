package org.jproggy.snippetory.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jproggy.snippetory.Snippetory;
import org.jproggy.snippetory.spi.Encoding;
import org.jproggy.snippetory.spi.Syntax;
import org.jproggy.snippetory.spi.SyntaxID;


public class SnippetBuilder {
	private Locale _locale = Locale.getDefault();
	private Syntax _syntax;
	private Syntax.Parser _parser;
	private CharSequence _data;
	private Map<String, String> _baseAttribs = new HashMap<String, String>();

	public SnippetBuilder(CharSequence data) {
		this._data = data;
		this._baseAttribs.put("date", "");
		this._baseAttribs.put("number", "");
	}

	public Snippetory parse() {
		_parser = getSyntax().parse(_data);
		Variable root = new Variable(null, null, _baseAttribs, "", _locale);
		Snippetory template = parse(root);
		root.setTemplate(template);
		return template;
	}

	private Snippetory parse(Variable parent) {
		List<Object> parts = new ArrayList<Object>();
		Map<String, Snippetory> children = new HashMap<String, Snippetory>();
		while (_parser.hasNext()) {
			Token t = _parser.next();
			
			try {
				switch (t.getType()) {
				case BlockStart: {
					if (children.containsKey(t.getName())) {
						throw new ParseError("duplicate child template " + t.getName(), t);
					}
					Variable var = new Variable(parent, t.getName(), t.getAttributes(), "", _locale);
					parts.add(var);
					Syntax.Parser old = null;
					if (t.getAttributes().get("syntax") != null) {
						old = _parser;
						String s = t.getAttributes().remove("syntax");
						_parser = Syntax.REGISTRY.byName(s).takeOver(old);
					}
					Snippetory template = parse(var);
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
					return new SnippetImpl(parent, parts, children);
				case Field:
					parts.add(new Variable(parent, t.getName(), t.getAttributes(), t.getContent(), _locale));
					break;
				case TemplateData:
					parts.add(t.getContent());
					break;
				case Syntax:
					syntax(Syntax.REGISTRY.byName(t.getName()));
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
		return new SnippetImpl(parent, parts, children);
	}


	private Syntax getSyntax() {
		if (_syntax == null) return Syntax.REGISTRY.getDefault();
		return _syntax;
	}

	public SnippetBuilder syntax(SyntaxID syntax) {
		return syntax(Syntax.REGISTRY.byName(syntax.getName()));
	}

	public SnippetBuilder syntax(Syntax syntax) {
		if (syntax == null) throw new NullPointerException();
		this._syntax = syntax;
		return this;
	}

	public SnippetBuilder encoding(String encoding) {
		return attrib("enc", encoding);
	}

	public SnippetBuilder encoding(Encoding encoding) {
		return encoding(encoding.getName());
	}

	public SnippetBuilder locale(Locale locale) {
		this._locale = locale;
		return this;
	}

	public SnippetBuilder attrib(String name, String value) {
		this._baseAttribs.put(name, value);
		return this;
	}
}
