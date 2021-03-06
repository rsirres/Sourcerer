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
package edu.uci.ics.sourcerer.util.io;

import static edu.uci.ics.sourcerer.util.io.Logging.logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.Deque;
import java.util.Set;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import edu.uci.ics.sourcerer.util.Helper;
import edu.uci.ics.sourcerer.util.Pair;
import edu.uci.ics.sourcerer.util.io.properties.IOFilePropertyFactory;

/**
 * @author Joel Ossher (jossher@uci.edu)
 */
public final class FileUtils {
  protected static final Property<File> TEMP_DIR = new IOFilePropertyFactory("temp-dir", "temp", "Name of temp directory placed into OUTPUT directory").asOutput().register("General");
  
  private FileUtils() {}
  
  public static void close (Closeable closeMe) {
    if (closeMe != null) {
      try {
        closeMe.close();
      } catch (IOException e) {}
    }
  }  
  
  public static void close(ZipFile zipFile) {
    if (zipFile != null) {
      try {
        zipFile.close();
      } catch (IOException e) {}
    }
  }
  
  public static void zipFile(File in, Property<File> out) {
    zipFile(in, out.getValue());
  }
  
  public static void zipFile(File in, File out) {
    ZipOutputStream zos = null;
    try {
      zos = new ZipOutputStream(new FileOutputStream(out));
      Deque<File> stack = Helper.newStack();
      stack.push(in);
      String base = in.getPath();
      while (!stack.isEmpty()) {
        for (File file : stack.pop().listFiles()) {
          if (file.isFile()) {
            ZipEntry entry = new ZipEntry(convertToRelativePath(base, file.getPath()));
            zos.putNextEntry(entry);
            writeFileToStream(file, zos);
          } else {
            stack.push(file);
          }
        }
      }
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error in zipping file: " + in.getPath(), e);
    } finally {
      close(zos);
    }
  }
  
  public static File getTempDir() {
    File tempDir = TEMP_DIR.getValue();
    tempDir = new File(tempDir, "thread-" + Thread.currentThread().getId());
    if (tempDir.exists() || tempDir.mkdirs()) {
      return tempDir;
    } else {
      return null;
    }
  }
  
  public static void resetTempDir() {
    File tempDir = getTempDir();
    for (File file : tempDir.listFiles()) {
      if (file.isDirectory()) {
        deleteDirectory(file);
      } else {
        file.delete();
      }
    }
  }
  
  public static void cleanTempDir() {
    File tempDir = getTempDir();
    deleteDirectory(tempDir);
  }
  
  private static void deleteDirectory(File dir) {
    for (File file : dir.listFiles()) {
      if (file.isDirectory()) {
        deleteDirectory(file);
      } else {
        file.delete();
      }
    }
    dir.delete();
  }
  
  public static String getFileAsString(String path) {
    return getFileAsString(new File(path));
  }
  
  public static String getFileAsString(File file) {
    FileReader reader = null;
    try {
      StringBuilder builder = new StringBuilder();
      reader = new FileReader(file);
      char[] buff = new char[1024];
      for (int read = reader.read(buff); read > 0; read = reader.read(buff)) {
        builder.append(buff, 0, read);
      }
      return builder.toString();
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Unable to read file: " + file.getAbsolutePath(), e);
      return null;
    } finally {
      close(reader);
    }
  }
  
  public static byte[] getFileAsByteArray(File file) {
    if (file == null) {
      return null;
    }
    InputStream is = null;
    try {
      long length = file.length();
      if (length > Integer.MAX_VALUE) {
        logger.log(Level.SEVERE, file.getPath() + " too big to read");
        return null;
      }
      byte[] retval = new byte[(int)length];
      is = new FileInputStream(file);
      int off = 0;
      for (int read = is.read(retval, off, retval.length - off); read > 0; read = is.read(retval, off, retval.length - off)) {
        off += read;
      }
      if (off < retval.length) {
        logger.log(Level.SEVERE, "Unable to completely read file " + file.getPath());
        return null;
      }
      return retval;
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Unable to read file.", e);
      return null;
    } finally {
      close(is);
    }
  }
  
  public static byte[] getInputStreamAsByteArray(InputStream is, int estimated) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream(estimated);
    try {
      byte[] buff = new byte[1024];
      int read = 0;
      while ((read = is.read(buff)) > 0) {
        bos.write(buff, 0, read);
      }
      return bos.toByteArray();
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error reading from stream", e);
      return null;
    } finally {
      close(is);
    }
  }
  
  public static byte[] getFileFragmentAsByteArray(File file, int offset, int length) {
    InputStream is = null;
    try {
      byte[] retval = new byte[length];
      is = new FileInputStream(file);
      while (offset > 0) {
        offset -= is.skip(offset);
      }
      for (int read = is.read(retval, offset, retval.length - offset); read > 0; read = is.read(retval, offset, retval.length - offset)) {
        offset += read;
      }
      if (offset < retval.length) {
        logger.log(Level.SEVERE, "Unable to completely read file " + file.getPath());
        return null;
      }
      return retval;
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Unable to read file.", e);
      return null;
    } finally {
      close(is);
    }
  }
  
  public static byte[] getInputStreamFragmentAsByteArray(InputStream is, int offset, int length) {
    try {
      byte[] buff = new byte[length];
      while (offset > 0) {
        offset -= is.skip(offset);
      }
      
      int read = 0;
      while (read < length) {
        read += is.read(buff, read, length - read);
      }
      return buff;
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error reading from stream", e);
      return null;
    } finally {
      close(is);
    }
  }
  
  public static Set<String> getFileAsSet(File file) {
    BufferedReader br = null;
    try {
      Set<String> set = Helper.newHashSet();
      br = new BufferedReader(new FileReader(file));
      for (String line = br.readLine(); line != null; line = br.readLine()) {
        set.add(line);
      }
      return set;
    } catch (IOException e) {
      return Collections.emptySet();
    } finally {
      close(br);
    }
  }
  
  public static void writeStringToFile(String source, File target) throws IOException {
    writeByteArrayToFile(source.getBytes(), target);
  }
  
  public static void writeByteArrayToFile(byte[] source, File target) throws IOException {
    writeStreamToStream(new ByteArrayInputStream(source), new FileOutputStream(target));
  }
  
  public static void writeFileToStream(File source, OutputStream out) throws IOException {
    FileInputStream in = null;
    try {
      in = new FileInputStream(source);
      writeStreamToStream(in, out);
    } finally {
      FileUtils.close(in);
    }
  }
  public static void writeStreamToStream(InputStream in, OutputStream out) throws IOException {
    byte[] buff = new byte[1024];
    for (int read = in.read(buff); read > 0; read = in.read(buff)) {
      out.write(buff, 0, read);
    }
  }
  
  public static boolean copyFile(File source, File destination) {
    if (source.isDirectory()) {
      boolean result = true;
      Deque<Pair<File, File>> stack = Helper.newStack();
      stack.push(new Pair<File, File>(source, destination));
      while (!stack.isEmpty()) {
        Pair<File, File> pair = stack.pop();
        for (File file : pair.getFirst().listFiles()) {
          if (file.isDirectory()) {
            stack.push(new Pair<File, File>(file, new File(pair.getSecond(), file.getName())));
          } else {
            result &= copyFileHelper(file, new File(pair.getSecond(), file.getName()));
          }
        }
      }
      return result;
    } else {
      return copyFileHelper(source, destination);
    }
  }
  
  private static boolean copyFileHelper(File source, File destination) {
    FileInputStream in = null;
    FileOutputStream out = null;
    try {
      in = new FileInputStream(source);
      destination.getParentFile().mkdirs();
      out = new FileOutputStream(destination);
      writeStreamToStream(in, out);
      return true;
    } catch (IOException e) {
       logger.log(Level.SEVERE, "Unable to copy file from " + source.getPath() + " to " + destination.getPath(), e);
       return false;
    } finally {
      close(in);
      close(out);
    }
  }
  
  public static String convertToRelativePath(String base, String path) {
    path = path.replace('\\', '/');
    base = base.replace('\\', '/');
    if (base == null) {
      return path.replace(' ', '*');
    } else {
      if (path.startsWith(base)) {
        return path.substring(base.length()).replace(' ', '*');
      } else if (base.endsWith("content")) { 
        return path.substring(path.indexOf("content") + "content".length()).replace(' ', '*');
      } else {
        logger.severe("Unable to convert " + path + " to relative path (" + base + ")");
        return path.replace(' ', '*');
      }
    }
  }
  
  public static String stripFileName(String path) {
    int index = path.lastIndexOf('/');
    if (index == -1) {
      return "";
    } else {
      return path.substring(0, index);
    }
  }

  public static String computeHash(File file) {
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      
      byte[] buff = new byte[1024];
      InputStream is = null; 
      try {
        is = new FileInputStream(file);
        int size;
        while ((size = is.read(buff)) != -1) {
          md5.update(buff, 0, size);
        }
      } finally {
        FileUtils.close(is);
      }
      return new BigInteger(1, md5.digest()).toString(16);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error getting md5 for " + file.getPath(), e);
      return null;
    }
  }
  
  public static Pair<String, String> computeHashes(File file) {
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      MessageDigest sha = MessageDigest.getInstance("SHA");
      
      byte[] buff = new byte[1024];
      InputStream is = null; 
      try {
        is = new FileInputStream(file);
        int size;
        while ((size = is.read(buff)) != -1) {
          md5.update(buff, 0, size);
          sha.update(buff, 0, size);
        }
      } finally {
        FileUtils.close(is);
      }
      return new Pair<String, String>(new BigInteger(1, md5.digest()).toString(16), new BigInteger(1, sha.digest()).toString(16));
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Error getting md5 for " + file.getPath(), e);
      return null;
    }
  }
  
  public static BufferedWriter getBufferedWriter(IOFilePropertyFactory ioFactory) throws IOException {
    return new BufferedWriter(new FileWriter(ioFactory.asOutput().getValue()));
  }
  
//  public static BufferedWriter getBufferedWriter(Property<File> property) throws IOException {
//    return new BufferedWriter(new FileWriter(property.getValue()));
//  }
  
//  public static BufferedReader getBufferedReader(Property<File> property) throws IOException {
//    return new BufferedReader(new FileReader(property.getValue()));
//  }
  
  public static BufferedReader getBufferedReader(IOFilePropertyFactory ioFactory) throws IOException {
    return new BufferedReader(new FileReader(ioFactory.asInput().getValue()));
  }
  
  /**
   * Uses reflection. This will break if a heterogeneous collection is used.
   */
  public static <T extends LineWriteable> void writeLineFile(Iterable<T> iterable, IOFilePropertyFactory ioFactory) throws IOException {
    LineFileWriter writer = null;
    try {
      writer = new LineFileWriter(getBufferedWriter(ioFactory));
      writer.write(iterable);
    } finally {
      close(writer);
    }
  }
  
  public static LineFileWriter getLineFileWriter(IOFilePropertyFactory ioFactory) throws IOException {
    return new LineFileWriter(getBufferedWriter(ioFactory));
  }
  
  public static LineFileWriter getLineFileWriter(File file) throws IOException {
    return new LineFileWriter(new BufferedWriter(new FileWriter(file)));
  }
  
  public static <T extends LineWriteable> Iterable<T> readLineFile(Class<T> klass, IOFilePropertyFactory ioFactory, String ... fields) throws IOException {
    LineFileReader reader = new LineFileReader(getBufferedReader(ioFactory));
    return reader.readNextToIterable(klass, true, fields);
  }
  
  public static LineFileReader getLineFileReader(IOFilePropertyFactory ioFactory) throws IOException {
    return new LineFileReader(getBufferedReader(ioFactory));
  }
  
  public static LineFileReader getLineFileReader(File file) throws IOException {
    return new LineFileReader(new BufferedReader(new FileReader(file)));
  }
}
