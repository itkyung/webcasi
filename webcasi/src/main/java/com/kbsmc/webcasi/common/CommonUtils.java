package com.kbsmc.webcasi.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.security.Key;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 공통 적인 Utility. 다양한 공통적으로 필요한 것들이 있음. 
 *
 */
abstract public class CommonUtils {
  private static final Log log = LogFactory.getLog(CommonUtils.class);
  
  private static DateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
  /**
   * UTF-8 파일의 prefix
   */
  public static final String UTF_8_PREFIX;
  static {
    String utf8Prefix = null;
    try {
      utf8Prefix = new String(new byte[]{(byte)0xEF,(byte)0xBB,(byte)0xBF}, "UTF-8");
    } catch (UnsupportedEncodingException e) { /* cannot happen */}
    UTF_8_PREFIX = utf8Prefix;
  }

  private static final int BUFFER_SIZE=4096;
  
  /**
   * str 이 숫자인지 여부를 확인함
   * @param str 확인 할 string
   * @return str 이 numer 이면 true 아니면 false
   */  
  public static final boolean isNumber(String str) {
    if (str == null || str.length()==0) return false;
    boolean seenDot = false;
    for (int i = str.charAt(0)=='-'?1:0,len=str.length(); i < len; i++) {
      char ch = str.charAt(i);
      if (!seenDot && ch == '.') {
        seenDot = true;
      } else if (!Character.isDigit(ch)) {
        return false;
      }
    }
    return true;
  }
  
  /**
   * str 을 array 로 comma 구분자를 사용하여 분리함
   * @param str
   * @return str 을 array 로 comma 구분자를 사용하여 분리된 결과
   */
  public static String []stringToArray(String str) {
    String []arr = null;
    if (str != null && str.trim().length() > 0) {
      String[] ss = StringUtils.splitPreserveAllTokens(str, ',');
      arr = new String[ss.length];
      for (int i = 0, len = ss.length; i < len; i++) {
        arr[i] = ss[i].trim();
      }
    }
    return arr;
  }
  
  /**
   * obj 가 empty string 이면 true
   * @param obj 확인할 object
   * @return obj 가 empty string 이면 true
   */
  public static final boolean isEmpty(Object obj) {
    if (obj == null) {
      return true;
    } else if ("".equals(obj)) {
      return true;
    } else if (obj instanceof Object[]) {
      for (Object v : ((Object[])obj)) {
        if (!isEmpty(v)) return false;
      }
      return true;
    } else return false;
  }  


  /**
   * String 체크
   * 백봉기
   * @param str
   * @return booelan
   */
  public static final boolean isValid(String str){
    return !isEmpty(str);    
  }

  /**
   * key 와 value 가 2개 있는 map 을 만듬
   */
  public static <K,V> Map<K,V> tupleMap(K key, V value, K key2, V value2) {
    Map<K,V> m = new HashMap<K, V>(3);
    m.put(key, value);
    m.put(key2, value2);
    return m;
  }
  /**
   * key 와 value 가 n개 있는 map 을 만듬
   */
  @SuppressWarnings("unchecked")
  public static <K,V,W> Map<K,V> nTupleMap(K key, W value, Object ... rest) {
    Map<K,V> m = new HashMap<K, V>(3);
    m.put(key, (V) value);
    for (int i = 0; i < rest.length; i+=2) {
      m.put((K) rest[i], (V) rest[i+1]);
    }
    return m;
  }
  
  /**
   * key 와 value 가 n개 있는 map 을 만듬
   */
  @SuppressWarnings("unchecked")
  public static <K,V> Map<K,V> nTupleMap(Object [] rest) {
    Map<K,V> m = new HashMap<K, V>(3);
    for (int i = 0; i < rest.length; i+=2) {
      m.put((K) rest[i], (V) rest[i+1]);
    }
    return m;
  }


  /**
   * source 에 있는 fieldNames 에 해당되는 field 값들을 target 으로 copy 함.
   * @param source 여기에서 copy 함
   * @param target 여기로 copoy 함
   * @param fieldNames copy 할 field 이름들.
   */
  public static void copyFields(Object source, Object target,
      String fieldNames) {
    copyFields(source, target, fieldNames, false);
  }
  /**
   * Source 의 fieldNames에 정의 된 field들을 target으로 copy함.
   * @param source
   * @param fieldNames comma로 놔눠진 field명들.
   * @param copyToNonNullFieldsOnly target의 field값이 null인 경우만
   *   copy함.
   */
  public static void copyFields(Object source, Object target,
      String fieldNames, boolean copyToNonNullFieldsOnly) {
    if (!isValid(fieldNames)) return;
    if (source == null || target == null) {
      throw new IllegalArgumentException("Null parameter to copyFields method");
    }
    String []names = StringUtils.splitPreserveAllTokens(fieldNames, ',');
    for (int i = 0,len=names.length; i < len; i++) {
      String name = names[i].trim();
      if ("".equals(name)) continue;
      try {
        // 만약 target의 field가 null일때만 copy해야하면 null인지 체크 함.
        if (!copyToNonNullFieldsOnly || PropertyUtils.getProperty(target, name)==null) {
          Object property = PropertyUtils.getProperty(source, name);
          PropertyUtils.setProperty(target, name, property);
        }
      } catch (Exception e) {
        log.warn("Unable to copy property " + name +
            " from " + source.getClass() + " to " + target.getClass() +
            " because " + e, e);
      }
    }
  }
  
  @SuppressWarnings("unused")
  private static final boolean isInArray(String val, String [] arr) {
    for (String v : arr) {
      if (val.equals(v)) {
        return true;
      }
    }
    return false;
  }

  /**
   * in 에서 out 로 모든 data (즉 End of Stream 까지) 를 copy 함
   * @param in copy 할 input stream
   * @param out copy target output stream
   * @param closeOut 다 transfer 하고 out 를 닫을지 여부
   * @throws IOException transfer 하면서 IOException 날 때
   */
  public static void transfer(InputStream in, OutputStream out, boolean closeOut) throws IOException {
    if (in == null || out == null) {
      return;
    }
    try {
      byte []buf = new byte[BUFFER_SIZE];
      int len = 0;
      while ((len=in.read(buf))!=-1) {
        out.write(buf, 0, len);
      }
    } finally {
      in.close();
      if (closeOut) {
        try {
          out.close();
        } catch (Exception ex) {
          // Just ignore. Probably the client just closed the connection
        }
      } else {
        out.flush();
      }
    }
  }
  /**
   * in 에서 out 로 모든 data (즉 End of Stream 까지) 를 copy 함
   * @param in copy 할 input reader
   * @param out copy target writer
   * @param closeOut 다 transfer 하고 out 를 닫을지 여부
   * @throws IOException transfer 하면서 IOException 날 때
   */
  public static void transfer(Reader in, Writer out, boolean closeOut) throws IOException {
    try {
      char []buf = new char[BUFFER_SIZE];
      int len = 0;
      while ((len=in.read(buf))!=-1) {
        out.write(buf, 0, len);
      }
    } finally {
      in.close();
      if (closeOut) {
        out.close();
      } else {
        out.flush();
      }
    }
  }

  /**
   * HttpServletRequest 의 parameterMap 의 value type 은 string array 인데
   * 이것을 그냥 string 으로 (첫 번 째 index 에 해당되는) 바꿈.
   */
  public static Map<String,String> wrapParameters(Map<String, String []> parameterMap) {
    return new ParameterMapWrapper(parameterMap);
  }


  /**
   * fileName 의 extension 을 보고 이에 해당되는 mime type 을 구함
   * @param fileName 확인할 file 명
   * @return fileName 의 extension 을 보고 이에 해당되는 mime type
   */
  public static String findMimeType(String fileName) {
    return new MimetypesFileTypeMap().getContentType(fileName);
  }

  /**
   * <p>Left pad a String with a specified character.</p>
   *
   * <p>Pad to a size of <code>size</code>.</p>
   *
   * <pre>
   * StringUtils.leftPad(null, *, *)     = null
   * StringUtils.leftPad("", 3, 'z')     = "zzz"
   * StringUtils.leftPad("bat", 3, 'z')  = "bat"
   * StringUtils.leftPad("bat", 5, 'z')  = "zzbat"
   * StringUtils.leftPad("bat", 1, 'z')  = "bat"
   * StringUtils.leftPad("bat", -1, 'z') = "bat"
   * </pre>
   *
   * @param str  the String to pad out, may be null
   * @param size  the size to pad to
   * @param padChar  the character to pad with
   * @return left padded String or original String if no padding is necessary,
   *  <code>null</code> if null String input
   * @since 2.0
   */
  public static String leftPad(String str, int size, char padChar){
    return StringUtils.leftPad(str, size, padChar);
  }

  /**
   * <p>Copy property values from the "origin" bean to the "destination" bean
   * for all cases where the property names are the same (even though the
   * actual getter and setter methods might have been customized via
   * <code>BeanInfo</code> classes).  No conversions are performed on the
   * actual property values -- it is assumed that the values retrieved from
   * the origin bean are assignment-compatible with the types expected by
   * the destination bean.</p>
   *
   * <p>If the origin "bean" is actually a <code>Map</code>, it is assumed
   * to contain String-valued <strong>simple</strong> property names as the keys, pointing
   * at the corresponding property values that will be set in the destination
   * bean.<strong>Note</strong> that this method is intended to perform 
   * a "shallow copy" of the properties and so complex properties 
   * (for example, nested ones) will not be copied.</p>
   * 
   * <p>Note, that this method will not copy a List to a List, or an Object[] 
   * to an Object[]. It's specifically for copying JavaBean properties. </p>
   *
   * @param dest Destination bean whose properties are modified
   * @param orig Origin bean whose properties are retrieved
   *
   */
  public static void copyProperties(Object dest, Object orig)throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    PropertyUtils.copyProperties(dest, orig);
  }
  
  @SuppressWarnings({ "unchecked", "unused" })
  private static final int compare(Object o1, Object o2) {
    if (o1 == null) {
      return o2 == null ? 0 : -1;
    }
    if (o2 == null) {
      return 1;
    }
    if (o1 instanceof Comparable<?>) {
      return ((Comparable<Object>) o1).compareTo(o2);
    }
    return 0;
  }
  static final boolean eq(Object o1, Object o2) {
    return o1==o2||(o1!=null&&o1.equals(o2));
  }

  /**
   * Map 두개를 하나의 Map 처럼 보이게 함
   * @author uchung
   */
  final static class DecoratedMap<K,V> implements Map<K,V> {
    private Map<K,V> map1;
    private Map<K,V> map2;

    /**
     * DecoratedMap 객체를 생성함
     */
    public DecoratedMap(Map<K, V> map1, Map<K, V> map2) {
      this.map1 = map1;
      this.map2 = map2;
    }
    /**
     * {@inheritDoc}
     */
    public void clear() {
      map1.clear();
      map2.clear();
    }
    /**
     * {@inheritDoc}
     */
    public boolean containsKey(Object key) {
      return map1.containsKey(key) || map2.containsKey(key); 
    }
    /**
     * {@inheritDoc}
     */
    public boolean containsValue(Object value) {
      return map1.containsValue(value) || map2.containsValue(value); 
    }
    /**
     * {@inheritDoc}
     */
    public Set<java.util.Map.Entry<K, V>> entrySet() {
      // FIXME need to implement
      return map2.entrySet();
    }
    /**
     * {@inheritDoc}
     */
    public V get(Object key) {
      V value = map1.get(key);
      return value==null?map2.get(key):value;
    }
    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
      return map1.isEmpty() && map2.isEmpty();
    }
    /**
     * {@inheritDoc}
     */
    public Set<K> keySet() {
      // FIXME need to implement
      return map2.keySet();
    }
    /**
     * {@inheritDoc}
     */
    public V put(K key, V value) {
      return map1.put(key, value);
    }
    /**
     * {@inheritDoc}
     */
    public void putAll(Map<? extends K, ? extends V> m) {
      map1.putAll(m);
    }
    /**
     * {@inheritDoc}
     */
    public V remove(Object key) {
      return map1.remove(key);
    }
    /**
     * {@inheritDoc}
     */
    public int size() {
      return map1.size() + map2.size();
    }
    /**
     * {@inheritDoc}
     */
    public Collection<V> values() {
      // FIXME need to implement
      return map2.values();
    }
  }
  
  /**
   * Parameter Map 을 wrapping 하는 객체
   * @author uchung
   *
   */
  static class ParameterMapWrapper implements Map<String,String> {
    private Map<String,String []> delegate;

    /**
     * ParameterMapWrapper 객체를 생성함
     */
    public ParameterMapWrapper(Map<String, String []> delegate) {
      this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsKey(Object key) {
      return delegate.containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsValue(Object value) {
      if (value == null) return false;
      return delegate.containsValue(new String[]{value.toString()});
    }

    /**
     * {@inheritDoc}
     */
    public Set<Entry<String, String>> entrySet() {
      return new WrappedSet(delegate.entrySet());
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
      return delegate.equals(o);
    }

    /**
     * {@inheritDoc}
     */
    public String get(Object key) {
      String [] out = delegate.get(key);
      return out==null?null:out[0];
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
      return delegate.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
      return delegate.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> keySet() {
      return delegate.keySet();
    }

    /**
     * {@inheritDoc}
     */
    public String put(String key, String value) {
      return null;
    }

    /**
     * {@inheritDoc}
     */
    public String remove(Object key) {
      return null;
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
      return delegate.size();
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> values() {
      return new WrappedCollection(delegate.values());
    }
    /**
     * {@inheritDoc}
     */
    public void putAll(Map<? extends String, ? extends String> t) {
    }  
    /**
     * {@inheritDoc}
     */
    public String toString() {
      return delegate.toString();
    }
  }
  /**
   * Array 를 Collection 으로 wrapping 한 것
   * @author uchung
   *
   */
  static class WrappedCollection implements Collection<String> {
    private Collection<String[]> collection;
    /**
     * WrappedCollection 객체를 생성함
     */
    public WrappedCollection(Collection<String[]> collection) {
      this.collection = collection;
    }
    /**
     * {@inheritDoc}
     */
    public boolean add(String o) {return false;}
    /**
     * {@inheritDoc}
     */
    public boolean addAll(Collection<? extends String> c) { return false;}
    /**
     * {@inheritDoc}
     */
    public void clear() {}
    /**
     * {@inheritDoc}
     */
    public boolean contains(Object o) {return collection.contains(o);}
    /**
     * {@inheritDoc}
     */
    public boolean containsAll(Collection<?> c) {return collection.containsAll(c);}
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {return collection.equals(o);}
    /**
     * {@inheritDoc}
     */
    public int hashCode() {return collection.hashCode();}
    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {return collection.isEmpty();}
    /**
     * {@inheritDoc}
     */
    public boolean remove(Object o) {return false;}
    /**
     * {@inheritDoc}
     */
    public boolean removeAll(Collection<?> c) {return false;}
    /**
     * {@inheritDoc}
     */
    public boolean retainAll(Collection<?> c) {return false; }
    /**
     * {@inheritDoc}
     */
    public int size() {return collection.size();}
    /**
     * {@inheritDoc}
     */
    public Object[] toArray() {return toArray(new Object[size()]);}
    /**
     * {@inheritDoc}
     */
    public Iterator<String> iterator() {
      final Iterator<String[]> iter = collection.iterator();
      return new Iterator<String>(){
        public boolean hasNext() {return iter.hasNext();}
        public String next() {return iter.next()[0];}
        public void remove() {}
      };
    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
      int i = 0;
      for (String []v : collection) {
        a[i++] = (T) v[0];
      }
      return a;
    }
  }
  
  /**
   * Entry
   * @author uchung
   *
   */
  static class WrappedEntry implements Entry<String,String> {
    private String key;
    private String value;
    /**
     * WrappedEntry 객체를 생성함
     */
    public WrappedEntry(Entry<String,String[]> e) {
      this.key = e.getKey();
      this.value = e.getValue()[0];
    }
    /**
     * {@inheritDoc}
     */
    public String getKey() {return key;}
    /**
     * {@inheritDoc}
     */
    public String getValue() {return value;}
    /**
     * {@inheritDoc}
     */
    public String setValue(String value) {
      return this.value;
    }
    /**
     * {@inheritDoc}
     */
    public String toString() {return key + "=" + value;}
  }
  /**
   * Map 의 entry 들을 담고 있는 set
   * @author uchung
   *
   */
  static class WrappedSet implements Set<Entry<String, String>> {
    private Set<Entry<String, String[]>> set;
    /**
     * WrappedSet 객체를 생성함
     * @param set
     */
    public WrappedSet(Set<Entry<String, String[]>> set) {
      this.set = set;
    }
    /**
     * {@inheritDoc}
     */
    public boolean add(Entry<String, String> o) {return false;}
    /**
     * {@inheritDoc}
     */
    public void clear() {}
    /**
     * {@inheritDoc}
     */
    public boolean contains(Object o) {return set.contains(o);}
    /**
     * {@inheritDoc}
     */
    public boolean containsAll(Collection<?> c) {return set.containsAll(c);}
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {return set.equals(o);}
    /**
     * {@inheritDoc}
     */
    public int hashCode() {return set.hashCode();}
    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {return set.isEmpty();}
    /**
     * {@inheritDoc}
     */
    public boolean remove(Object o) { return false;}
    /**
     * {@inheritDoc}
     */
    public boolean removeAll(Collection<?> c) {return false;}
    /**
     * {@inheritDoc}
     */
    public boolean retainAll(Collection<?> c) { return false;}
    /**
     * {@inheritDoc}
     */
    public int size() {return set.size();}
    /**
     * {@inheritDoc}
     */
    public Object[] toArray() {return toArray(new Object[size()]);}
    /**
     * {@inheritDoc}
     */
    public boolean addAll(Collection<? extends Entry<String, String>> c) {return false;}
    /**
     * {@inheritDoc}
     */
    public Iterator<Entry<String, String>> iterator() {
      final Iterator<Entry<String,String[]>> iter = set.iterator();
      return new Iterator<Entry<String,String>>(){
        public boolean hasNext() {return iter.hasNext();}
        public Entry<String,String> next() {return new WrappedEntry(iter.next());}
        public void remove() {}
      };
    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
      int i = 0;
      for (Entry<String, String[]> v : set) {
        a[i++] = (T) new WrappedEntry(v);
      }
      return a;
    }

  }
  
  private static final Pattern IDENTIFIER = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");
  private static final Pattern IDENTIFIER_WITH_DOT = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_\\.]*");
  /**
   * conditions 에 있는 parameter 들의 key 를 identifier 에 적합한지 여부를 확인 함.
   * @param conditions 확인할 condition 들. 이 것의 key 들을 확인하는 것임.
   * @param allowDots key 에 점 (dot) 가 허용 되는지 여부.
   */
  public static void checkConditions(Map<String, Object> conditions, int numDotsToAllow) {
    if (conditions != null) {
      Pattern pattern = numDotsToAllow > 0 ? IDENTIFIER_WITH_DOT : IDENTIFIER;
      for (String key : conditions.keySet()) {
        if (!pattern.matcher(key).matches()) {
          throw new IllegalArgumentException("Invalid key");
        }
        if (numDotsToAllow > 0 && numDots(key) > numDotsToAllow) {
          throw new IllegalArgumentException("Invalid key");
        }
      }
    }
  }
  
  private static final int numDots(String s) {
    int cnt = 0;
    for (int i = 0, len=s.length(); i < len; i++) {
      if (s.charAt(i) == '.') {
        cnt++;
      }
    }
    return cnt;
  }
  private static final void escapeString(String s, int startIdx, int headEndIdx, StringBuilder buf) {
    for (int i = startIdx; i < headEndIdx; i++) {
      char ch = s.charAt(i);
      switch (ch) {
      case '"': buf.append("\\\""); break;
      case '\r': buf.append("\\r"); break;
      case '\n': buf.append("\\n"); break;
      case '\b': buf.append("\\b"); break;
      case '\\': buf.append("\\\\"); break;
      case '\0': buf.append("\\0"); break;
      default:
        buf.append(ch);
      }
    }
  }
  
  /**
   * s 를 javascript string 으로 변환함
   * @param s javascript string 으로 변환할 string
   * @return s 를 javascript string 으로
   */
  public static String toJavascriptString(String s) {
    if (s == null) {
      return "null";
    }
    StringBuilder buf = new StringBuilder(s.length() + 10);
    buf.append('"');
    escapeString(s, 0, s.length(), buf);
    buf.append('"');
    return buf.toString();
  }
  
  private static final Pattern SCRIPT_PATTERN = Pattern.compile("<script.*?</script>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
  private static final Pattern SCRIPT_PATTERN2 = Pattern.compile("<script.*?<script>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
  private static final Pattern JS_EVENT_PATTERN = Pattern.compile("<\\w+\\s+.*?on\\w+\\s*=.*?>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  private static final Pattern JS_EVENT_ATTR_PATTERN1 = Pattern.compile("on\\w+\\s*=\\s*('.*?'|\".*?\"|[^\\s>]*)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  
  /**
   * html 에서 모든 javascript 를 없앰. 주로 XSS 보안 check 를 위해서 사용하는 것임.
   *  즉 script tag 들과 onXXX event attribute 들을 없앰
   * @param html javascript 없엘 html
   * @return javascript 없는 html
   */
  public static final String removeJavascript(String html) {
	html = html.replaceAll("&lt;", "<");
	html = html.replaceAll("&gt;" ,">");
    StringBuilder buf = new StringBuilder();
    Matcher m = JS_EVENT_PATTERN.matcher(html);
    
    int prevEndIdx = 0;
    while (m.find()) {
      String tag = m.group();
      buf.append(html.substring(prevEndIdx, m.start()));
      buf.append(JS_EVENT_ATTR_PATTERN1.matcher(tag).replaceAll(""));
      prevEndIdx = m.end();
    }
    if (prevEndIdx == 0) {
      html = html.replaceAll("<", "&lt;");
      html = html.replaceAll(">", "&gt;");
      return html;
    }
    buf.append(html.substring(prevEndIdx));
    html = buf.toString();
	html = html.replaceAll("<", "&lt;");
	html = html.replaceAll(">", "&gt;");
	
    return html;
  }
  
  public static final String removeSpace(String str){
	  StringBuilder out = new StringBuilder();
	  if(!isValid(str)) return null;
	  for( int i = 0 ; i < str.length();i++ ){
		  out.append(str.substring(i, i+1).trim());
	  }
	  return out.toString();
  }
  public static final Class<?> toClass(String className) {
		try {
			return Thread.currentThread().getContextClassLoader().loadClass(className);
		} catch (ClassNotFoundException e) {
			log.warn("Class " + className + " not found.");
			return null;
		}
	}
  
  

	private static final String keyValue = "webcasi_1qazxsw2_1qazxsw";	//24바이트 triple des용 키.
	
	public static String md5(String input) throws Exception{
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		byte[] results = messageDigest.digest(input.getBytes());
		
		StringBuffer encoded = new StringBuffer();
		for( int i=0; i < results.length; i++){
			String hex = Integer.toHexString(0xff & results[i]);
			if(hex.length()==1) encoded.append('0');
			encoded.append(hex);
		}
		
		return encoded.toString();
	}
	
	public static String encryptTripleDes(String plain) throws Exception{
		String instance = "DESede/ECB/PKCS5Padding";
	    javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(instance);
	    cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, getKey());
	    String amalgam = plain;

	    byte[] inputBytes1 = amalgam.getBytes("UTF8");
	    byte[] outputBytes1 = cipher.doFinal(inputBytes1);
	    //sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
	    //Base64 base64 = new Base64();
	    
	   // String outputStr1 = encoder.encode(outputBytes1);
	    String outputStr1 = Base64.encodeBase64String(outputBytes1);
	    return outputStr1;
	}
	
	public static String descriptTribleDes(String enc) throws Exception{
	     String instance = "DESede/ECB/PKCS5Padding";
	     javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(instance);
	     cipher.init(javax.crypto.Cipher.DECRYPT_MODE, getKey());
	     //sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
	     //Base64 base64 = new Base64();
	     
	    // byte[] inputBytes1 = decoder.decodeBuffer(enc);
	     byte[] inputBytes1 =  Base64.decodeBase64(enc);
	     byte[] outputBytes2 = cipher.doFinal(inputBytes1);

	     String strResult = new String(outputBytes2, "UTF8");
	     return strResult;
	}
	
	private static Key getKey() throws Exception{
		DESedeKeySpec desKeySpec = new DESedeKeySpec(keyValue.getBytes());
	    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
	    Key key = keyFactory.generateSecret(desKeySpec);
	    return key;
	}
	
	public static String toJson(Object obj){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		return gson.toJson(obj);
	}
	
	public static boolean isSameDay(Date first,Date second){
		String firstStr = fm.format(first);
		String secondStr = fm.format(second);
		
		return firstStr.equals(secondStr);
	}
	
	//first날이 second날보다 difference일 이전(이후)안에 포함되는지 여부.
	public static boolean isInRangeDay(Date first,Date second,int difference,boolean isPre){
		Calendar secondCal = Calendar.getInstance();
		secondCal.setTime(second);
		if(isPre){
			secondCal.add(Calendar.DATE, difference*-1);
			return (first.equals(secondCal.getTime()) || first.after(secondCal.getTime())) ? true : false;
			
		}else{
			
			secondCal.add(Calendar.DATE, difference);
			return (first.equals(secondCal.getTime()) || first.before(secondCal.getTime())) ? true : false;
			
		}
		
		
	}
	
	public static int getAge(String resno){
		if(resno == null) return -1;
		
		String birthYear = "19" + resno.substring(0,2);
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, new Integer(birthYear));
		cal.set(Calendar.MONTH, 1);
		cal.set(Calendar.DATE, 1);
		
		Calendar now = Calendar.getInstance();
		
		int age = now.get(Calendar.YEAR) - cal.get(Calendar.YEAR);
	    if (  (cal.get(Calendar.MONTH) > now.get(Calendar.MONTH))
	            || (    cal.get(Calendar.MONTH) == now.get(Calendar.MONTH) 
	                    && cal.get(Calendar.DAY_OF_MONTH) > now.get(Calendar.DAY_OF_MONTH)   )   
	    ){
	        age--;
	    }
	    return age;
	}
	
	public static boolean isGalaxyNote(HttpServletRequest request){
		String agent = request.getHeader("user-agent");
		return agent.contains("Android") ? true : false;
	}
	
	/**
	 * 특정 날짜가 오늘을 기준으로 day보다 안에 존재하는지 여부.
	 * 예를 들어서 day가 7이면 입력된 date가 오늘부터 7일안에 존재하는지 여부를 확인한다.
	 * @param date
	 * @param day
	 * @return
	 */
	public static boolean isInFromToday(Date date,int day){
		Calendar todayC = Calendar.getInstance();
		
		todayC.add(Calendar.DAY_OF_MONTH, day);
		
		if(date.after(todayC.getTime()))
			return false;
		
		return true;
	}
}