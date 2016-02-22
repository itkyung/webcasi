package com.kbsmc.webcasi.common;

import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 * JSON 관련 Utility
 * @author uchung
 *
 */
abstract public class JSONUtils {
  /**
   * hex characters
   */
  public final static char[] HEX = "0123456789ABCDEF".toCharArray();

  /**
   * str 을 json string 으로 변환
   */
  public static String toJSONString(String str) {
    return toJSONString(new StringBuilder(str.length()+5), str).toString();
  }
  /**
   * str 을 json string 으로 변환
   */
  public static StringBuilder toJSONString(StringBuilder b, String str) {
    try {
      toJSONString((Appendable)b, str);
    } catch (IOException e) {
      // Can never happen
    }
    return b;
  }
  /**
   * str 을 json string 으로 변환
   */
  public static void toJSONString(Appendable a, String str) throws IOException {
    a.append('"');
    CharacterIterator it = new StringCharacterIterator( str );
    for (char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
      if (c == '"') a.append("\\\"");
      else if (c == '\\') a.append("\\\\");
      // else if (c == '/') add("\\/");
      else if (c == '\b') a.append("\\b");
      else if (c == '\f') a.append("\\f");
      else if (c == '\n') a.append("\\n");
      else if (c == '\r') a.append("\\r");
      else if (c == '\t') a.append("\\t");
      else if (Character.isISOControl(c)) {
        unicode(a, c);
      } else {
        a.append(c);
      }
    }
    a.append('"');
  }
  private static void unicode(Appendable a, char c) throws IOException {
    a.append("\\u");
    int n = c;
    for (int i = 0; i < 4; ++i) {
      int digit = (n & 0xf000) >> 12;
    a.append(HEX[digit]);
    n <<= 4;
    }
  }  
}