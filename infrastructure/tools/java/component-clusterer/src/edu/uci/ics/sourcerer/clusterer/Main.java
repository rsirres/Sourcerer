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

import edu.uci.ics.sourcerer.clusterer.cloning.CloningStatistics;
import edu.uci.ics.sourcerer.clusterer.cloning.method.fingerprint.FingerprintClusterer;
import edu.uci.ics.sourcerer.clusterer.cloning.method.fqn.FqnClusterer;
import edu.uci.ics.sourcerer.clusterer.cloning.method.hash.HashingClusterer;
import edu.uci.ics.sourcerer.clusterer.usage.project.UsageComputer;
import edu.uci.ics.sourcerer.db.util.DatabaseConnection;
import edu.uci.ics.sourcerer.repo.general.AbstractRepository;
import edu.uci.ics.sourcerer.util.io.Command;
import edu.uci.ics.sourcerer.util.io.Properties;
import edu.uci.ics.sourcerer.util.io.PropertyManager;

/**
 * @author Joel Ossher (jossher@uci.edu)
 *
 */
public class Main {
//  public static final Command GENERATE_DIRECTORY_LISTING =
//    new Command("generate-dir-listing", "Generates the directory listing file.") {
//      protected void action() {
//        DirectoryClusterer.generateDirectoryListing();
//      }
//    }.setProperties(AbstractRepository.INPUT_REPO, DirectoryClusterer.DIRECTORY_LISTING);
//  
//  public static final Command GENERATE_COMPARISON_FILES =
//    new Command("generate-comparison-files", "Performs basic directory comparison.") {
//      protected void action() {
//        DirectoryClusterer.generateComparisonFiles();
//      }
//    }.setProperties(Properties.INPUT, DirectoryClusterer.DIRECTORY_LISTING, DirectoryClusterer.MINIMUM_MATCH_SIZE, DirectoryClusterer.POPULAR_DISCARD, DirectoryClusterer.MATCHED_DIRECTORIES, DirectoryClusterer.MATCHED_FILES, DirectoryClusterer.POPULAR_NAMES, TablePrettyPrinter.CSV_MODE);
//    
//  public static final Command GENERATE_FQN_FILE_LISTING =
//    new Command("generate-fqn-file-listing", "Generates the fqn file listing file.") {
//      protected void action() {
//        FqnClusterer.generateFileListing();
//      }
//    }.setProperties(FqnClusterer.FQN_FILE_LISTING, DatabaseConnection.DATABASE_URL, DatabaseConnection.DATABASE_USER, DatabaseConnection.DATABASE_PASSWORD);
//    
//    public static final Command GENERATE_FINGERPRINT_FILE_LISTING =
//      new Command("generate-fingerprint-file-listing", "Generates the fingerprint file listing file.") {
//        protected void action() {
//          FingerprintClusterer.generateFileListing();
//        }
//      }.setProperties(FingerprintClusterer.FINGERPRINT_FILE_LISTING, FqnClusterer.FQN_FILE_LISTING, DatabaseConnection.DATABASE_URL, DatabaseConnection.DATABASE_USER, DatabaseConnection.DATABASE_PASSWORD);
//  
////  public static final Command COMPILE_DIR_STATISTICS =
////    new Command("compile-dir-stats", "Compile statistics from directory comparison.") {
////      protected void action() {
////        DirectoryClusterer.compileStatistics();
////      }
////    }.setProperties(Properties.INPUT, DirectoryClusterer.MATCHED_DIRECTORIES, DirectoryClusterer.MATCHED_FILES);
//  
////  public static final Command INTERACTIVE_RESULTS =
////    new Command("interactive-results", "Interactive results viewer.") {
////      protected void action() {
////        DirectoryClusterer.interactiveResultsViewer();
////      }
////    }.setProperties(Properties.INPUT, DirectoryClusterer.DIRECTORY_LISTING, DirectoryClusterer.MINIMUM_DIR_SIZE, DirectoryClusterer.POPULAR_DISCARD);
//  
  public static final Command GENERATE_HASH_FILE_LISTING =
    new Command("generate-hash-file-listing", "Generates the hash file listing file.") {
      protected void action() {
        HashingClusterer.generateFileListing();
      }
    }.setProperties(AbstractRepository.INPUT_REPO, HashingClusterer.HASH_FILE_LISTING.asOutput());
    
  public static final Command GENERATE_FQN_FILE_LISTING =
    new Command("generate-fqn-file-listing", "Generates the fqn file listing file.") {
      protected void action() {
        FqnClusterer.generateFileListing();
      }
    }.setProperties(AbstractRepository.INPUT_REPO, FqnClusterer.FQN_FILE_LISTING.asOutput(), DatabaseConnection.DATABASE_URL, DatabaseConnection.DATABASE_USER, DatabaseConnection.DATABASE_PASSWORD);
    
  public static final Command GENERATE_FINGERPRINT_FILE_LISTING =
    new Command("generate-fingerprint-file-listing", "Generates the fingerprint file listing file.") {
      protected void action() {
        FingerprintClusterer.generateFileListing();
      }
    }.setProperties(AbstractRepository.INPUT_REPO, FingerprintClusterer.FINGERPRINT_FILE_LISTING.asOutput(), DatabaseConnection.DATABASE_URL, DatabaseConnection.DATABASE_USER, DatabaseConnection.DATABASE_PASSWORD);

//  public static final Command PRINT_HASH_PROJECT_MATCHING_RATES =
//    new Command("print-hash-project-matching-rates", "Prints the project-project matching rates using the hashing method.") {
//      protected void action() {
//        HashingClusterer.printProjectMatchingRates();
//      }
//    }.setProperties(Properties.INPUT, HashingClusterer.HASH_FILE_LISTING);
    
  public static final Command PERFORM_CLONING_ANALYSIS =
    new Command("perform-cloning-analysis", "Analyzes the cloning results.") {
      protected void action() {
        CloningStatistics.performAnalysis();
      }
    }.setProperties(Properties.INPUT, HashingClusterer.HASH_FILE_LISTING.asInput(), FqnClusterer.FQN_FILE_LISTING.asInput(), FingerprintClusterer.FINGERPRINT_FILE_LISTING.asInput(), CloningStatistics.COMPARE_FILE_SETS, CloningStatistics.COMPUTE_CLONING_STATS);

  public static final Command COMPUTE_ENTITY_LISTING =
    new Command("compute-entity-listing", "Computes the list of entities starting with the specified prefix.") {
      protected void action() {
        UsageComputer.computeEntityListing();
      }
    }.setProperties(UsageComputer.ENTITY_LISTING_FILE.asOutput(), UsageComputer.FQN_PREFIX, DatabaseConnection.DATABASE_URL, DatabaseConnection.DATABASE_USER, DatabaseConnection.DATABASE_PASSWORD);

  public static final Command COMPUTE_USAGE_LISTING =
    new Command("compute-usage-listing", "") {
      protected void action() {
        UsageComputer.computeUsageListing();
        
      }
    }.setProperties(Properties.INPUT, UsageComputer.ENTITY_LISTING_FILE.asInput(), UsageComputer.USAGE_LISTING_FILE.asInput(), DatabaseConnection.DATABASE_URL, DatabaseConnection.DATABASE_USER, DatabaseConnection.DATABASE_PASSWORD);
//  public static final Command COMPUTE_GENERAL_STATISTICS =
//    new Command("compute-general-stats", "Compute general statistics.") {
//      protected void action() {
//        Aggregator.computeGeneralStats();
//      }
//    }.setProperties(Properties.INPUT, HashingClusterer.HASH_FILE_LISTING, Aggregator.GENERAL_STATISTICS);
//    
//  public static final Command COMPUTE_FILTERED_STATISTICS =
//      new Command("compute-filtered-stats", "Compute filtered general statistics.") {
//        protected void action() {
//          Aggregator.computeFilteredStats();
//        }
//      }.setProperties(Properties.INPUT, HashingClusterer.HASH_FILE_LISTING, Aggregator.FILTERED_STATISTICS);
//      
//  public static final Command GENERATE_MATCHING_COMPARISON  =
//    new Command("generate-matching-comparison", "Generate pairwise matching comparison.") {
//      protected void action() {
//        Aggregator.generateMatchingComparison();
//      }
//    }.setProperties(Properties.INPUT, HashingClusterer.HASH_FILE_LISTING, Aggregator.MATCHING_COMPARISON, Aggregator.COMPARISON_STATISTICS);      
//  
//  public static final Command GENERATE_INTERSECTION_FILE =
//    new Command("generate-intersection-file", "Compares the file listings from the different methods.") {
//      protected void action() {
//        Verifier.generateIntersectionFile();
//      }
//    }.setProperties(Properties.INPUT, HashingClusterer.HASH_FILE_LISTING, DirectoryClusterer.DIRECTORY_LISTING, FqnClusterer.FQN_FILE_LISTING, FingerprintClusterer.FINGERPRINT_FILE_LISTING, Verifier.INTERSECTION_FILE_LISTING);
//    
//  public static final Command COMPUTE_INTERSECTION_PROJECT_SIZES =
//    new Command("compute-intersection-project-sizes", "") {
//      @Override
//      protected void action() {
//        Verifier.computeIntersectionProjectSizes();
//      }
//    }.setProperties(Properties.INPUT, Verifier.INTERSECTION_FILE_LISTING, Verifier.INTERSECTION_PROJECT_SIZES);
//    
//  public static final Command GENERATE_FILTERED_LIST =
//    new Command("generate-filtered-list", "Blah blah.") {
//      protected void action() {
//        Verifier.generateFilteredList();
//      }
//    }.setProperties(Properties.INPUT, HashingClusterer.HASH_FILE_LISTING, DirectoryClusterer.MATCHED_FILES, DirectoryClusterer.FILTERED_MATCHED_FILES, FqnClusterer.FQN_FILE_LISTING, FingerprintClusterer.FINGERPRINT_FILE_LISTING, Verifier.INTERSECTION_FILE_LISTING);
//
//  // FQN USAGE COMMANDS
//  public static final Command GENERATE_FQN_USAGE_LISTING =
//    new Command("generate-fqn-usage-listing", "Generate a fqn usage listing file.") {
//      @Override
//      protected void action() {
//        UsageGenerator.generateFqnUsageListing();
//      }
//    }.setProperties(UsageGenerator.FQN_USAGE_LISTING, DatabaseConnection.DATABASE_URL, DatabaseConnection.DATABASE_USER, DatabaseConnection.DATABASE_PASSWORD);
//      
//  public static final Command CREATE_USAGE_TREE =
//    new Command("create-usage-tree", "Creates the fqn usage tree.") {
//      @Override
//      protected void action() {
//        UsageGenerator.createFqnUsageTree();
//      }
//    }.setProperties(Properties.INPUT, UsageGenerator.FQN_USAGE_LISTING, FqnTree.FQN_TREE);
//  
//  public static final Command COMPUTE_TOP_REFERENCED_FRAGMENTS =
//    new Command("compute-top-referenced-fragments", "Compute top referenced fragments.") {
//      @Override
//      protected void action() {
//        UsageGenerator.printTopReferencedFragments();
//      }
//    }.setProperties(Properties.INPUT, UsageGenerator.TOP_COUNT, UsageGenerator.TOP_REFERENCED_FRAGMENTS_FILE, FqnTree.FQN_TREE);
//  
//  public static final Command GENERATE_IMPORT_USAGE_LISTING =
//    new Command("generate-import-usage-listing", "Generate an import usage listing file.") {
//      @Override
//      protected void action() {
//        ImportUsageGenerator.generateImportUsageListing();
//      }
//    }.setProperties(ImportUsageGenerator.IMPORT_USAGE_LISTING, DatabaseConnection.DATABASE_URL, DatabaseConnection.DATABASE_USER, DatabaseConnection.DATABASE_PASSWORD);
  
  public static void main(String[] args) {
    PropertyManager.executeCommand(args, Main.class);
  }
}
