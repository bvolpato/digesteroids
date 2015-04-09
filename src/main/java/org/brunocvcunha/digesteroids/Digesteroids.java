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
     * @param caster
     */
    public Digesteroids(DigesteroidsCaster caster) {
      super();
      this.caster = caster;
    }


    /**
     * @param original
     * @param baseClass
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public <T> T convertObjectToType(String source, Object original, Type targetType)
            throws InstantiationException, IllegalAccessException {
        TypeToken<T> typeToken = (TypeToken<T>) TypeToken.get(targetType);
        
        Class<T> targetClass = (Class<T>) typeToken.getRawType();
        T target = targetClass.newInstance();

        Map<String, Object> originalMap = caster.map(original);

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
                System.out.println("Found reference, using field: " + reference.value() + ". Field name: "
                        + entryField.getName() + ", Base Class: " + targetClass + ", object: " + original.getClass());

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

                // if (Collection.class.isAssignableFrom(valueType)) {
                //
                // Collection<?> collectionValue = (Collection<?>) value;
                //
                // if (!collectionValue.isEmpty()) {
                // Class<?> firstObjectClass =
                // collectionValue.iterator().next().getClass();
                //
                // List<Object> goldenCollection = new ArrayList<>();
                //
                // for (Object collectionElement : collectionValue) {
                // if (firstObjectClass.isPrimitive()
                // || firstObjectClass == String.class) {
                // goldenCollection.add(collectionElement);
                // } else {
                // goldenCollection.add(convertObjectToTargetMap(source,
                // collectionElement, firstObjectClass));
                // }
                // }
                //
                // newFields.put(reference.value(), goldenCollection);
                // }
                //
                // } else if (value instanceof Map) {
                //
                // newFields.put(reference.value(),
                // convertObjectToTargetMap(source, value,
                // entryField.getType()));
                //
                // } else {
                //
                // newFields.put(reference.value(), value);
                //
                // }

                System.out.println("Reference for field " + entryField.getName() + " - " + reference);
                Object resolvedValue = resolveValue(source, originalMap, reference.refType(), reference.value(), valueType);

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

    public Object resolveValue(String source, Object originalMap, ReferenceTypeEnum refType,
            String refValue, Type valueType) throws InstantiationException, IllegalAccessException {

        Map<String, Object> targetMap;
        if (originalMap instanceof Map) {
            targetMap = (Map<String, Object>) originalMap;
        } else {
            targetMap = caster.map(originalMap);
        }

        TypeToken<?> typeToken = (TypeToken<?>) TypeToken.get(valueType);
        
        Class<?> targetClass = (Class<?>) typeToken.getRawType();

        Object resolvedValue = null;

        if (refType == ReferenceTypeEnum.NORMAL) {

          resolvedValue = DigesteroidsReflectionUtils.getRecursive(targetMap, refValue);
          
          if (targetClass.getAnnotation(DigesterEntity.class) != null) {
            resolvedValue = convertObjectToType(source, resolvedValue, valueType);
          } 
          
        } else if (refType == ReferenceTypeEnum.JSON_PATH) {

            resolvedValue = JsonPath.read(caster.json(targetMap), refValue);

        } else if (refType == ReferenceTypeEnum.PASS_THROUGH) {

            
            if (Collection.class.isAssignableFrom(targetClass)) {
                
                System.out.println("Annotated: " + valueType);
                
                List<Object> array = new ArrayList<Object>();
                
                Object resolvedElement;
                if (valueType instanceof ParameterizedType) {
                    resolvedElement = convertObjectToType(source, targetMap, ((ParameterizedType) valueType).getActualTypeArguments()[0]);
                } else {
                    resolvedElement = convertObjectToType(source, targetMap, valueType);
                }
                array.add(resolvedElement);

                resolvedValue = array;
                
                
            } else {
                resolvedValue = convertObjectToType(source, targetMap, valueType);
            }

        } else if (refType == ReferenceTypeEnum.HARDCODE) {

            resolvedValue = refValue;

        }

        //make sure that it's the type
        return caster.cast(resolvedValue, valueType);

    }

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

              //cast to the type, and call the setter
              setter.invoke(target, caster.cast(resolvedValue, valueType));

            } catch (Exception e) {
                log.warn("Exception occurred while trying to convert data", e);
            }

        }
    }
}
