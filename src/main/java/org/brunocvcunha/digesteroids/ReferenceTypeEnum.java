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

/**
 * Reference Type Enum for annotation mapping
 * @author Bruno Candido Volpato da Cunha
 *
 */
public enum ReferenceTypeEnum {

    /**
     * Normal reference, using getter
     */
    NORMAL,
    
    /**
     * Always assume the same value in the target
     */
    HARDCODE,
    
    /**
     * Passes the entire cursor to allow nested operations
     */
    PASS_THROUGH,
    
    /**
     * Uses JSON Path library to select
     */
    JSON_PATH,
    
    /**
     * Uses XPath to select
     */
    HTML_XPATH,
    
    /**
     * Uses CSS selector to compute result
     */
    HTML_CSS,
    
    /**
     * Uses HTML ID to get the result
     */
    HTML_ID;
    
}
