/*******************************************************************************
 * Copyright (c) 2011-2012 JProggy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * EXCEPT AS EXPRESSLY SET FORTH IN THIS AGREEMENT, THE PROGRAM IS PROVIDED ON AN 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR 
 * IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR CONDITIONS OF TITLE, 
 * NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE
 *******************************************************************************/

package org.jproggy.snippetory.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	private static final String BACKWARD = Attributes.BACKWARD;
	private Syntax tempSyntax;
	private Syntax.Tokenizer parser;
	private final TemplateContext ctx;

	protected TemplateBuilder(TemplateContext ctx, CharSequence data) {
		this.ctx = ctx;
		tempSyntax = ctx.getSyntax();
		parser = getSyntax().parse(data, ctx);
	}
	
	public static Template parse(TemplateContext ctx, CharSequence data) {
		TemplateBuilder builder = new TemplateBuilder(ctx.clone(), data);
		Location root = new Location(null, null, ctx.getBaseAttribs(), "", ctx);
		return builder.parse(root);
	}

	private Region parse(Location parent) {
		List<Object> parts = new ArrayList<Object>();
		Map<String, Region> children = new HashMap<String, Region>();
		Token t = null;
		while (parser.hasNext()) {
			t = parser.next();
			
			try {
				switch (t.getType()) {
				case BlockStart: {
					checkNameUnique(children, t);
					String end = handleBackward(parts, t);
					Location placeHolder = placeHolder(parent, t);
					parts.add(placeHolder);
					Region template = parse(placeHolder);
					children.put(placeHolder.getName(), template);
					if (end != null) parts.add(end);
					break;
				}
				case BlockEnd:
					verifyName(parent, t);
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
					parser = getSyntax().takeOver(parser);
					break;
				case Comment:
					// comments are simply ignored.
					break;
				default:
					throw new SnippetoryException("Unknown token type: " + t.getType());
				}
			} catch (ParseError e) {
				throw e;
			} catch (RuntimeException e) {
				throw new ParseError(e, t);
			}
		}
		verifyRootNode(parent, t);
		return new Region(parent, parts, children);
	}

	private void verifyRootNode(Location parent, Token t) {
		if (parent.getName() != null)
			throw new ParseError("No end element for " + parent.getName(), t);
	}

	private void verifyName(Location parent, Token t) {
		if (parent.getName() == null || !parent.getName().equals(t.getName())) {
			throw new ParseError(t.getName() + " found but " + 
					(parent.getName() == null ? "file end" : parent.getName()) + " expected", t);
		}
	}

	private String handleBackward(List<Object> parts, Token t) {
		String end = null;
		if (t.getAttributes().containsKey(BACKWARD)) {
			String target = t.getAttributes().get(BACKWARD);
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
			t.getAttributes().remove(BACKWARD);
		}
		return end;
	}

	private Location location(Location parent, Token t) {
		return new Location(parent, t.getName(), 
				t.getAttributes(), t.getContent(), ctx);
	}

	private void checkNameUnique(Map<String, Region> children, Token t) {
		if (children.containsKey(t.getName())) {
			throw new ParseError("duplicate child template " +
					t.getName(), t);
		}
	}

	private Location placeHolder(Location parent, Token t) {
		Location var = new Location(parent, t.getName(), 
				t.getAttributes(), "", ctx);
		return var;
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
