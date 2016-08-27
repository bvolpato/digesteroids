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

import com.google.gson.reflect.TypeToken;
import com.jayway.jsonpath.JsonPath;

import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.brunocvcunha.digesteroids.annotation.DigesterEntity;
import org.brunocvcunha.digesteroids.annotation.DigesterMapping;
import org.brunocvcunha.digesteroids.cast.DigesteroidsCaster;
import org.brunocvcunha.digesteroids.cast.DigesteroidsDefaultCaster;
import org.brunocvcunha.inutils4j.MyStreamUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * Crawler Utils
 * 
 * @author brunovolpato
 *
 */
public class Digesteroids {

  private static Logger log = Logger.getLogger(Digesteroids.class);

  private DigesteroidsCaster caster;


  /**
   * Non-Args Constructor for the Digesteroids
   */
  public Digesteroids() {
    this.caster = new DigesteroidsDefaultCaster();
  }

  /**
   * @param caster The instance of Caster to use
   */
  public Digesteroids(DigesteroidsCaster caster) {
    super();
    this.caster = caster;
  }


  /**
   * Convert given stream with HTML to targetType, using the mappings from the default source.
   * @param source Source name
   * @param is Stream with the HTML
   * @param targetType Target type
   * @return Converted object
   * @throws InstantiationException Can not create instance of target object
   * @throws IllegalAccessException Access violation
   * @param <T> Type to return
   */
  public <T> T convertHTMLToType(String source, InputStream is, Type targetType)
      throws InstantiationException, IllegalAccessException {
    return convertObjectToType(source, MyStreamUtils.readContent(is), targetType);
    
  }

  /**
   * Convert given original object to targetType, using the mappings from the default source.
   * @param original Original data
   * @param targetType Target type
   * @return Converted object
   * @throws InstantiationException Can not create instance of target object
   * @throws IllegalAccessException Access violation
   * @param <T> Type to return
   */
  public <T> T convertObjectToType(Object original, Type targetType)
      throws InstantiationException, IllegalAccessException {
    return convertObjectToType("", original, targetType);
    
  }
    
  /**
   * Convert given original object to targetType, using the mappings from the source parameter.
   * @param source Source name
   * @param original Original data
   * @param targetType Target type
   * @return Converted object
   * @throws InstantiationException Can not create instance of target object
   * @throws IllegalAccessException Access violation
   * @param <T> Type to return
   */
  public <T> T convertObjectToType(String source, Object original, Type targetType)
      throws InstantiationException, IllegalAccessException {

    if (original == null) {
      return null;
    }

    TypeToken<T> typeToken = (TypeToken<T>) TypeToken.get(targetType);

    Class<T> targetClass = (Class<T>) typeToken.getRawType();
    T target = targetClass.newInstance();

    // at this point, we starting doing comparisons
    for (Field entryField : targetClass.getDeclaredFields()) {
      entryField.setAccessible(true);

      DigesterMapping reference = null;

      try {
        DigesterMapping[] references = entryField.getAnnotationsByType(DigesterMapping.class);

        for (DigesterMapping candidate : references) {
          if (candidate.source().equalsIgnoreCase(source)) {
            reference = candidate;
            break;
          }
        }
      } catch (Exception e) {
        // it's ok
      }

      if (reference != null) {
        log.info("Found reference, using field: " + reference.value() + ". Field name: "
            + entryField.getName() + ", Base Class: " + targetClass + ", object: "
            + original.getClass());

        PropertyDescriptor descriptor;
        Method writerMethod;
        Object value;

        try {
          descriptor = new PropertyDescriptor(entryField.getName(), targetClass);

          writerMethod = descriptor.getWriteMethod();

        } catch (Exception e) {
          e.printStackTrace();
          log.warn("Exception converting record: ", e);
          continue;
        }

        Type valueType = writerMethod.getGenericParameterTypes()[0];

        log.info("Reference for field " + entryField.getName() + " - " + reference);
        Object resolvedValue =
            resolveValue(original, reference, valueType);

        if (resolvedValue != null) {
          try {
            invokeSetter(target, writerMethod, resolvedValue);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }

      }
    }

    return target;
  }

  /**
   * @param originalData Original data to resolve
   * @param reference Reference annotation
   * @param valueType Value to return
   * @return Value resolved based on the annotation
   * @throws InstantiationException Can not create instance of target object
   * @throws IllegalAccessException Access violation
   */
  public Object resolveValue(Object originalData, DigesterMapping reference, Type valueType) throws InstantiationException, IllegalAccessException {

    TypeToken<?> typeToken = (TypeToken<?>) TypeToken.get(valueType);

    Class<?> targetClass = (Class<?>) typeToken.getRawType();

    Object resolvedValue = null;

    if (reference.refType() == ReferenceTypeEnum.NORMAL) {
      resolvedValue = resolveValueNormal(reference.source(), originalData, reference.value(), valueType, targetClass);
    } else if (reference.refType() == ReferenceTypeEnum.PASS_THROUGH) {
      resolvedValue = resolveValuePassthrough(reference.source(), originalData, valueType, targetClass);
    } else if (reference.refType() == ReferenceTypeEnum.JSON_PATH) {
      resolvedValue = resolveValueJsonPath(originalData, reference.value());
    } else if (reference.refType() == ReferenceTypeEnum.HTML_ID) {
      resolvedValue = resolveValueHTMLId(originalData, reference.value(), reference.htmlText());
    } else if (reference.refType() == ReferenceTypeEnum.HTML_CSS) {
      resolvedValue = resolveValueHTMLCss(originalData, reference.value(), reference.htmlText());
    } else if (reference.refType() == ReferenceTypeEnum.HTML_XPATH) {
      resolvedValue = resolveValueHTMLXPath(originalData, reference.value(), reference.htmlText());
    } else if (reference.refType() == ReferenceTypeEnum.HARDCODE) {
      resolvedValue = reference.value();
    }

    // make sure that it's the type
    return caster.cast(resolvedValue, valueType);

  }

  /**
   * @param source Source to consider the mappings
   * @param originalData Original data
   * @param valueType The type of the value 
   * @param targetClass The target class to consider
   * @return Resolved value
   * @throws InstantiationException Failed to create instance
   * @throws IllegalAccessException Access violation
   */
  protected Object resolveValuePassthrough(String source, Object originalData, Type valueType,
      Class<?> targetClass) throws InstantiationException, IllegalAccessException {
    Object resolvedValue;

    if (Collection.class.isAssignableFrom(targetClass)) {

      log.info("Annotated: " + valueType);

      List<Object> array = new ArrayList<>();

      Object resolvedElement;
      if (valueType instanceof ParameterizedType) {
        resolvedElement = convertObjectToType(source, originalData,
            ((ParameterizedType) valueType).getActualTypeArguments()[0]);
      } else {
        resolvedElement = convertObjectToType(source, originalData, valueType);
      }
      array.add(resolvedElement);

      resolvedValue = array;


    } else {
      
      resolvedValue = convertObjectToType(source, originalData, valueType);
    }
    return resolvedValue;
  }

  /**
   * @param originalData Original data with the HTML
   * @param id The ID to look for
   * @param htmlText If should return HTML text or the whole object
   * @return HTML Information
   */
  protected Object resolveValueHTMLId(Object originalData, String id, boolean htmlText) {
    Element targetElement = caster.htmlElement(originalData);
    
    Element elementById = targetElement.getElementById(id);
    if (htmlText) {
      return elementById.text();
    }

    return elementById;
  }

  /**
   * @param originalData Original data with the HTML
   * @param refValue The CSS selector to look for
   * @param htmlText If should return HTML text or the whole object
   * @return HTML Information
   */
  protected Object resolveValueHTMLCss(Object originalData, String refValue, boolean htmlText) {
    Element targetElement = caster.htmlElement(originalData);
    Elements elements = targetElement.select(refValue);
    
    if (htmlText) {
      return elements.text();
    }
    
    return elements;
  }

  /**
   * @param originalData Original data with the HTML
   * @param refValue The XPath selector to look for
   * @param htmlText If should return HTML text or the whole object
   * @return HTML Information
   */
  protected Object resolveValueHTMLXPath(Object originalData, String refValue, boolean htmlText) {
    Element targetElement = caster.htmlElement(originalData);
    Elements elements = targetElement.select(refValue);
    
    if (htmlText) {
      return elements.text();
    }
    
    return elements;
  }

  /**
   * @param originalData The JSON data
   * @param path The JsonPath to return
   * @return Json Path information
   */
  protected Object resolveValueJsonPath(Object originalData, String path) {
    Map<String, Object> targetMap = caster.map(originalData);
    return JsonPath.read(caster.json(targetMap), path);
  }

  /**
   * @param source Source to get the annotations
   * @param originalData Original data to resolve
   * @param refValue Where the data is
   * @param valueType The value type
   * @param targetClass The target class
   * @return Resolved value
   * @throws InstantiationException Can not create instance of target object
   * @throws IllegalAccessException Access violation
   */
  protected Object resolveValueNormal(String source, Object originalData, String refValue,
      Type valueType, Class<?> targetClass) throws InstantiationException, IllegalAccessException {
    Map<String, Object> targetMap = caster.map(originalData);
    Object resolvedValue = DigesteroidsReflectionUtils.getRecursive(targetMap, refValue);

    if (targetClass.getAnnotation(DigesterEntity.class) != null) {
      resolvedValue = convertObjectToType(source, resolvedValue, valueType);
    }
    return resolvedValue;
  }

  /**
   * @param target Target to set
   * @param setter Setter method
   * @param resolvedValue Value to set
   * @throws IllegalArgumentException Invalid arguments sent
   * @throws InvocationTargetException Access violation in the method
   * @throws IllegalAccessException Access violation
   */
  public void invokeSetter(Object target, Method setter, Object resolvedValue)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Class<?> valueType = setter.getParameterTypes()[0];

    // try {
    // writerMethod.invoke(target, resolvedValue);
    // } catch (IllegalAccessException | IllegalArgumentException |
    // InvocationTargetException e) {
    // e.printStackTrace();
    // }

    if (valueType.isAssignableFrom(resolvedValue.getClass())) {
      // if the value and the setter are compatible, so just
      // invoke it to set the new value

      setter.invoke(target, resolvedValue);

    } else {
      // if the value cannot be used for the setter argument (e.g.
      // String to a Date)
      // so we need to convert types. sometimes it happens because
      // the lack of support
      // of some types in JSON
      if (log.isDebugEnabled()) {
        log.debug("Need to convert " + resolvedValue.getClass() + " to " + valueType);
      }

      try {

        // cast to the type, and call the setter
        setter.invoke(target, caster.cast(resolvedValue, valueType));

      } catch (Exception e) {
        log.warn("Exception occurred while trying to convert data", e);
      }

    }
  }

  /**
   * @return the caster
   */
  public DigesteroidsCaster getCaster() {
    return caster;
  }

  /**
   * @param caster the caster to set
   */
  public void setCaster(DigesteroidsCaster caster) {
    this.caster = caster;
  }
  
  
}
