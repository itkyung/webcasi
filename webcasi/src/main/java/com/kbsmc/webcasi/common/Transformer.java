package com.kbsmc.webcasi.common;

import java.util.List;
import java.util.Map;



/**
 * Transformers are used to alter the values written to a Flexjson stream.
 * This allows you to modify your data for use with HTML, security like stripping
 * out &lt;script&gt; tags, or rendering HTML from simple markups like markdown or other
 * technologies.  Use {@link JSONSerializer#transform} to register a Transformer to with
 * a JSONSerializer.
 * @title JSON serialize 시 data 를 transform 할 수 있는 기능
 */
public interface Transformer {
  /**
   * value 를 특정 방식으로 변환함
   * @param value 변환할 값
   * @param extra 기타 변수들
   * @param path 현재 transformer하고 있는 node의 path
   * @return 변환된 값
   */
  Object transform( Object value, Map<String,Object> extra, List<String> path);
}
