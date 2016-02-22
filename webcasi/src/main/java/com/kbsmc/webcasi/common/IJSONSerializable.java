package com.kbsmc.webcasi.common;

import java.io.IOException;


/**
 * JSONSerializer 에서 객체가 별도의 serialization 방식을 지원할 때 구현함
 */
public interface IJSONSerializable {
  /**
   * serialize 함
   */
  void serialize(IObjectVisitor visitor) throws IOException ;
}
