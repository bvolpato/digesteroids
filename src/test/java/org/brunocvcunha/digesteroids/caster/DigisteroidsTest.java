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

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.brunocvcunha.digesteroids.Digesteroids;
import org.brunocvcunha.digesteroids.model.PersonPOJO;
import org.brunocvcunha.inutils4j.MyStringUtils;
import org.junit.Test;

/**
 * Some testing for casting utilities
 * 
 * @author Bruno Candido Volpato da Cunha
 *
 */
public class DigisteroidsTest {
  
  private static Logger log = Logger.getLogger(DigisteroidsTest.class);

  public static final String SOURCE_EXAMPLE = "SourceTest";
  public static final String SOURCE_HTML = "SourceTestHtml";
  public static final String SOURCE_PROPERTY = "SourceTestProperty";
  public static final String SOURCE_JSON = "SourceTestJson";
  
  @Test
  public void simpleMapDigester() throws InstantiationException, IllegalAccessException {

    Digesteroids digister = new Digesteroids();

    Map<String, Object> personMap = new LinkedHashMap<>();
    personMap.put("fullName", "Bruno");
    personMap.put("age", "24");
    
    Map<String, Object> addressMap = new LinkedHashMap<>();
    addressMap.put("address", "Av Santos Dumont, 801");
    addressMap.put("addressCity", "Joinville");
    
    personMap.put("personAddress", addressMap);
    
    PersonPOJO person = digister.convertObjectToType(DigisteroidsTest.SOURCE_EXAMPLE, personMap, PersonPOJO.class);
    log.info("simpleMapDigester: " + digister.getCaster().json(person));

    assertEquals("Bruno", person.getName());
    assertEquals(Integer.valueOf(24), person.getAge());
    assertEquals("Joinville", person.getAddress().getCity());

  }


  @Test
  public void simpleJsonDigester() throws InstantiationException, IllegalAccessException {

    Digesteroids digister = new Digesteroids();
    PersonPOJO person = digister.convertObjectToType(DigisteroidsTest.SOURCE_EXAMPLE, "{\"fullName\": \"Bruno Candido Volpato da Cunha\"}", PersonPOJO.class);
    log.info("simpleJsonDigester: " + digister.getCaster().json(person));

    assertEquals("Bruno Candido Volpato da Cunha", person.getName());

  }

  @Test
  public void simpleJsonPathDigester() throws InstantiationException, IllegalAccessException {

    Digesteroids digister = new Digesteroids();
    PersonPOJO person = digister.convertObjectToType(DigisteroidsTest.SOURCE_JSON, "{\"personName\": \"Bruno Candido Volpato da Cunha\"}", PersonPOJO.class);
    log.info("simpleJsonPathDigester: " + digister.getCaster().json(person));

    assertEquals("Bruno Candido Volpato da Cunha", person.getName());

  }

  
  @Test
  public void simpleHTMLDigester() throws InstantiationException, IllegalAccessException, IOException {

    Digesteroids digister = new Digesteroids();
    
    PersonPOJO person = digister.convertHTMLToType(DigisteroidsTest.SOURCE_HTML, getClass().getResourceAsStream("/source.html"), PersonPOJO.class);
    log.info("simpleHTMLDigester: " + digister.getCaster().json(person));

    assertEquals("Bruno Candido Volpato da Cunha", person.getName());
    assertNotNull(person.getAddress());
    assertEquals("Av Santos Dumont, 831", person.getAddress().getAddress1());
    assertEquals("Palo Alto", person.getAddress().getCity());

  }

  @Test
  public void simplePropertiesDigester() throws InstantiationException, IllegalAccessException, IOException {

    Digesteroids digister = new Digesteroids();
    
    Properties prop = new Properties();
    prop.load(getClass().getResourceAsStream("/person.properties"));
    
    PersonPOJO person = digister.convertObjectToType(DigisteroidsTest.SOURCE_PROPERTY, prop, PersonPOJO.class);
    log.info("simplePropertiesDigester: " + digister.getCaster().json(person));

    assertEquals("Bruno", person.getName());
    assertNotNull(person.getAddress());
    assertEquals("Castro St", person.getAddress().getAddress1());
    assertEquals("San Francisco", person.getAddress().getCity());

  }

  

}
