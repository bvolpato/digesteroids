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
package org.brunocvcunha.digesteroids.model;

import org.brunocvcunha.digesteroids.ReferenceTypeEnum;
import org.brunocvcunha.digesteroids.annotation.DigesterEntity;
import org.brunocvcunha.digesteroids.annotation.DigesterMapping;
import org.brunocvcunha.digesteroids.caster.DigisteroidsTest;

/**
 * Test Address POJO
 * 
 * @author Bruno Candido Volpato da Cunha
 *
 */
@DigesterEntity
public class AddressPOJO {
  @DigesterMapping(source = DigisteroidsTest.SOURCE_EXAMPLE, value = "address")
  @DigesterMapping(source = DigisteroidsTest.SOURCE_HTML, refType = ReferenceTypeEnum.HTML_CSS, value = "p.address1")
  private String address1;
  
  @DigesterMapping(source = DigisteroidsTest.SOURCE_EXAMPLE, value = "addressCity")
  @DigesterMapping(source = DigisteroidsTest.SOURCE_HTML, refType = ReferenceTypeEnum.HTML_XPATH, value = "p > span")
  private String city;
  
  /**
   * @return the address1
   */
  public String getAddress1() {
    return address1;
  }
  /**
   * @param address1 the address1 to set
   */
  public void setAddress1(String address1) {
    this.address1 = address1;
  }
  /**
   * @return the city
   */
  public String getCity() {
    return city;
  }
  /**
   * @param city the city to set
   */
  public void setCity(String city) {
    this.city = city;
  }
  

}
