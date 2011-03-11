package de.jproggy.templa;


/**
 * The template is the central interface of the Templa Template Engine.
 * 
 * It abstracts from most technical issues of the output by handling several
 * typical issues when generating textual languages interpreted by other machines
 * and presented to humans all over the world. On one hand any target language needs
 * some help to maintain syntactical correctness.
 * Encoding are designed to deliver this help by escaping characters or terms to
 * keep their original meaning instead of confusing the target parser or provide 
 * possibilities for several types of attacks. As the needs differ and grow additional 
 * encodings can be added and existing can be replaced.
 * To achieve a professional presentation to people of different languages, throughout
 * a mix of transmission languages and technologies you need a quite flexible and
 * robust formatting system. This is provided with a chain of type sensitive 
 * formatters applied just before encoding.
 * And s formatting is part of the look of the result it is useful to have it in the 
 * Template.
 * 
 * The next important aspect of the design of Templa is, that logic separated from view.
 * No loops, no conditions, no variable definitions and manipulations in the template.
 * (by the way, we'll talk of variables in some cases, but formally that are just 
 * location marks in the template not a declaration and several usages, that have to
 * be in sequence) And freeing template from logic means freeing from context. And
 * in consequence it is easy to take a peace of template and use it where appropriate. 
 * This means an abstraction from template storage and organization and allows one to 
 * organize the template structure as needed.
 * 
 * @author Sir RotN
 */

public interface Template  {

	/**
	 * @return a clean instance of the child template identified by the name  or null if 
	 * there is none with this name. It's undefined if this is a new copy or if only a
	 * single instance exists. Though subsequent calls an get on the same instance with
	 * the same name might clear the instances returned by previous call or not. 
	 */
	Template get(String... name);
	
	/**
	 * Sets all variables with given name to a String representation of the value.
	 * Exact value might differ according to different meta data associated with
	 * each of these variables. Eventually set or appended data is overwritten.
	 * All matching formats and encodings are used, with exception of Templates.
	 * Templates are never formatted or encoded.
	 * 
	 * @return the template itself
	 */
	Template set(String name, Object value);
	
	/**
	 * This is used to write already encoded data like a peace of text rendered with
	 * other mechanisms or locale entries that contain mark up. Use with great care
	 * as it bypasses the abstraction layer of the template. In most cases it is better
	 * to use container types 
	 *   
	 * @return the template itself
	 */
	Template append(String name, Object value);
	Template clear();
	
	void render();
	void render(String target);
	void render(Template target, String key);
}
