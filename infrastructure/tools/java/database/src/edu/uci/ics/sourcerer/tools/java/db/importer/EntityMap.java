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
import java.util.Collections;
import java.util.HashMap;

import edu.uci.ics.sourcerer.tools.java.model.types.RelationClass;
import edu.uci.ics.sourcerer.utils.db.QueryExecutor;

/**
 * @author Joel Ossher (jossher@uci.edu)
 */
class EntityMap extends AbstractEntityMap {
  private LibraryEntityMap libraries;
  private QueryExecutor exec;
  
  public EntityMap(QueryExecutor exec, LibraryEntityMap libraries) {
    super(Collections.<Integer>emptySet());
    this.libraries = libraries;
    this.exec = exec;
    entities = Collections.emptyMap();
  }
  
  public EntityMap(QueryExecutor exec, LibraryEntityMap libraries, Collection<Integer> projects) {
    super(projects);
    this.libraries = libraries;
    this.exec = exec;
    entities = new HashMap<>();
  }
  
  @Override
  protected DatabaseEntity makeEntity(Integer entityID) {
    return DatabaseEntity.make(entityID, RelationClass.EXTERNAL);
  }
  
  public DatabaseEntity getEntity(String fqn) {
    DatabaseEntity entity = entities.get(fqn);
    if (entity == null) {
      if (!projects.isEmpty()) {
        entity = checkVirtualBinding(exec, fqn);
      }
      if (entity == null) {
        entity = libraries.getEntity(exec, fqn);
      }
    }
    return entity;
  }
}