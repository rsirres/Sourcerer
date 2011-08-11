/* 
 * Sourcerer: an infrastructure for large-scale source code analysis.
 * Copyright (C) by contributors. See CONTRIBUTORS.txt for full list.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package edu.uci.ics.sourcerer.tools.java.repo.model.internal;

import edu.uci.ics.sourcerer.tools.core.repo.model.internal.AbstractRepository;
import edu.uci.ics.sourcerer.tools.core.repo.model.internal.BatchImpl;
import edu.uci.ics.sourcerer.tools.core.repo.model.internal.ProjectLocationImpl;
import edu.uci.ics.sourcerer.tools.core.repo.model.internal.RepoFileImpl;
import edu.uci.ics.sourcerer.tools.java.repo.model.JavaRepository;
import edu.uci.ics.sourcerer.util.io.arguments.Argument;
import edu.uci.ics.sourcerer.util.io.arguments.StringArgument;

/**
 * @author Joel Ossher (jossher@uci.edu)
 */
class JavaRepositoryImpl extends AbstractRepository<JavaProjectImpl> implements JavaRepository<JavaProjectImpl, BatchImpl<JavaProjectImpl>> {
  public static final Argument<String> JARS_DIRECTORY = new StringArgument("jars-dir", "jars", "Repository subdirectory containing jar files.").permit();
  
  
  private JarIndex jarIndex;
  
  protected JavaRepositoryImpl(RepoFileImpl repoRoot) {
    super(repoRoot);
    RepoFileImpl jarsDir = this.repoRoot.getChild(JARS_DIRECTORY.getValue());
    
  }

  @Override
  protected JavaProjectImpl createProject(ProjectLocationImpl loc) {
    return new JavaProjectImpl(this, loc);
  }
}
