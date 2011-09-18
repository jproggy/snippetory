package org.jproggy.snippetory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jproggy.snippetory.engine.TemplateBuilder;
import org.jproggy.snippetory.spi.Encoding;
import org.jproggy.snippetory.spi.Syntax;
import org.jproggy.snippetory.spi.SyntaxID;

public class TemplateContext {
	private Locale locale = Locale.getDefault();
	private Syntax syntax = Syntax.REGISTRY.getDefault();
	private CharSequence data;
	private Map<String, String> baseAttribs = new HashMap<String, String>();

	public TemplateContext(CharSequence data) {
		this.data = data;
		this.baseAttribs.put("date", "");
		this.baseAttribs.put("number", "");
	}

	public TemplateContext data(CharSequence data) {
		setData(data);
		return this;
	}
	public CharSequence getData() {
		return data;
	}
	public void setData(CharSequence data) {
		this.data = data;
	}

	public TemplateContext syntax(SyntaxID syntax) {
		return syntax(Syntax.REGISTRY.byName(syntax.getName()));
	}
	public TemplateContext syntax(Syntax syntax) {
		if (syntax == null) throw new NullPointerException();
		this.syntax = syntax;
		return this;
	}
	public Syntax getSyntax() {
		return syntax;
	}
	public void setSyntax(Syntax syntax) {
		this.syntax = syntax;
	}

	public TemplateContext encoding(String encoding) {
		return attrib("enc", encoding);
	}
	public TemplateContext encoding(Encoding encoding) {
		return encoding(encoding.getName());
	}

	public TemplateContext locale(Locale locale) {
		this.locale = locale;
		return this;
	}
	public Locale getLocale() {
		return locale;
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
	}


	public TemplateContext attrib(String name, String value) {
		this.baseAttribs.put(name, value);
		return this;
	}
	public Map<String, String> getBaseAttribs() {
		return baseAttribs;
	}
	public void setBaseAttribs(Map<String, String> baseAttribs) {
		this.baseAttribs = baseAttribs;
	}
	
	public Template parse() {
		return new TemplateBuilder().parse(this);
	}
}
