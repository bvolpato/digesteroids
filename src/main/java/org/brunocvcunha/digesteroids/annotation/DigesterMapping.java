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
package org.brunocvcunha.digesteroids.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.brunocvcunha.digesteroids.ReferenceTypeEnum;

/**
 * Annotation used to map field with another table
 * @author Bruno Candido Volpato da Cunha
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(value = DigesterMappings.class)
public @interface DigesterMapping {
    
    /**
     * @return source
     */
    String source() default "";
    
    /**
     * @return refType
     */
    ReferenceTypeEnum refType() default ReferenceTypeEnum.NORMAL;
    
    /**
     * @return value
     */
    String value();
    
    /**
     * @return htmlText
     */
    boolean htmlText() default true;

    /**
     * @return trim
     */
    boolean trim() default true;

    
}
