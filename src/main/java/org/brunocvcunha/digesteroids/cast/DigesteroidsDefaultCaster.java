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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Reflection Difference Handling
 * 
 * @author Bruno Candido Volpato da Cunha
 */
public class DigesteroidsDefaultCaster implements DigesteroidsCaster {

  private static Logger log = Logger.getLogger(DigesteroidsDefaultCaster.class);

  /**
   * Constant for Map of String to Object
   */
  public static final Type _obj_map_token = new TypeToken<HashMap<String, Object>>() {
    //no impl
  }.getType();
  
  /**
   * Constant for List of Map from String to Object
   */
  public static final Type _obj_map_list_token = new TypeToken<ArrayList<HashMap<String, Object>>>() {
    //no impl
  }.getType();

  /**
   * The default format for date and time
   */
  public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
  
  /**
   * The default format for date
   */
  public static final String DEFAULT_DATE_ONLY_FORMAT = "yyyy-MM-dd";
  
  /**
   * The default date/time formatter
   */
  public static final DateTimeFormatter DATE_FORMAT_PARSER = DateTimeFormat.forPattern(DEFAULT_DATE_FORMAT).withZoneUTC();

  private static DigesteroidsDefaultCaster instance;
  
  
  private DigesteroidsDefaultCaster() {
  }

  /**
   * @return singleton instance
   */
  public static DigesteroidsDefaultCaster getInstance() {
    if (instance == null) {
      instance = new DigesteroidsDefaultCaster();
    }
    
    return instance;
  }
  
  private static Gson gson = new GsonBuilder().create();
  private static Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();

  @Override
  public String json(Object value) {
    if (value == null) {
      return null;
    }
    
    return gson.toJson(value);
  }
  
  @Override
  public String jsonPretty(Object value) {
    if (value == null) {
      return null;
    }
    
    return gsonPretty.toJson(value);
  }

  @Override
  public String string(Object value) {
    if (value == null) {
      return null;
    }
    
    if (value instanceof String) {
      return (String) value;
    }

    return String.valueOf(value);
  }

  @Override
  public Map<String, Object> map(Object value) {
    if (value instanceof Map) {
      return (Map<String, Object>) value;
    }
    
    String json;
    if (value instanceof String) {
      json = (String) value;
    } else {
      json = gson.toJson(value);
    }

    return gson.fromJson(json, _obj_map_token);
  }

  @Override
  public List<Map<String, Object>> mapList(Object value) {
    String json;
    if (value instanceof String) {
      json = (String) value;
    } else {
      json = gson.toJson(value);
    }

    return gson.fromJson(json, _obj_map_list_token);
  }

  
  @Override
  public Date date(Object value) {
    if (value instanceof GregorianCalendar) {
      return ((GregorianCalendar) value).getTime();
    }
    
    return DATE_FORMAT_PARSER.parseDateTime(String.valueOf(value)).toDate();
  }
  
  @Override
  public <T> T cast(Object value, Type targetType) {
    return cast(value, targetType, null);
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <T> T cast(Object value, Type targetType, String source) {

    if (value == null) {
      return null;
    }
    
    TypeToken<T> typeToken = (TypeToken<T>) TypeToken.get(targetType);
    Class<T> toClazz = (Class<T>) typeToken.getRawType();

    log.debug("Handling value conversion (" + value.getClass() + " / " + value.toString() + ") to "
        + toClazz);

    if (toClazz.isAssignableFrom(String.class)) {
      return (T) string(value);
    }

    if (toClazz.isAssignableFrom(Byte.class)) {
      return (T) Byte.valueOf(value.toString());
    }

    if (toClazz.isAssignableFrom(Short.class)) {
      return (T) Short.valueOf(value.toString());
    }

    if (toClazz.isAssignableFrom(Integer.class)) {
      return (T) Integer.valueOf(Double.valueOf(value.toString()).intValue());
    }

    if (toClazz.isAssignableFrom(Long.class)) {
      return (T) Long.valueOf(Double.valueOf(value.toString()).longValue());
    }

    if (toClazz.isAssignableFrom(Double.class)) {
      return (T) Double.valueOf(value.toString());
    }

    if (toClazz.isAssignableFrom(Date.class)) {
      return (T) date(value);
    }

    
//    throw new IllegalArgumentException("Need to convert value (" + value.getClass() + " / " + value.toString() + ") to " + toClazz);
    
    if (value instanceof String) {
      try {
        return gson.fromJson((String) value, targetType);
      } catch (Exception e) {
        //it's ok
        log.debug("Exception trying to convert string to type", e);
      }
    }

    return gson.fromJson(gson.toJson(value), targetType);
  }

  @Override
  public Element htmlElement(Object value) {
    if (value instanceof Element) {
      return (Element) value;
    }
    
    Document document = Jsoup.parse(string(value));
    return document;
  }
  
}


