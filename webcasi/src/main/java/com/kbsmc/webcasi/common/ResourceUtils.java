package com.kbsmc.webcasi.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
abstract public class ResourceUtils {
	  private ResourceUtils() {}

	  /**
	   * path 에 있는 resource 를 구함
	   */
	  public static final String loadResource(String path) throws IOException {
	    URL url = Thread.currentThread().getContextClassLoader().getResource(path);
	    URLConnection conn = url.openConnection();
	    byte []buf = new byte[conn.getContentLength()];
	    InputStream in = conn.getInputStream();
	    try {
	      int len = in.read(buf);
	      return new String(buf, 0, len, "UTF-8");
	    } finally {
	      in.close();
	    }
	  }
	  
	  
	  private static final int toMonth(String mon) {
	    switch (mon.charAt(0)) {
	    case 'J': 
	      char ch2 = mon.charAt(2);
	      return ch2 == 'n' ? Calendar.JANUARY : (ch2 == 'n' ? Calendar.JUNE : Calendar.JULY);
	    case 'F': return Calendar.FEBRUARY;
	    case 'M': 
	      return mon.charAt(2) == 'r' ? Calendar.MARCH : Calendar.MAY;
	    case 'A': 
	      return mon.charAt(1)=='p' ? Calendar.APRIL : Calendar.AUGUST;
	    case 'S': return Calendar.SEPTEMBER;
	    case 'O': return Calendar.OCTOBER;
	    case 'N': return Calendar.NOVEMBER;
	    case 'D': return Calendar.DECEMBER;
	    }
	    return 0;
	  }
	  
	  private static final int parseInt2(String s, int idx) {
	    char ch0 = s.charAt(idx);
	    char ch1 = s.charAt(idx+1);
	    return (ch0 - '0') * 10 + (ch1 - '0');
	  }
	  private static final int parseInt4(String s, int idx) {
	    char ch0 = s.charAt(idx);
	    char ch1 = s.charAt(idx+1);
	    char ch2 = s.charAt(idx+2);
	    char ch3 = s.charAt(idx+3);
	    return (ch0 - '0') * 1000 + (ch1 - '0') * 100 + (ch2 - '0') * 10 + (ch3 - '0');
	  }
	  
	  /**
	   * rfc2616 으로 format 된 string 을 parsing 함
	   */
	  public static final long parseRfc2616Date(String date) {
	    if (date == null || date.length() != 29) {
	      return -1;
	    }
	    Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.DAY_OF_MONTH, parseInt2(date, 5));
	    cal.set(Calendar.MONTH, toMonth(date.substring(8, 11)));
	    cal.set(Calendar.YEAR, parseInt4(date, 12));
	    cal.set(Calendar.HOUR_OF_DAY, parseInt2(date, 17));
	    cal.set(Calendar.MINUTE, parseInt2(date, 20));
	    cal.set(Calendar.SECOND, parseInt2(date, 23));
	    cal.set(Calendar.ZONE_OFFSET, 0);
	    return cal.getTimeInMillis();
	  }
	}