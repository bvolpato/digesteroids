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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;

import org.brunocvcunha.digesteroids.model.PersonPOJO;
import org.apache.log4j.Logger;
import org.brunocvcunha.digesteroids.Digesteroids;
import org.brunocvcunha.digesteroids.cast.DigesteroidsDefaultCaster;
import org.junit.Test;

/**
 * Some testing for casting utilities
 * 
 * @author Bruno Candido Volpato da Cunha
 *
 */
public class DigesteroidsCasterTest {

  private static Logger log = Logger.getLogger(DigesteroidsCasterTest.class);

  @Test
  public void simpleStringCast() {

    DigesteroidsDefaultCaster caster = new DigesteroidsDefaultCaster();

    assertEquals("Bruno", caster.cast("Bruno", String.class));
    assertEquals("1", caster.cast(1, String.class));
    assertEquals("1", caster.cast(1, String.class));
    assertEquals("true", caster.cast(true, String.class));

  }

  @Test
  public void simpleIntCast() {

    DigesteroidsDefaultCaster caster = new DigesteroidsDefaultCaster();

    assertEquals(Integer.valueOf(1), caster.cast("1", Integer.class));
    assertEquals(Integer.valueOf(1), caster.cast("1.0", Integer.class));

  }

  @Test
  public void simpleDateCast() {

    DigesteroidsDefaultCaster caster = new DigesteroidsDefaultCaster();

    Calendar dateCalendar = new GregorianCalendar();
    dateCalendar.setTime(caster.date("2015-08-06T05:43:38.239Z"));
    
    
    assertEquals(2015, dateCalendar.get(Calendar.YEAR));
    assertEquals(7, dateCalendar.get(Calendar.MONTH));
    assertEquals(5, dateCalendar.get(Calendar.DAY_OF_MONTH));

  }
  
  
  @Test
  public void simpleMapToPOJOCast() {

    DigesteroidsDefaultCaster caster = new DigesteroidsDefaultCaster();

    Map<String, Object> personMap = new LinkedHashMap<String, Object>();
    personMap.put("name", "Bruno");
    personMap.put("age", "24");
    
    Map<String, Object> addressMap = new LinkedHashMap<String, Object>();
    addressMap.put("address1", "Av Santos Dumont, 801");
    addressMap.put("city", "Joinville");
    personMap.put("address", addressMap);
    
    PersonPOJO person = caster.cast(personMap, PersonPOJO.class);
    log.info("simpleMapToPOJOCast: " + caster.json(person));
    
    assertEquals("Bruno", person.getName());
    assertEquals(Integer.valueOf(24), person.getAge());
    assertEquals("Joinville", person.getAddress().getCity());

  }

  


}
