package edu.nus.soc.sourcerer.ddb.tools;

import java.io.IOException;
import java.util.Vector;

import org.apache.commons.lang.NotImplementedException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableExistsException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;

import edu.nus.soc.sourcerer.ddb.HBaseConnectionException;
import edu.nus.soc.sourcerer.ddb.HBaseException;
import edu.nus.soc.sourcerer.ddb.tables.RelationsDirectHBTable;
import edu.nus.soc.sourcerer.ddb.tables.EntitiesHBTable;
import edu.nus.soc.sourcerer.ddb.tables.EntitiesHashHBTable;
import edu.nus.soc.sourcerer.ddb.tables.FilesHBTable;
import edu.nus.soc.sourcerer.ddb.tables.RelationsHashHBTable;
import edu.nus.soc.sourcerer.ddb.tables.RelationsInverseHBTable;
import edu.nus.soc.sourcerer.ddb.tables.ProjectsHBTable;

import static edu.nus.soc.sourcerer.ddb.Commons.LOG;

/**
 * This class is responsible with initialisation operations which prepare the
 * database to used. This operations include the creation of the tables.
 * 
 * @author Calin-Andrei Burloiu
 *
 */
public class DatabaseInitializer {
  
  HBaseAdmin admin = null;
  protected Vector<HTableDescriptor> tables = null;
  
  /**
   * Construct object that will initialize the table identified by the 
   * descriptor. Pass null to initialize all tables.
   * 
   * @param tableDescriptor descriptor of table to be initialized
   * @throws HBaseConnectionException
   */
  public DatabaseInitializer(HTableDescriptor tableDescriptor)
      throws HBaseConnectionException {
    Configuration conf = HBaseConfiguration.create();
    try {
      admin = new HBaseAdmin(conf);
    } catch (ZooKeeperConnectionException e) {
      throw new HBaseConnectionException(e);
    } catch (MasterNotRunningException e) {
      throw new HBaseConnectionException(e);
    }
    
    tables = new Vector<HTableDescriptor>(16);
    if (tableDescriptor == null) {
      tables.add(ProjectsHBTable.getTableDescriptor());
      tables.add(FilesHBTable.getTableDescriptor());
      tables.add(EntitiesHashHBTable.getTableDescriptor());
      tables.add(EntitiesHBTable.getTableDescriptor());
      tables.add(RelationsHashHBTable.getTableDescriptor());
      tables.add(RelationsInverseHBTable.getTableDescriptor());
      tables.add(RelationsDirectHBTable.getTableDescriptor());
    }
    else {
      tables.add(tableDescriptor);
    }
  }
  
  public DatabaseInitializer() throws HBaseConnectionException {
    this(null);
  }
  
  /**
   * Start initialising HBase tables by creating them. In case a table already
   * exists, the parameters configure if it must be emptied or altered in case
   * the schema is not the same.
   * 
   * @param emptyExisting empty tables that already exist
   * @param updateExisting alter tables that already exist and have a different
   * schema
   */
  public void start(boolean emptyExisting, boolean updateExisting)
      throws HBaseException {
    for (HTableDescriptor tableDesc : tables) {
      try {
        createTable(tableDesc);
        LOG.info("Table `" + tableDesc.getNameAsString() + "` created.");
      } catch (TableExistsException e) {
        LOG.info("Table `" + tableDesc.getNameAsString() + "` already exists.");
        
        if (emptyExisting) {
          emptyTable(tableDesc.getName());
          try {
            createTable(tableDesc);
          } catch (TableExistsException e1) {
            throw new Error("Error while recreating table `"
                + tableDesc.getNameAsString()
                + "`; this table shouldn't exist.");
          }
          LOG.info("Table `" + tableDesc.getNameAsString() + "` emptied.");
        }
        
        if (updateExisting && !emptyExisting) {
          if (updateTable(tableDesc))
            LOG.info("Table `" + tableDesc.getNameAsString() + "` modified.");
          else
            LOG.info("Table `" + tableDesc.getNameAsString()
                + "` already exists and did not required modifications.");
        }
      }
    }
  }
  
  /**
   * Create an HBase table.
   * 
   * @param tableDesc
   * @throws HBaseConnectionException
   * @throws TableExistsException
   */
  public void createTable(HTableDescriptor tableDesc) 
      throws HBaseException, TableExistsException {
    try {
      admin.createTable(tableDesc);
    } catch (MasterNotRunningException e) {
      throw new HBaseConnectionException(e);
    } catch (TableExistsException e) {
      throw e;
    } catch (IOException e) {
      throw new HBaseException(e);
    }
  }
  
  /**
   * Empties an HBase table.
   * 
   * @param tableName
   * @throws HBaseConnectionException
   */
  public void emptyTable(byte[] tableName)
      throws HBaseException {
    try {
      if (!admin.isTableDisabled(tableName)) {
        admin.disableTable(tableName);
      }
      admin.deleteTable(tableName);
    } catch (IOException e) {
      throw new HBaseException(e);
    }
  }
  
  /**
   * Modifies schema and properties of an HBase table if they differ from those
   * provided as parameters. If they do not nothing happens. 
   * 
   * @param tableDesc
   * @return true if the table was modified
   * @throws HBaseConnectionException
   */
  public boolean updateTable(HTableDescriptor tableDesc) 
      throws HBaseConnectionException {
    // TODO updateTable
    throw new NotImplementedException();
  }
  
  // FIXME remove testing main
  public static void main(String args[]) {
    try {
      DatabaseInitializer dbInit = new DatabaseInitializer();
      dbInit.start(false, false);
    } catch (HBaseConnectionException e) {
      e.printStackTrace();
    } catch (HBaseException e) {
      e.printStackTrace();
    }
  }

}
