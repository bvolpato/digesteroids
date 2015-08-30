/**
 * Copyright (C) 2015 Bruno Candido Volpato da Cunha (brunocvcunha@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.brunocvcunha.digesteroids.cast;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Element;

/**
 * Reflection Difference Handling Interface
 * 
 * @author Bruno Candido Volpato da Cunha
 */
public interface DigesteroidsCaster {

  /**
   * Cast value to the given type
   * @param value Value to convert
   * @param typeOfT Type to convert
   * @return Return data
   */
  public <T> T cast(Object value, Type typeOfT);
  public <T> T cast(Object value, Type targetType, String source);

  /**
   * Convert value to a list of map
   * @param value Value
   * @return List
   */
  List<Map<String, Object>> mapList(Object value);
  
  /**
   * Convert value to a map
   * @param value Value
   * @return Map
   */
  Map<String, Object> map(Object value);
  
  /**
   * Convert a value to JSON
   * @param value Value
   * @return Json
   */
  String json(Object value);

  /**
   * Convert a value to String
   * @param value String
   * @return String
   */
  String string(Object value);

  /**
   * Convert a value to Date
   * @param value Value
   * @return Date
   */
  Date date(Object value);

  /**
   * Convert a value to HTML Element
   * @param value Value
   * @return Date
   */
  Element htmlElement(Object value);



}


