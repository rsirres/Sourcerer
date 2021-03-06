package edu.nus.soc.sourcerer.model.ddb;

import org.apache.commons.lang.StringUtils;

import edu.nus.soc.sourcerer.util.EnumUtil;
import edu.nus.soc.sourcerer.util.Serialization;
import edu.uci.ics.sourcerer.model.Project;

/**
 * Default model for HBase `projects` table.
 * 
 * @author Calin-Andrei Burloiu
 * 
 */
public class ProjectModel extends ModelWithID {
  protected Byte type;
  protected String name;
  protected String description;
  protected String version;
  protected String group;
  protected String path;
  protected byte[] hash = null;
  protected Boolean hasSource;

  // Line of Code
  protected Integer loc;
  // Non white space Lines of Code
  protected Integer nwloc;

  public ProjectModel(byte[] projectID, Byte type, String name,
      String description, String version, String group, String path,
      byte[] hash, Boolean hasSource, Integer loc, Integer nwloc) {
    super(projectID);
    this.type = type;
    this.name = name;
    this.description = description;
    this.version = version;
    this.group = group;
    this.path = path;
    this.hash = hash;
    this.hasSource = hasSource;
    this.loc = loc;
    this.nwloc = nwloc;
  }
  
  public ProjectModel(Byte type, String name,
      String description, String version, String group, String path,
      byte[] hash, Boolean hasSource, Integer loc, Integer nwloc) {
    super();
    this.type = type;
    this.name = name;
    this.description = description;
    this.version = version;
    this.group = group;
    this.path = path;
    this.hash = hash;
    this.hasSource = hasSource;
    this.loc = loc;
    this.nwloc = nwloc;
    
    // Compute ID.
    if (type == Project.SYSTEM.getValue()) {
      if (name.equals("primitives")) {
        id = new byte[] {'p', 'r', 'i', 'm', 'i', 't', 'i', 'v', 'e', 's',
            '\0', '\0', '\0', '\0', '\0', '\0'};
      } else if (name.equals("unknowns")) {
        id = new byte[] {'u', 'n', 'k', 'n', 'o', 'w', 'n', 's',
            '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0'};
      }
    } else if (type == Project.JAVA_LIBRARY.getValue()
        || type == Project.CRAWLED.getValue()) {
      id = computeId(1024, "path");
    } else if (type == Project.JAR.getValue()
        || type == Project.MAVEN.getValue()) {
      id = hash;
    }
  }
  
  public ProjectModel(Byte type, String name, String path, byte[] hash) {
    this(type, name, null, null, null, path, hash, null, null, null);
  }

  public Byte getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getVersion() {
    return version;
  }

  public String getGroup() {
    return group;
  }

  public String getPath() {
    return path;
  }

  public byte[] getHash() {
    return hash;
  }

  public boolean hasSource() {
    return hasSource;
  }

  public Integer getLoc() {
    return loc;
  }

  public Integer getNwloc() {
    return nwloc;
  }

  @Override
  public String toString() {
    return Serialization.byteArrayToHexString(id)
        + "\n  " + StringUtils.rightPad("name: ", 16) + name
        + "\n  " + StringUtils.rightPad("type: ", 16) + EnumUtil.getEnumByValue(Project.values(), type) + "(0x"
            + Serialization.byteArrayToHexString(new byte[] {type}) + ")"
        + (description != null ? "\n  " + StringUtils.rightPad("description: ", 16) + description : "")
        + (version != null ? "\n  " + StringUtils.rightPad("version: ", 16) + version : "")
        + (group != null ? "\n  " + StringUtils.rightPad("group: ", 16) + group : "")
        + (path != null ? "\n  " + StringUtils.rightPad("path: ", 16) + path : "")
        + (hash != null ? "\n  " + StringUtils.rightPad("hash: ", 16) + Serialization.byteArrayToHexString(hash) : "")
        + "\n  " + StringUtils.rightPad("hasSource: ", 16) + hasSource
        + (loc != null ? "\n  " + StringUtils.rightPad("loc: ", 16) + loc : "")
        + (nwloc != null ? "\n  " + StringUtils.rightPad("nwloc: ", 16) + nwloc : "");
  }
  
//  public static void main(String args[]) {
//    ProjectDDB prj = new ProjectDDB(Project.CRAWLED, "abc", "A", "", "", "",
//        false, 0xCAFEBABE, 0x01020304);
//    System.out.println(HumanReadable.byteArrayToHexString(prj.getId()));
//  }
}
