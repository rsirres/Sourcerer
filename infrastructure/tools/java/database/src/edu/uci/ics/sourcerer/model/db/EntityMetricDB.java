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
package edu.uci.ics.sourcerer.model.db;

import edu.uci.ics.sourcerer.model.metrics.Metric;

/**
 * @author Joel Ossher (jossher@uci.edu)
 */
public class EntityMetricDB {
  private Integer projectID;
  private Integer fileID;
  private Integer entityID;
  private Metric metric;
  private int value;
  
  public EntityMetricDB(Integer projectID, Integer fileID, Integer entityID, Metric metric, int value) {
    this.projectID = projectID;
    this.fileID = fileID;
    this.entityID = entityID;
    this.metric = metric;
    this.value = value;
  }

  public Integer getProjectID() {
    return projectID;
  }
  
  public Integer getFileID() {
    return fileID;
  }
  
  public Integer getEntityID() {
    return entityID;
  }

  public Metric getMetric() {
    return metric;
  }

  public int getValue() {
    return value;
  }
}
