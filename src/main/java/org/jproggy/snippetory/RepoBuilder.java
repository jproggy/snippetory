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
package org.jproggy.snippetory;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jproggy.snippetory.engine.NoDataException;

/**
 * To provide a maximum of flexibility out of the box the RepoBuilder allows
 * to combine several <code>UriResolver</code>s to a repository. Such a
 * repository in turn has a fall back mechanism or search order.
 * <p>
 * This makes it simple to allow a customer to overwrite the default templates
 * (delivered as resources within a jar) by putting replacements on a ftp
 * server or in a folder in file system, whatever is appropriate.
 */
public class RepoBuilder extends UriResolver {
  private List<UriResolver> parts =  new ArrayList<UriResolver>();

  public RepoBuilder addDirectories(final String... dirs) {
    return add(UriResolver.directories(dirs));
  }

  /**
   * Creates a UriResolver that sequentially scans the provided directories for
   * a file that's represented by the uri and returns the content of the first
   * match.
   */
  public RepoBuilder addDirectories(final File... dirs) {
    return add(UriResolver.directories(dirs));
  }

  /**
   * Resolves to a resource. The resource has to be queried as described in
   * {@link ClassLoader#getResource(String)}. The used Classloader is typically
   * the {@link Thread#getContextClassLoader() ContextClassloader}
   *
   */
  public  RepoBuilder addResource() {
    return add(UriResolver.resource());
  }

  /**
   * Allows resource lookup from packages with a short name as well as
   * organizing template sets in different packages.
   */
  public  RepoBuilder addResource(String prefix) {
    return add(UriResolver.resource(prefix));
  }

  /**
   * resolves uris relative to the base URL
   */
  public  RepoBuilder addUrl(String base) {
    return add(UriResolver.url(base));
  }

  /**
   * resolves uris relative to the base URL
   */
  public  RepoBuilder addUrl(final URL base) {
    return add(UriResolver.url(base));
  }
  /**
   * resolves uris relative to the base URL
   */
  public  RepoBuilder add(UriResolver part) {
    parts.add(part);
    return this;
  }

  @Override
  public String resolve(String uri, TemplateContext context) {
    if (parts.isEmpty()) {
      throw new NoDataException("Empty repository. " +
      		"Please use add... methods to build an meaningfull repository");
    }
    List<Exception> exceptions = new ArrayList<Exception>();
    for (UriResolver part: parts) {
      try {
        String result = part.resolve(uri, context);
        if (result != null) {
          return result;
        }
      } catch (Exception e) {
        exceptions.add(e);
      }
    }
    throw new NoDataException("No Data found for uri: " + uri, exceptions);
  }

}
