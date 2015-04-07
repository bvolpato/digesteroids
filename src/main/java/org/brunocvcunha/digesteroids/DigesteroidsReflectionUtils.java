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
package org.brunocvcunha.digesteroids;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.brunocvcunha.digesteroids.cast.DigesteroidsCaster;
import org.brunocvcunha.digesteroids.cast.DigesteroidsDefaultCaster;

/**
 * Reflection Utilities.
 * 
 * @author Bruno Candido Volpato da Cunha
 */
public class DigesteroidsReflectionUtils {

  private static Logger log = Logger.getLogger(DigesteroidsReflectionUtils.class);

  private static DigesteroidsCaster caster = new DigesteroidsDefaultCaster();
  
  /**
   * Builds a instance of the class for a map containing the values, without specifying the handler for differences
   * 
   * @param clazz The class to build instance
   * @param values The values map
   * @return The instance
   * @throws InstantiationException Error instantiating
   * @throws IllegalAccessException Access error
   * @throws IntrospectionException Introspection error
   * @throws IllegalArgumentException Argument invalid
   * @throws InvocationTargetException Invalid target
   */
  public static <T> T buildInstanceForMap(Class<T> clazz, Map<String, Object> values)
      throws InstantiationException, IllegalAccessException, IntrospectionException,
      IllegalArgumentException, InvocationTargetException {
    return buildInstanceForMap(clazz, values, new DigesteroidsDefaultCaster());
  }


  /**
   * Builds a instance of the class for a map containing the values
   * 
   * @param clazz Class to build
   * @param values Values map
   * @param differenceHandler The difference handler
   * @return The created instance
   * @throws InstantiationException Error instantiating
   * @throws IllegalAccessException Access error
   * @throws IntrospectionException Introspection error
   * @throws IllegalArgumentException Argument invalid
   * @throws InvocationTargetException Invalid target
   */
  public static <T> T buildInstanceForMap(Class<T> clazz, Map<String, Object> values, DigesteroidsCaster differenceHandler)
      throws InstantiationException, IllegalAccessException, IntrospectionException,
      IllegalArgumentException, InvocationTargetException {

    log.debug("Building new instance of Class " + clazz.getName());

    T instance = clazz.newInstance();

    for (String key : values.keySet()) {
      Object value = values.get(key);

      if (value == null) {
        log.debug("Value for field " + key + " is null, so ignoring it...");
        continue;
      }
      
      log.debug(
          "Invoke setter for " + key + " (" + value.getClass() + " / " + value.toString() + ")");
      Method setter = null;
      try {
        setter = new PropertyDescriptor(key.replace('.', '_'), clazz).getWriteMethod();
      } catch (Exception e) {
        throw new IllegalArgumentException("Setter for field " + key + " was not found", e);
      }

      Class<?> argumentType = setter.getParameterTypes()[0];

      if (argumentType.isAssignableFrom(value.getClass())) {
        setter.invoke(instance, value);
      } else {

        Object newValue = differenceHandler.cast(value, setter.getParameterTypes()[0]);
        setter.invoke(instance, newValue);

      }
    }

    return instance;
  }


  /**
   * Get the closest annotation for a method (inherit from class)
   * 
   * @param method method
   * @param typeOfT type of annotation inspected
   * @return annotation instance
   */
  public static <T extends Annotation> T getClosestAnnotation(Method method, Class<T> typeOfT) {
    T annotation = method.getAnnotation(typeOfT);
    if (annotation == null) {

      Class<?> clazzToIntrospect = method.getDeclaringClass();
      while (annotation == null && clazzToIntrospect != null) {
        annotation = clazzToIntrospect.getAnnotation(typeOfT);
        clazzToIntrospect = clazzToIntrospect.getSuperclass();
      }
    }

    return annotation;
  }
  
  /**
   * @param dataMap
   * @param fieldName
   * @return
   */
  public static Object getRecursive(Map<String, Object> dataMap, String fieldName) {
     List<Object> values = getAllRecursive(dataMap, fieldName);
     
     if (values == null || values.isEmpty()) {
         return null;
     }
     
     return values.get(0);
  }
  
  /**
   * @param dataMap
   * @param fieldName
   * @return Object array
   */
  public static List<Object> getAllRecursive(Map<String, Object> dataMap, String fieldName) {
      List<Object> values = Arrays.asList((Object) dataMap);
      
      List<Object> nextLevels = new ArrayList<Object>();
      String[] fieldStructure = fieldName.split("\\.");
      for (String fieldLevel : fieldStructure) {
          for (Object value : values) {
              
              List<Object> extractedValues = extractDeepestValues(value, fieldLevel);
              
              for (Object extracted : extractedValues) {
                  if (extracted != null) {
                      nextLevels.add(extracted);
                  }
              }
              value = extractDeepestValue(value, fieldLevel);
              
          }
          
          values = nextLevels;
          nextLevels = new ArrayList<Object>();
          
      }

      return values;
  }
  
  /**
   * @param value
   * @param fieldLevel
   * @return
   */
  public static List<Object> extractDeepestValues(Object value, String fieldLevel) {
      
      List<Map<String, Object>> levelMaps = new ArrayList<Map<String, Object>>();
      if (value instanceof Map) {
          levelMaps.add((Map<String, Object>) value);
      } else if (value instanceof Collection) {
          Collection<?> col = (Collection<?>) value;
          if (col.size() > 0) {
              for (Object element : col) {
                  levelMaps.add(caster.map(element));
              }
          }
      } else {

          String jsonVal = caster.json(value);
          if (jsonVal.startsWith("[")) {
              List<Map<String, Object>> col = caster.mapList(jsonVal);
              if (col.size() > 0) {
                  for (Map<String, Object> element : col) {
                      levelMaps.add(element);
                  }
              }
          } else {
              levelMaps.add(caster.map(jsonVal));
          }
      }

      if (levelMaps == null || levelMaps.isEmpty()) {
          return null;
      }

      List<Object> values = new ArrayList<Object>();
      for (Map<String, Object> levelMap : levelMaps) {
          if (levelMap.containsKey(fieldLevel)) {
              values.add(levelMap.get(fieldLevel));
          }
      }
      
      return values;
  }
  
  /**
   * @param value
   * @param fieldLevel
   * @return
   */
  public static Object extractDeepestValue(Object value, String fieldLevel) {
      Map<String, Object> levelMap = null;
      if (value instanceof Map) {
          levelMap = (Map<String, Object>) value;
      } else if (value instanceof Collection) {
          Collection<?> col = (Collection<?>) value;
          if (col.size() > 0) {
              levelMap = caster.map(col.iterator().next());
          }
      } else {

          String jsonVal = caster.json(value);
          if (jsonVal.startsWith("[")) {
              List<Map<String, Object>> col = caster.mapList(jsonVal);
              if (col.size() > 0) {
                  levelMap = col.get(0);
              }
          } else {
              levelMap = caster.map(jsonVal);
          }
      }

      if (levelMap == null || !levelMap.containsKey(fieldLevel)) {
          return null;
      }

      return levelMap.get(fieldLevel);
  }
  
  
}


