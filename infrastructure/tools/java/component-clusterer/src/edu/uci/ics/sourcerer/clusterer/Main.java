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
package edu.uci.ics.sourcerer.clusterer;

import edu.uci.ics.sourcerer.clusterer.dir.DirectoryClusterer;
import edu.uci.ics.sourcerer.clusterer.fingerprint.FingerprintClusterer;
import edu.uci.ics.sourcerer.clusterer.fqn.FqnClusterer;
import edu.uci.ics.sourcerer.clusterer.hash.HashingClusterer;
import edu.uci.ics.sourcerer.clusterer.stats.Aggregator;
import edu.uci.ics.sourcerer.clusterer.stats.Verifier;
import edu.uci.ics.sourcerer.db.util.DatabaseConnection;
import edu.uci.ics.sourcerer.repo.general.AbstractRepository;
import edu.uci.ics.sourcerer.util.io.Command;
import edu.uci.ics.sourcerer.util.io.Properties;
import edu.uci.ics.sourcerer.util.io.PropertyManager;
import edu.uci.ics.sourcerer.util.io.TablePrettyPrinter;

/**
 * @author Joel Ossher (jossher@uci.edu)
 *
 */
public class Main {
  public static final Command GENERATE_DIRECTORY_LISTING =
    new Command("generate-dir-listing", "Generates the directory listing file.") {
      protected void action() {
        DirectoryClusterer.generateDirectoryListing();
      }
    }.setProperties(AbstractRepository.INPUT_REPO, DirectoryClusterer.DIRECTORY_LISTING);
  
  public static final Command GENERATE_COMPARISON_FILES =
    new Command("generate-comparison-files", "Performs basic directory comparison.") {
      protected void action() {
        DirectoryClusterer.generateComparisonFiles();
      }
    }.setProperties(Properties.INPUT, DirectoryClusterer.DIRECTORY_LISTING, DirectoryClusterer.MINIMUM_MATCH_SIZE, DirectoryClusterer.POPULAR_DISCARD, DirectoryClusterer.MATCHED_DIRECTORIES, DirectoryClusterer.MATCHED_FILES, DirectoryClusterer.POPULAR_NAMES, TablePrettyPrinter.CSV_MODE);
    
  public static final Command GENERATE_FQN_FILE_LISTING =
    new Command("generate-fqn-file-listing", "Generates the fqn file listing file.") {
      protected void action() {
        FqnClusterer.generateFileListing();
      }
    }.setProperties(FqnClusterer.FQN_FILE_LISTING, DatabaseConnection.DATABASE_URL, DatabaseConnection.DATABASE_USER, DatabaseConnection.DATABASE_PASSWORD);
    
    public static final Command GENERATE_FINGERPRINT_FILE_LISTING =
      new Command("generate-fingerprint-file-listing", "Generates the fingerprint file listing file.") {
        protected void action() {
          FingerprintClusterer.generateFileListing();
        }
      }.setProperties(FingerprintClusterer.FINGERPRINT_FILE_LISTING, FqnClusterer.FQN_FILE_LISTING, DatabaseConnection.DATABASE_URL, DatabaseConnection.DATABASE_USER, DatabaseConnection.DATABASE_PASSWORD);
  
//  public static final Command COMPILE_DIR_STATISTICS =
//    new Command("compile-dir-stats", "Compile statistics from directory comparison.") {
//      protected void action() {
//        DirectoryClusterer.compileStatistics();
//      }
//    }.setProperties(Properties.INPUT, DirectoryClusterer.MATCHED_DIRECTORIES, DirectoryClusterer.MATCHED_FILES);
  
//  public static final Command INTERACTIVE_RESULTS =
//    new Command("interactive-results", "Interactive results viewer.") {
//      protected void action() {
//        DirectoryClusterer.interactiveResultsViewer();
//      }
//    }.setProperties(Properties.INPUT, DirectoryClusterer.DIRECTORY_LISTING, DirectoryClusterer.MINIMUM_DIR_SIZE, DirectoryClusterer.POPULAR_DISCARD);
  
  public static final Command GENERATE_HASH_FILE_LISTING =
    new Command("generate-hash-file-listing", "Generates the file listing file.") {
      protected void action() {
        HashingClusterer.generateFileListing();
      }
    }.setProperties(AbstractRepository.INPUT_REPO, HashingClusterer.HASH_FILE_LISTING);
  
  public static final Command COMPUTE_GENERAL_STATISTICS =
    new Command("compute-general-stats", "Compute general statistics.") {
      protected void action() {
        Aggregator.computeGeneralStats();
      }
    }.setProperties(Properties.INPUT, HashingClusterer.HASH_FILE_LISTING, Aggregator.GENERAL_STATISTICS);
    
  public static final Command COMPUTE_FILTERED_STATISTICS =
      new Command("compute-filtered-stats", "Compute filtered general statistics.") {
        protected void action() {
          Aggregator.computeFilteredStats();
        }
      }.setProperties(Properties.INPUT, HashingClusterer.HASH_FILE_LISTING, Aggregator.FILTERED_STATISTICS);
      
  public static final Command GENERATE_MATCHING_COMPARISON  =
    new Command("generate-matching-comparison", "Generate pairwise matching comparison.") {
      protected void action() {
        Aggregator.generateMatchingComparison();
      }
    }.setProperties(Properties.INPUT, HashingClusterer.HASH_FILE_LISTING, Aggregator.MATCHING_COMPARISON, Aggregator.COMPARISON_STATISTICS);      
  
  public static final Command GENERATE_INTERSECTION_FILE =
    new Command("generate-intersection-file", "Compares the file listings from the different methods.") {
      protected void action() {
        Verifier.generateIntersectionFile();
      }
    }.setProperties(Properties.INPUT, HashingClusterer.HASH_FILE_LISTING, DirectoryClusterer.DIRECTORY_LISTING, FqnClusterer.FQN_FILE_LISTING, FingerprintClusterer.FINGERPRINT_FILE_LISTING, Verifier.INTERSECTION_FILE_LISTING);
    
  public static void main(String[] args) {
    PropertyManager.executeCommand(args, Main.class);
  }
}
