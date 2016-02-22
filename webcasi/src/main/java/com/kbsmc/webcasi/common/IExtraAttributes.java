package com.kbsmc.webcasi.common;

import java.util.Map;

/**
 * class에서 추가 지원 할 수 있는 attribute를 attributes field로 넣으면
 * JSONSerializer나 기타 serializer방식에서 이 attribute들을 해당 class의
 * attribute로 promote시킴.
 * @title 추가 attribute 를 Map으로 제공 하는 interface
 */
public interface IExtraAttributes {
  
  /**
   * 추가될 attribute들
   * @return 추가될 attribute들
   */
  public Map<String, ? extends Object> getAttributes();
}
