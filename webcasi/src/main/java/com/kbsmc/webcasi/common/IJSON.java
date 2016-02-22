package com.kbsmc.webcasi.common;

import java.io.IOException;
import java.io.Writer;

/**
 * Java 객체를 JSON String 으로 변환해(Serialize) 주는 기능 interface
 *
 */
public interface IJSON {
  /**
   * Serialize 할 때 fields 들을 포함 시키기
   * @param fields 포함 시킬 field들. root 에서 부터의 dot notation 으로 해도 되고.
   *   *.fieldName 같이 하면 모든 객체에 해당됨
   * @return fields 들을  포함 시켜 serialize 하는 IJSON object
   */
  IJSON include( String... fields );
  
  /**
   * Serialize 할 때 fields 들을 제외 시키기
   * @param fields 제외 시킬
   * @return fields 들을 제외 시켜 serialize 하는 IJSON object
   */
  IJSON exclude( String... fields );
  /**
   * Java 객체를 JSON 으로 serialize 하여 writer 로 보냄
   * @param rootName field 명의 root.
   * @param target Serialize 할 java 객체
   * @param writer 여기로 serialize 된 data 를 보냄
   * @throws IOException
   */
  void serialize(String rootName, Object target, Writer writer) throws IOException;
  /**
   * Java 객체를 JSON 으로 serialize 하여 writer 로 보냄
   * @param target Serialize 할 java 객체
   * @param writer 여기로 serialize 된 data 를 보냄
   * @throws IOException
   */
  void serialize(Object target, Writer writer) throws IOException;
  /**
   * Java 객체를 JSON 으로 serialize 하여 String 으로 return 함
   * @param rootName field 명의 root.
   * @param target Serialize 할 java 객체
   * @return Serialize 된 JSON string
   */
  String serialize(String rootName, Object target);
  /**
   * Java 객체를 JSON 으로 serialize 하여 String 으로 return 함
   * @param target Serialize 할 java 객체
   * @return Serialize 된 JSON string
   */
  String serialize(Object target);
  /**
   * Serialize 시 사용할 Transformer를 등록하여 사용 할 수 있는 serializer 구함
   * @param transformer 사용할 Transformer
   * @param fields 이 Transformer 를 적용할 field 목록
   * @return Serialize 시 사용할 Transformer 들을 등록하여 사용 할 수 있는 serializer
   */
  IJSON transform( Transformer transformer, String... fields );
}