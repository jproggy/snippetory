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
import de.jproggy.snippetory.spi.SyntaxID;

public class SnippetBuilder {
	private Locale _locale;
	private String _encoding;
	private Syntax _syntax;
	private Syntax.Parser _parser;
	private CharSequence _data;

	public SnippetBuilder(CharSequence data) {
		this._data =  data;
	}

	public SnippetBuilder(CharSequence data, Locale locale) {
		this._data = data;
		this._locale = locale;
	}

	public SnippetBuilder(Locale locale, Encoding encoding) {
		this(locale, encoding.getName());
	}

	public SnippetBuilder(Locale locale, String encoding) {
		super();
		this._locale = locale;
		this._encoding = encoding;
	}

	public Snippetory parse() {
		return parse(_data);
	}

	public Snippetory parse(CharSequence data) {
		_parser = getSyntax().parse(data);
		Map<String, String> attribs = Collections.singletonMap("enc", _encoding);
		Variable root = new Variable(null, null, attribs, "", _locale);
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
		this._encoding = encoding;
		return this;
	}

	public SnippetBuilder encoding(Encoding encoding) {
		this._encoding = encoding.getName();
		return this;
	}

	public SnippetBuilder locale(Locale locale) {
		this._locale = locale;
		return this;
	}
}
