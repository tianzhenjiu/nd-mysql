package io.mqttpush;




import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
* Various utility methods for the driver.
*/
public class Util {
  class RandStructcture {
      long maxValue;

      double maxValueDbl;

      long seed1;

      long seed2;
  }

  private static Util enclosingInstance = new Util();

  private static boolean isJdbc4;

  private static boolean isJdbc42;

  private static int jvmVersion = -1;

  private static int jvmUpdateNumber = -1;

  private static boolean isColdFusion = false;

  

  public static boolean isJdbc4() {
      return isJdbc4;
  }

  public static boolean isJdbc42() {
      return isJdbc42;
  }

  public static int getJVMVersion() {
      return jvmVersion;
  }

  public static boolean jvmMeetsMinimum(int version, int updateNumber) {
      return getJVMVersion() > version || getJVMVersion() == version && getJVMUpdateNumber() >= updateNumber;
  }

  public static int getJVMUpdateNumber() {
      return jvmUpdateNumber;
  }

  public static boolean isColdFusion() {
      return isColdFusion;
  }

  /**
   * Checks whether the given server version string is a MySQL Community edition
   */
  public static boolean isCommunityEdition(String serverVersion) {
      return !isEnterpriseEdition(serverVersion);
  }

  /**
   * Checks whether the given server version string is a MySQL Enterprise edition
   */
  public static boolean isEnterpriseEdition(String serverVersion) {
      return serverVersion.contains("enterprise") || serverVersion.contains("commercial") || serverVersion.contains("advanced");
  }

  // Right from Monty's code
  public static String newCrypt(String password, String seed, String encoding) {
      byte b;
      double d;

      if ((password == null) || (password.length() == 0)) {
          return password;
      }

      long[] pw = newHash(seed.getBytes());
      long[] msg = hashPre41Password(password, encoding);
      long max = 0x3fffffffL;
      long seed1 = (pw[0] ^ msg[0]) % max;
      long seed2 = (pw[1] ^ msg[1]) % max;
      char[] chars = new char[seed.length()];

      for (int i = 0; i < seed.length(); i++) {
          seed1 = ((seed1 * 3) + seed2) % max;
          seed2 = (seed1 + seed2 + 33) % max;
          d = (double) seed1 / (double) max;
          b = (byte) java.lang.Math.floor((d * 31) + 64);
          chars[i] = (char) b;
      }

      seed1 = ((seed1 * 3) + seed2) % max;
      seed2 = (seed1 + seed2 + 33) % max;
      d = (double) seed1 / (double) max;
      b = (byte) java.lang.Math.floor(d * 31);

      for (int i = 0; i < seed.length(); i++) {
          chars[i] ^= (char) b;
      }

      return new String(chars);
  }

  public static long[] hashPre41Password(String password, String encoding) {
      // remove white spaces and convert to bytes
      try {
          return newHash(password.replaceAll("\\s", "").getBytes(encoding));
      } catch (UnsupportedEncodingException e) {
          return new long[0];
      }
  }

  public static long[] hashPre41Password(String password) {
      return hashPre41Password(password, Charset.defaultCharset().name());
  }

  static long[] newHash(byte[] password) {
      long nr = 1345345333L;
      long add = 7;
      long nr2 = 0x12345671L;
      long tmp;

      for (byte b : password) {
          tmp = 0xff & b;
          nr ^= ((((nr & 63) + add) * tmp) + (nr << 8));
          nr2 += ((nr2 << 8) ^ nr);
          add += tmp;
      }

      long[] result = new long[2];
      result[0] = nr & 0x7fffffffL;
      result[1] = nr2 & 0x7fffffffL;

      return result;
  }

  public static String oldCrypt(String password, String seed) {
      long hp;
      long hm;
      long s1;
      long s2;
      long max = 0x01FFFFFF;
      double d;
      byte b;

      if ((password == null) || (password.length() == 0)) {
          return password;
      }

      hp = oldHash(seed);
      hm = oldHash(password);

      long nr = hp ^ hm;
      nr %= max;
      s1 = nr;
      s2 = nr / 2;

      char[] chars = new char[seed.length()];

      for (int i = 0; i < seed.length(); i++) {
          s1 = ((s1 * 3) + s2) % max;
          s2 = (s1 + s2 + 33) % max;
          d = (double) s1 / max;
          b = (byte) java.lang.Math.floor((d * 31) + 64);
          chars[i] = (char) b;
      }

      return new String(chars);
  }

  static long oldHash(String password) {
      long nr = 1345345333;
      long nr2 = 7;
      long tmp;

      for (int i = 0; i < password.length(); i++) {
          if ((password.charAt(i) == ' ') || (password.charAt(i) == '\t')) {
              continue;
          }

          tmp = password.charAt(i);
          nr ^= ((((nr & 63) + nr2) * tmp) + (nr << 8));
          nr2 += tmp;
      }

      return nr & ((1L << 31) - 1L);
  }

  private static RandStructcture randomInit(long seed1, long seed2) {
      RandStructcture randStruct = enclosingInstance.new RandStructcture();

      randStruct.maxValue = 0x3FFFFFFFL;
      randStruct.maxValueDbl = randStruct.maxValue;
      randStruct.seed1 = seed1 % randStruct.maxValue;
      randStruct.seed2 = seed2 % randStruct.maxValue;

      return randStruct;
  }

  /**
   * Given a ResultSet and an index into the columns of that ResultSet, read
   * binary data from the column which represents a serialized object, and
   * re-create the object.
   * 
   * @param resultSet
   *            the ResultSet to use.
   * @param index
   *            an index into the ResultSet.
   * @return the object if it can be de-serialized
   * @throws Exception
   *             if an error occurs
   */
  public static Object readObject(java.sql.ResultSet resultSet, int index) throws Exception {
      ObjectInputStream objIn = new ObjectInputStream(resultSet.getBinaryStream(index));
      Object obj = objIn.readObject();
      objIn.close();

      return obj;
  }

  private static double rnd(RandStructcture randStruct) {
      randStruct.seed1 = ((randStruct.seed1 * 3) + randStruct.seed2) % randStruct.maxValue;
      randStruct.seed2 = (randStruct.seed1 + randStruct.seed2 + 33) % randStruct.maxValue;

      return ((randStruct.seed1) / randStruct.maxValueDbl);
  }

  /**
   * @param message
   * @param password
   */
  public static String scramble(String message, String password) {
      long[] hashPass;
      long[] hashMessage;
      byte[] to = new byte[8];
      String val = "";

      message = message.substring(0, 8);

      if ((password != null) && (password.length() > 0)) {
          hashPass = hashPre41Password(password);
          hashMessage = newHash(message.getBytes());

          RandStructcture randStruct = randomInit(hashPass[0] ^ hashMessage[0], hashPass[1] ^ hashMessage[1]);

          int msgPos = 0;
          int msgLength = message.length();
          int toPos = 0;

          while (msgPos++ < msgLength) {
              to[toPos++] = (byte) (Math.floor(rnd(randStruct) * 31) + 64);
          }

          /* Make it harder to break */
          byte extra = (byte) (Math.floor(rnd(randStruct) * 31));

          for (int i = 0; i < to.length; i++) {
              to[i] ^= extra;
          }

          val = new String(to);
      }

      return val;
  }

  

  /**
   * Does a network interface exist locally with the given hostname?
   * 
   * @param hostname
   *            the hostname (or IP address in string form) to check
   * @return true if it exists, false if no, or unable to determine due to VM
   *         version support of java.net.NetworkInterface
   */
  public static boolean interfaceExists(String hostname) {
      try {
          Class<?> networkInterfaceClass = Class.forName("java.net.NetworkInterface");
          return networkInterfaceClass.getMethod("getByName", (Class[]) null).invoke(networkInterfaceClass, new Object[] { hostname }) != null;
      } catch (Throwable t) {
          return false;
      }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static void resultSetToMap(Map mappedValues, java.sql.ResultSet rs) throws SQLException {
      while (rs.next()) {
          mappedValues.put(rs.getObject(1), rs.getObject(2));
      }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static void resultSetToMap(Map mappedValues, java.sql.ResultSet rs, int key, int value) throws SQLException {
      while (rs.next()) {
          mappedValues.put(rs.getObject(key), rs.getObject(value));
      }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static void resultSetToMap(Map mappedValues, java.sql.ResultSet rs, String key, String value) throws SQLException {
      while (rs.next()) {
          mappedValues.put(rs.getObject(key), rs.getObject(value));
      }
  }

  public static Map<Object, Object> calculateDifferences(Map<?, ?> map1, Map<?, ?> map2) {
      Map<Object, Object> diffMap = new HashMap<Object, Object>();

      for (Map.Entry<?, ?> entry : map1.entrySet()) {
          Object key = entry.getKey();

          Number value1 = null;
          Number value2 = null;

          if (entry.getValue() instanceof Number) {

              value1 = (Number) entry.getValue();
              value2 = (Number) map2.get(key);
          } else {
              try {
                  value1 = new Double(entry.getValue().toString());
                  value2 = new Double(map2.get(key).toString());
              } catch (NumberFormatException nfe) {
                  continue;
              }
          }

          if (value1.equals(value2)) {
              continue;
          }

          if (value1 instanceof Byte) {
              diffMap.put(key, Byte.valueOf((byte) (((Byte) value2).byteValue() - ((Byte) value1).byteValue())));
          } else if (value1 instanceof Short) {
              diffMap.put(key, Short.valueOf((short) (((Short) value2).shortValue() - ((Short) value1).shortValue())));
          } else if (value1 instanceof Integer) {
              diffMap.put(key, Integer.valueOf((((Integer) value2).intValue() - ((Integer) value1).intValue())));
          } else if (value1 instanceof Long) {
              diffMap.put(key, Long.valueOf((((Long) value2).longValue() - ((Long) value1).longValue())));
          } else if (value1 instanceof Float) {
              diffMap.put(key, Float.valueOf(((Float) value2).floatValue() - ((Float) value1).floatValue()));
          } else if (value1 instanceof Double) {
              diffMap.put(key, Double.valueOf((((Double) value2).shortValue() - ((Double) value1).shortValue())));
          } else if (value1 instanceof BigDecimal) {
              diffMap.put(key, ((BigDecimal) value2).subtract((BigDecimal) value1));
          } else if (value1 instanceof BigInteger) {
              diffMap.put(key, ((BigInteger) value2).subtract((BigInteger) value1));
          }
      }

      return diffMap;
  }


  /** Cache for the JDBC interfaces already verified */
  private static final ConcurrentMap<Class<?>, Boolean> isJdbcInterfaceCache = new ConcurrentHashMap<Class<?>, Boolean>();

 


  /** Cache for the implemented interfaces searched. */
  private static final ConcurrentMap<Class<?>, Class<?>[]> implementedInterfacesCache = new ConcurrentHashMap<Class<?>, Class<?>[]>();

  /**
   * Retrieves a list with all interfaces implemented by the given class. If possible gets this information from a cache instead of navigating through the
   * object hierarchy. Results are stored in a cache for future reference.
   * 
   * @param clazz
   *            The class from which the interface list will be retrieved.
   * @return
   *         An array with all the interfaces for the given class.
   */
  public static Class<?>[] getImplementedInterfaces(Class<?> clazz) {
      Class<?>[] implementedInterfaces = Util.implementedInterfacesCache.get(clazz);
      if (implementedInterfaces != null) {
          return implementedInterfaces;
      }

      Set<Class<?>> interfaces = new LinkedHashSet<Class<?>>();
      Class<?> superClass = clazz;
      do {
          Collections.addAll(interfaces, (Class<?>[]) superClass.getInterfaces());
      } while ((superClass = superClass.getSuperclass()) != null);

      implementedInterfaces = interfaces.toArray(new Class<?>[interfaces.size()]);
      Class<?>[] oldValue = Util.implementedInterfacesCache.putIfAbsent(clazz, implementedInterfaces);
      if (oldValue != null) {
          implementedInterfaces = oldValue;
      }
      return implementedInterfaces;
  }

  /**
   * Computes the number of seconds elapsed since the given time in milliseconds.
   * 
   * @param timeInMillis
   *            The past instant in milliseconds.
   * @return
   *         The number of seconds, truncated, elapsed since timeInMillis.
   */
  public static long secondsSinceMillis(long timeInMillis) {
      return (System.currentTimeMillis() - timeInMillis) / 1000;
  }

  /**
   * Converts long to int, truncating to maximum/minimum value if needed.
   * 
   * @param longValue
   * @return
   */
  public static int truncateAndConvertToInt(long longValue) {
      return longValue > Integer.MAX_VALUE ? Integer.MAX_VALUE : longValue < Integer.MIN_VALUE ? Integer.MIN_VALUE : (int) longValue;
  }

  /**
   * Converts long[] to int[], truncating to maximum/minimum value if needed.
   * 
   * @param longArray
   * @return
   */
  public static int[] truncateAndConvertToInt(long[] longArray) {
      int[] intArray = new int[longArray.length];

      for (int i = 0; i < longArray.length; i++) {
          intArray[i] = longArray[i] > Integer.MAX_VALUE ? Integer.MAX_VALUE : longArray[i] < Integer.MIN_VALUE ? Integer.MIN_VALUE : (int) longArray[i];
      }
      return intArray;
  }

  /**
   * Returns the package name of the given class.
   * Using clazz.getPackage().getName() is not an alternative because under some class loaders the method getPackage() just returns null.
   * 
   * @param clazz
   *            the Class from which to get the package name
   * @return the package name
   */
  public static String getPackageName(Class<?> clazz) {
      String fqcn = clazz.getName();
      int classNameStartsAt = fqcn.lastIndexOf('.');
      if (classNameStartsAt > 0) {
          return fqcn.substring(0, classNameStartsAt);
      }
      return "";
  }
}