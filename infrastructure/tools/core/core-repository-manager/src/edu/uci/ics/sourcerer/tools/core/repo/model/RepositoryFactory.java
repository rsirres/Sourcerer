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
package edu.uci.ics.sourcerer.tools.core.repo.model;

import java.io.File;

import edu.uci.ics.sourcerer.tools.core.repo.model.internal.InternalRepositoryFactory;
import edu.uci.ics.sourcerer.util.io.arguments.Argument;
import edu.uci.ics.sourcerer.util.io.arguments.FileArgument;

/**
 * @author Joel Ossher (jossher@uci.edu)
 */
public abstract class RepositoryFactory {
  public static final Argument<File> INPUT_REPO = new FileArgument("input-repo", "The root directory of the input repository.");
  public static final Argument<File> OUTPUT_REPO = new FileArgument("output-repo", "The root directory of the output repository.");

  public static final RepositoryFactory INSTANCE = new InternalRepositoryFactory(); 
  
  public abstract IRepository<? extends ISourceProject, ? extends IBatch<? extends ISourceProject>> loadSourceRepository(Argument<File> root);
  
  public abstract IRepositoryMod<? extends ISourceProjectMod, ? extends IBatchMod<? extends ISourceProjectMod>> loadModifiableSourceRepository(Argument<File> root);
}