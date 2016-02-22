package com.kbsmc.webcasi.common;

import java.io.IOException;

/**
 * Object visitor
 *
 */
interface IObjectVisitor {
  /**
   * obj 를 visit 함
   */
  IObjectVisitor add(Object obj, boolean forceInclude) throws IOException;
  /**
   * v 를 visit 함
   */
  IObjectVisitor add(int v) throws IOException;
  /**
   * b 를 visit 함
   */
  IObjectVisitor add(boolean b) throws IOException;
  /**
   * raw string 인 str 을 visit 함
   */
  IObjectVisitor raw(String str) throws IOException;
  /**
   * fieldName 이 추가 되는지 여부
   */
  boolean isIncluded( String fieldName);
}