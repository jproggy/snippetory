package org.jproggy.snippetory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jproggy.snippetory.impl.ParseError;
import org.jproggy.snippetory.impl.Region;
import org.jproggy.snippetory.impl.Token;
import org.jproggy.snippetory.impl.Location;
import org.jproggy.snippetory.spi.Encoding;
import org.jproggy.snippetory.spi.Syntax;
import org.jproggy.snippetory.spi.SyntaxID;


public class Parser {
	private Locale _locale = Locale.getDefault();
	private Syntax _syntax;
	private Syntax.Tokenizer _parser;
	private CharSequence _data;
	private Map<String, String> _baseAttribs = new HashMap<String, String>();

	public Parser(CharSequence data) {
		this._data = data;
		this._baseAttribs.put("date", "");
		this._baseAttribs.put("number", "");
	}

	public Template parse() {
		_parser = getSyntax().parse(_data);
		Location root = new Location(null, null, _baseAttribs, "", _locale);
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
						throw new ParseError("duplicate child template " + t.getName(), t);
					}
					Location var = new Location(parent, t.getName(), t.getAttributes(), "", _locale);
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
					parts.add(new Location(parent, t.getName(), t.getAttributes(), t.getContent(), _locale));
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
		return new Region(parent, parts, children);
	}


	private Syntax getSyntax() {
		if (_syntax == null) return Syntax.REGISTRY.getDefault();
		return _syntax;
	}

	public Parser syntax(SyntaxID syntax) {
		return syntax(Syntax.REGISTRY.byName(syntax.getName()));
	}

	public Parser syntax(Syntax syntax) {
		if (syntax == null) throw new NullPointerException();
		this._syntax = syntax;
		return this;
	}

	public Parser encoding(String encoding) {
		return attrib("enc", encoding);
	}

	public Parser encoding(Encoding encoding) {
		return encoding(encoding.getName());
	}

	public Parser locale(Locale locale) {
		this._locale = locale;
		return this;
	}

	public Parser attrib(String name, String value) {
		this._baseAttribs.put(name, value);
		return this;
	}
}
