/// Copyright JProggy
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///     http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.

package org.jproggy.snippetory;

import java.util.Locale;

import org.jproggy.snippetory.spi.SyntaxID;

/**
 * Provides direct access to the predefined syntaxes.
 * For more information see
 * <a href="http://www.jproggy.org/snippetory/Syntax.html">
 * syntaxes in official documentation
 * </a>.
 * @author B.Ebertz
 */
public enum Syntaxes implements SyntaxID {
	/**
	 * This is the default syntax. It is used if no syntax is defined. It
	 * looks like this:
	 * <p>
	 * <code>
	 * &lt;t:name default='def'>{v:other_name}&lt;/t:name>
	 * </code>
	 * </p>
	 * It integrates fine in xml-based formats and is very visible in many others.
	 * It provides low risk of incompatibility to a certain output format.
	 */
	XML_ALIKE,
	/**
	 * @deprecated use FLUYT_CC instead
	 */
	@Deprecated
	C_COMMENTS,

	/**
	 * The fluyt syntax is designed with an minimal overhead in mind. It very homogeneous,
	 * meaning there is not a completely distinct syntax for regions and location, but
	 * both is based on the same syntactical element. This element consists of a name, an
	 * attribute region (surrounded by round brackets) and a region demarcation (consisting of curly
	 * brackets and a concluding $. If there is a name it can be repeated for additional sanity
	 * <code>{...}[name]$</code>). An fluyt expression consists of a leading
	 * $ (dollar sign) and one or more of the three parts. Whitespace between those three elements
	 * is not supported, but it's allowed to use empty attribute brackets.
	 * <p>
	 * Fluyt is designed for addhoc templating. Which means short templates in an string literal. In
	 * a context like this every character counts.
	 * </p>
	 * <p>
	 * <code>
	 * $name(default='def'){$other_name}name$<br />
	 * <b>some text</b>$variable()<b>more text</b><br />
	 * ${<b>if and only if </b>$variable<b> is set</b>}$<br />
	 * $empty-region{<br />
	 * }$ <br />
	 * /// <i>however, the comment is an additional syntactical element and spans always a complete line</i>
	 * </code>
	 * </p>
	 */
	FLUYT,

	/**
	 * Is a fluyt syntax, that can be optionally be coated into C-style comments. Start and end tokens of
	 * regions as well as locations can be put into comment regions or be prefixed by a double slash.
	 * Using and omitting the comment tokens can be mixed in any possible way. Tabs and spaces are allowed
	 * between comment tokens and mark up. For locations there is an addition mock element before the closing
	 * attribute bracket. Arbitrary text is demarcated by an inverse comment region (<code>*&#47mock/*</code>).
	 * <p>
	 * <pre>
	 * &#47;* $name(default='def'){ *&#47;<b>some text</b>//$other-name}name$
	 * <b>some text</b>/*$variable(*&#47;some mock up/*)*&#47;<b>more text</b>
	 * /* ${<b>if and only if </b>$variable<b> is set</b>}$*&#47;
	 * // $empty-region{
	 * // }$
	 * /// <i>however, the comment does not support additional coating</i>
	 * </pre>
	 * </p>
	 */
	FLUYT_CC,

	/**
	 * Adds tag with a name space specifier 't' for region definition to a full fluyt syntax. This allows
	 * more convenient integration within XML editors and is invisible within HTML files. As such it allows
	 * HTML templates that are viewable in the browser, while the fluyt syntax allows concise in tag logic.
	 * <p>
	 * <pre>
	 * &lt;t:region>
	 * <b>&lt;div</b> ${<b>title="</b>$optional-title<b>"</b>}$ ${<b>style="</b>$optional-style<b>"</b>}$>
	 * <b>&lt;a href="../</b>$productId(enc="url")<b>/details.html"></b>$(msg="view_details")<b>&lt;/a></b>
	 * <b>&lt;/div></b>
	 * &lt;/t:region>
	 * </pre>
	 * </p>
	 */
	FLUYT_X,

	/**
	 * This syntax allows template markup, that is invisible to the parser
	 * of many output formats. There are variants based on &lt;!-- --> and on
	 * &#47;* *&#47; that can be mixed freely as needed:
	 * <p>
	 * <pre>
	 * &#47;*t:name1-->{v:name2}&lt;!--t:name3 default='def'-->&#47;*!t:name3*&#47;
	 * <--!t:name1*&#47;
	 * </pre>
	 * </p>
	 */
	HIDDEN_BLOCKS;

	@Override
    public String getName() { return name(); }

	/**
	 *  a template context pre-configured for selected syntax
	 */
	public TemplateContext context() {
		return new TemplateContext().syntax(this);
	}

	/**
	 * parse with selected syntax
	 * @param data input data to parse
	 * @return the template for the parsed data
	 */
	public org.jproggy.snippetory.Repo.TemplateContext read(CharSequence data) {
		return Repo.read(data).syntax(this);
	}

	/**
	 * Parse with selected syntax and locale. Using a locale activates locale
	 * specific formatting system.
	 * @param data input data to parse
	 * @param locale to be used in formats
	 * @return the template for the parsed data
	 */
	public Template parse(CharSequence data, Locale locale) {
		return context().locale(locale).parse(data);
	}

	/**
	 * parse with selected syntax
	 * @param data input data to parse
	 * @return the template for the parsed data
	 */
	public Template parse(CharSequence data) {
		return context().parse(data);
	}
}