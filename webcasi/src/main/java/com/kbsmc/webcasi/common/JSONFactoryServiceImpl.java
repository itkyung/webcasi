package com.kbsmc.webcasi.common;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Service;

@Service
/**
* Description : Java 객체를 JSON data로 변환해 주는 IJSON 객체를 생성해 주는 factory.
* Author : 강석주
* Date : 2011. 07. 27
*/
public class JSONFactoryServiceImpl implements FactoryBean{
  
  /**
   * JSONFactory 객체.
   *
   */
  public static class JSONFactoryImpl implements IJSONFactory {
    /**
     * JSONFactory 객체를 생성함
     */
    JSONFactoryImpl() {
    }

    /**
     * {@inheritDoc}
     */
    public IJSON create() {
      return create(false);
    }

    /**
     * {@inheritDoc}
     */
    public IJSON create(boolean includeAllMap) {
      return new JSONImpl(includeAllMap);
    }
  }

  public JSONFactoryServiceImpl() {
  }

  /**
   * {@inheritDoc}
   */
  public Object getObject() throws Exception {
    return new JSONFactoryImpl();
  }

  /**
   * {@inheritDoc}
   */
  public Class<?> getObjectType() {
    return IJSONFactory.class;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isSingleton() {
    return true;
  }
}
