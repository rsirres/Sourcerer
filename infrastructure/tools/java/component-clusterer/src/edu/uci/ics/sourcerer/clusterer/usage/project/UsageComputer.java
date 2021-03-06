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
package edu.uci.ics.sourcerer.clusterer.usage.project;

import static edu.uci.ics.sourcerer.util.io.Logging.logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import edu.uci.ics.sourcerer.db.queries.InlineDatabaseAccessor;
import edu.uci.ics.sourcerer.model.Relation;
import edu.uci.ics.sourcerer.model.db.MediumEntityDB;
import edu.uci.ics.sourcerer.util.Helper;
import edu.uci.ics.sourcerer.util.io.FileUtils;
import edu.uci.ics.sourcerer.util.io.LineFileReader;
import edu.uci.ics.sourcerer.util.io.LineFileWriter;
import edu.uci.ics.sourcerer.util.io.LineFileWriter.EntryWriter;
import edu.uci.ics.sourcerer.util.io.Property;
import edu.uci.ics.sourcerer.util.io.properties.IOFilePropertyFactory;
import edu.uci.ics.sourcerer.util.io.properties.StringProperty;

/**
 * @author Joel Ossher (jossher@uci.edu)
 */
public class UsageComputer {
  public static final Property<String> FQN_PREFIX = new StringProperty("fqn-prefix", "String prefix for which to compute project usage information.");
  
  public static final IOFilePropertyFactory ENTITY_LISTING_FILE = new IOFilePropertyFactory("entity-listing-file", "entity-listing.txt", "File containing the list of entities matching the fqn prefix.");
  public static final IOFilePropertyFactory USAGE_LISTING_FILE = new IOFilePropertyFactory("usage-listing-file", "usage-listing.txt", "File containing the usage listing.");
  
  public static void computeEntityListing() {
    new InlineDatabaseAccessor() {
      @Override
      public void action() {
        logger.info("Computing entity listing for: " + FQN_PREFIX.getValue());
        
        logger.info("Finding entities matching string...");
        Map<String, Set<Integer>> fqnMap = Helper.newHashMap(); 
        for (MediumEntityDB entity : entityQueries.getMediumReferenceableByFqnPrefix(FQN_PREFIX.getValue())) {
          Set<Integer> ids = fqnMap.get(entity.getFqn());
          if (ids == null) {
            ids = Helper.newHashSet();
            fqnMap.put(entity.getFqn(), ids);
          }
          ids.add(entity.getEntityID());
        }
        
        logger.info("Writing listing to file...");
        LineFileWriter writer = null;
        try {
          writer = FileUtils.getLineFileWriter(ENTITY_LISTING_FILE);
          EntryWriter<MatchingFqnEntities> ew = writer.getEntryWriter(MatchingFqnEntities.class);
          MatchingFqnEntities entities = new MatchingFqnEntities();
          for (Map.Entry<String, Set<Integer>> entry : fqnMap.entrySet()) {
            entities.setFqn(entry.getKey());
            int[] ids = new int[entry.getValue().size()];
            int idx = 0;
            for (Integer i : entry.getValue()) {
              ids[idx++] = i;
            }
            entities.setIds(ids);
            ew.write(entities);
          }
        } catch (IOException e) {
          logger.log(Level.SEVERE, "Error writing file.", e);
        } finally {
          FileUtils.close(writer);
        }
        logger.info("Done!");
      }
    }.execute();
  }
  
  public static void computeUsageListing() {
    new InlineDatabaseAccessor() {
      @Override
      public void action() {
        logger.info("Computing usage listing for " + ENTITY_LISTING_FILE.asInput().getValue().getName());
        
        LineFileReader reader = null;
        BufferedWriter writer = null;
        try {
          reader = FileUtils.getLineFileReader(ENTITY_LISTING_FILE);
          writer = FileUtils.getBufferedWriter(USAGE_LISTING_FILE);
          writer.write("fqn@times called@projects calling@times used@projects using");
          writer.newLine();
          Collection<Integer> totalProjects = Helper.newHashSet();
          Collection<Integer> projectsCalling = Helper.newHashSet();
          Collection<Integer> projectsUsing = Helper.newHashSet();
          for (MatchingFqnEntities matching : reader.readNextToIterable(MatchingFqnEntities.class, true)) {
            int calledCount = 0;
            projectsCalling.clear();
            int usedCount = 0;
            projectsUsing.clear();
            for (int id : matching.getIds()) {
              calledCount += relationQueries.getRelationCountBy(id, Relation.CALLS, Relation.INSTANTIATES);
              projectsCalling.addAll(relationQueries.getRelationProjectCountBy(id, Relation.CALLS, Relation.INSTANTIATES));
              usedCount += relationQueries.getRelationCountBy(id, Relation.USES);
              projectsUsing.addAll(relationQueries.getRelationProjectCountBy(id, Relation.USES));
            }
            totalProjects.addAll(projectsCalling);
            totalProjects.addAll(projectsUsing);
            writer.write(matching.getFqn() + "@" + calledCount + "@" + projectsCalling.size() + "@" + usedCount + "@" + projectsUsing.size());
            writer.newLine();
          }
          logger.info("Total projects: " + totalProjects.size());
        } catch (IOException e) {
          logger.log(Level.SEVERE, "Error reading file.", e);
        } finally {
          FileUtils.close(writer);
        }
        logger.info("Done!");
      }
    }.execute();
  }
}
