package com.kbsmc.webcasi.common;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * Introspection 관련 Utility
 *
 */
abstract public class IntrospectionUtils {
  private IntrospectionUtils() {}
  
  /**
   * object 에 실제 class를 구함 (proxy 면 상위 class)
   */
  public static Class<?> findBeanClass(Object object) {
    Class<?> cls = object.getClass();
    if (Object.class.equals(cls.getSuperclass())) {
      return cls;
    }
    if (cls.getName().indexOf("$$")!=-1) {
      // It's an enhanced class. Get the super class
      return cls.getSuperclass();
    }
    Class<?>[] classes = cls.getInterfaces();
    for( Class<?> clazz : classes ) {
      String name = clazz.getName();
      if( name.equals("org.hibernate.proxy.HibernateProxy") ) {
        return cls.getSuperclass();
      }
    }
    return cls;
  }
  
  /**
   * method 명에 해당되는 field 이름을 구함 (getter 인 경우에만)
   */
  public static String findPropertyName(String methodName) {
    int startIdx = -1;
    int methodNameLen = methodName.length();
    if (methodNameLen > 3 && methodName.startsWith("get")) {
      startIdx = 3;
    } else if (methodNameLen > 2 && methodName.startsWith("is")) {
      startIdx = 2;
    } else {
      return null;
    }
    if (methodNameLen > startIdx + 1 && Character.isUpperCase(methodName.charAt(startIdx+1))) {
      return methodName.substring(startIdx);
    }
    return Character.toLowerCase(methodName.charAt(startIdx)) + methodName.substring(startIdx + 1);
  }

  /**
   * accessors 를 추가 함
   */
  public static void addAccessors(Map<String, Method> accessors, Method[]methods) {
    for (Method m : methods) {
      String methodName = m.getName();
      Class<?> declaringClass = m.getDeclaringClass();
      if (Object.class.equals(declaringClass)) {
        continue;
      }
      if (Modifier.isPublic(m.getModifiers()) &&
          m.getParameterTypes().length == 0 &&
          !void.class.equals(m.getReturnType())) {
        String propName = IntrospectionUtils.findPropertyName(methodName);
        if (propName != null) {
          Method oldAccessor = accessors.get(propName);
          if (oldAccessor == null ||
              oldAccessor.getDeclaringClass().isAssignableFrom(declaringClass)) {
            accessors.put(propName, m);
          }
        }
      }
    }
  }

  /**
   * accessors 를 추가함
   */
  public static Map<String,Method> findAccessors(Class<?> cls) {
    String className = cls.getName();
    Map<String,Method> accessors = new HashMap<String, Method>();
    
    if (Object.class.equals(cls.getSuperclass()) && className.indexOf("$$") != -1) {
      for (Class<?> intf : cls.getInterfaces()) {
        String intfName = intf.getName();
        if (intfName.equals("org.hibernate.proxy.HibernateProxy") ||
            intfName.equals("javassist.util.proxy.ProxyObject") ||
            intfName.startsWith("org.springframework.aop.")) {
          continue;
        }
        IntrospectionUtils.addAccessors(accessors, intf.getMethods());
      }
    } else {
      IntrospectionUtils.addAccessors(accessors, cls.getMethods());
    }
    return accessors;
  }

}
