package edu.nus.soc.sourcerer.ddb.tables;

import java.io.UnsupportedEncodingException;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.regionserver.StoreFile;
import org.apache.hadoop.hbase.util.Bytes;

import edu.nus.soc.sourcerer.ddb.DatabaseConfiguration;
import edu.nus.soc.sourcerer.util.StringSerializationException;
import edu.uci.ics.sourcerer.model.Entity;

/**
 * This class contains information about HBase entities table including
 * table name, column families and column qualifiers name and a static method
 * which retrieves an HTableDescriptor.
 * 
 * @author Calin-Andrei Burloiu
 *
 */
public class EntitiesHBTable extends HBTable {
  private static EntitiesHBTable instance = null;
  
  public static final String NAME = "entities";
  
  public static final byte[] CF_DEFAULT = Bytes.toBytes("d");
  
  private EntitiesHBTable() {
    super();
  }
  
  public static EntitiesHBTable getInstance() {
    if (instance == null) {
      instance = new EntitiesHBTable();
    }
    return instance;
  }
  
  @Override
  public String getName() {
    return NAME;
  }
  
  @Override
  public void setupHTable() {
    super.setupHTable();
    
    hTable.setScannerCaching(128);
  }

  /**
   * Compute row key.
   * 
   * @param fqn
   * @param projectID
   * @param fileID
   * @return
   */
  public static byte[] row(String fqn, byte[] projectID, byte[] fileID) {
    try {
      return Bytes.add((fqn + '\0').getBytes("UTF-8"), projectID, fileID);
    } catch (UnsupportedEncodingException e) {
      throw new StringSerializationException(e);
    }
  }
  
  /**
   * Compute column qualifier for the default column family.
   * 
   * @param entityType
   * @return
   */
  public static byte[] col(Entity entityType) {
    return new byte[] {entityType.getValue()};
  }
  
  public static HTableDescriptor getTableDescriptor() {
    DatabaseConfiguration dbConf = DatabaseConfiguration.getInstance();
    
    HTableDescriptor tableDesc = new HTableDescriptor(
        dbConf.getTablePrefix() + NAME);
    
    // Default column family
    HColumnDescriptor cfDefault = new HColumnDescriptor(CF_DEFAULT);
    cfDefault.setMaxVersions(3);
    cfDefault.setCompressionType(dbConf.getDefaultCompression());
    cfDefault.setBlockCacheEnabled(false);
    cfDefault.setBloomFilterType(StoreFile.BloomType.ROW);
    tableDesc.addFamily(cfDefault);
    
    return tableDesc;
  }
  
}
