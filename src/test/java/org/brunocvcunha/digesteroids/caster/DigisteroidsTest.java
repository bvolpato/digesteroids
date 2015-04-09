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
package org.brunocvcunha.digesteroids.caster;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.brunocvcunha.digesteroids.Digesteroids;
import org.brunocvcunha.digesteroids.model.PersonPOJO;
import org.junit.Test;

/**
 * Some testing for casting utilities
 * 
 * @author Bruno Candido Volpato da Cunha
 *
 */
public class DigisteroidsTest {
  
  public static final String LINKEDIN = "LinkedIn";
  
  @Test
  public void simpleMapDigester() throws InstantiationException, IllegalAccessException {

    Digesteroids digister = new Digesteroids();

    Map<String, Object> personMap = new LinkedHashMap<String, Object>();
    personMap.put("fullName", "Bruno");
    personMap.put("age", "24");
    
    Map<String, Object> addressMap = new LinkedHashMap<String, Object>();
    addressMap.put("address", "Av Santos Dumont, 801");
    addressMap.put("addressCity", "Joinville");
    
    personMap.put("personAddress", addressMap);
    
    PersonPOJO person = digister.convertObjectToType(DigisteroidsTest.LINKEDIN, personMap, PersonPOJO.class);
    
    assertEquals("Bruno", person.getName());
    assertEquals(Integer.valueOf(24), person.getAge());
    assertEquals("Joinville", person.getAddress().getCity());

  }


  @Test
  public void simpleJsonDigester() throws InstantiationException, IllegalAccessException {

    Digesteroids digister = new Digesteroids();
    PersonPOJO person = digister.convertObjectToType(DigisteroidsTest.LINKEDIN, "{\"fullName\": \"Bruno Candido Volpato da Cunha\"}", PersonPOJO.class);
    
    assertEquals("Bruno Candido Volpato da Cunha", person.getName());

  }


}
