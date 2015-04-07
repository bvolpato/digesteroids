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

/**
 * Test POJO
 * 
 * @author Bruno Candido Volpato da Cunha
 *
 */
public class PersonPOJO {
  private String name;
  private Integer age;
  private AddressPOJO address;
  /**
   * @return the name
   */
  public String getName() {
    return name;
  }
  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }
  /**
   * @return the age
   */
  public Integer getAge() {
    return age;
  }
  /**
   * @param age the age to set
   */
  public void setAge(Integer age) {
    this.age = age;
  }
  /**
   * @return the address
   */
  public AddressPOJO getAddress() {
    return address;
  }
  /**
   * @param address the address to set
   */
  public void setAddress(AddressPOJO address) {
    this.address = address;
  }
  

}
