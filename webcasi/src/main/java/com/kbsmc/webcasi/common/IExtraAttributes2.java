package com.kbsmc.webcasi.common;


/**
 * class에서 추가 지원 할 수 있는 attribute를 attributes field로 넣으면
 * JSONSerializer나 기타 serializer방식에서 이 attribute들을 해당 class의
 * attribute로 promote시킴.
 * @title 추가 attribute 를 array 들로 제공 하는 interface
 */
public interface IExtraAttributes2 {
  /**
   * attribute 이름들을 구함
   * @return attribute 이름들
   */
  String [] getNames();
  
  /**
   * attribute value 들을 구함
   * @return attribute value 들
   */
  Object [] getValues();
}