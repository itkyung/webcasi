package com.kbsmc.webcasi.common;


/**
* Description : Java 객체를 JSON data로 변환해 주는 IJSON 객체를 생성해 주는 factory.
*/
public interface IJSONFactory {
  /**
   * 새로운 json converter 를 생성함
   * @return 생성된 json converter
   */
  IJSON create();
  /**
   * 새로운 json converter 를 생성함
   * @param includeAllMap 만약 true 이면 Map 의 모든 값들을 포함 시킴.
   * @return 생성된 json converter
   */
  IJSON create(boolean includeAllMap);
}
