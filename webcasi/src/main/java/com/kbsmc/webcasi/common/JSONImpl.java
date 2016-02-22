package com.kbsmc.webcasi.common;

import java.io.IOException;
import java.io.Writer;

/**
 * JavaObject 를 JSON string 으로 serialize 해 주는 기능.
 * @author uchung
 *
 */
public class JSONImpl implements IJSON {
  private JSONSerializer serializer;
  
  public JSONImpl() {
    serializer = new JSONSerializer();
  }
  
  /**
   * 새로운 JSONImpl 객체를 생성함
   * @param language
   * @param includeAllMap
   */
  public JSONImpl(boolean includeAllMap) {
    serializer = new JSONSerializer(includeAllMap);
  }
  
  /**
   * {@inheritDoc}
   */
  public IJSON exclude(String... fields) {
    serializer.exclude(fields);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public IJSON include(String... fields) {
    serializer.include(fields);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public void serialize(String rootName, Object target, Writer writer)
      throws IOException {
    serializer.serialize(rootName, target, writer);
  }

  /**
   * {@inheritDoc}
   */
  public void serialize(Object target, Writer writer) throws IOException {
    serializer.serialize(target, writer);
    
  }

  /**
   * {@inheritDoc}
   */
  public String serialize(String rootName, Object target) {
    return serializer.serialize(rootName, target);
  }

  /**
   * {@inheritDoc}
   */
  public String serialize(Object target) {
    return serializer.serialize(target);
  }

  /**
   * {@inheritDoc}
   */
  public IJSON transform(Transformer transformer, String... fields) {
    serializer.transform(transformer, fields);
    return this;
  }
}
