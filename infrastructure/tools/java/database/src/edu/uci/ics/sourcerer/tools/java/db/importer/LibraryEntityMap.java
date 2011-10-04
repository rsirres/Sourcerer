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
package edu.uci.ics.sourcerer.tools.java.db.importer;

import java.util.Collection;
import java.util.HashMap;

import edu.uci.ics.sourcerer.tools.java.model.types.RelationClass;
import edu.uci.ics.sourcerer.util.io.TaskProgressLogger;
import edu.uci.ics.sourcerer.utils.db.QueryExecutor;

/**
 * @author Joel Ossher (jossher@uci.edu)
 */
class LibraryEntityMap extends AbstractEntityMap {
  private SynchronizedUnknownsMap unknowns;

  public LibraryEntityMap(TaskProgressLogger task, Collection<Integer> projects, SynchronizedUnknownsMap unknowns) {
    super(projects);
    this.unknowns = unknowns;
    entities = new HashMap<>();
    populateMap(task);
  }

  @Override
  protected DatabaseEntity makeEntity(Integer entityID) {
    return DatabaseEntity.make(entityID, RelationClass.JAVA_LIBRARY);
  }
  
  public synchronized DatabaseEntity getEntity(QueryExecutor exec, String fqn) {
    DatabaseEntity entity = entities.get(fqn);
    if (entity == null) {
      entity = checkVirtualBinding(exec, fqn);
      if (entity == null) {
        entity = unknowns.getUnknown(fqn);
      }
    }
    return entity;
  }
}