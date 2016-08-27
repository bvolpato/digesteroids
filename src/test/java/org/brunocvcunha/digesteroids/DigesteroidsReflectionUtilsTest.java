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

import static org.junit.Assert.assertEquals;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.brunocvcunha.digesteroids.model.PersonPOJO;
import org.junit.Test;

/**
 * Some testing for reflection utilities
 * 
 * @author Bruno Candido Volpato da Cunha
 *
 */
public class DigesteroidsReflectionUtilsTest {

  @Test
  public void simpleReflect() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException {
    
    Map<String, Object> brunoMap = new HashMap<String, Object>();
    brunoMap.put("name", "Bruno Candido Volpato da Cunha");
    brunoMap.put("age", "24");
    
    PersonPOJO brunoVO = DigesteroidsReflectionUtils.buildInstanceForMap(PersonPOJO.class, brunoMap);
    
    assertEquals("Bruno Candido Volpato da Cunha", brunoVO.getName());
    assertEquals((Integer) 24, brunoVO.getAge());
  }
  
  
}
