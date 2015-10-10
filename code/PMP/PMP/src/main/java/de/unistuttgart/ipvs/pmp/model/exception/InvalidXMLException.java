/*
 * Copyright 2012 pmp-android development team
 * Project: PMP
 * Project-Site: https://github.com/stachch/Privacy_Management_Platform
 *
 * ---------------------------------------------------------------------
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.unistuttgart.ipvs.pmp.model.exception;

/**
 * An exception that is thrown whenever a component is registered with PMP that contains an XML file that is somehow
 * corrupt.
 * 
 * @author Tobias Kuhn
 * 
 */
public class InvalidXMLException extends Exception {
    
    private static final long serialVersionUID = -8873547466508287839L;
    
    private static final String NULL_STR = "null";
    private static final String COMPARE_EXCEPTION = "%s mismatch: '%s' != '%s'";
    
    
    /**
     * @see {@link Exception#Exception()}
     */
    public InvalidXMLException() {
        super();
    }
    
    
    /**
     * @see {@link Exception#Exception(String, Throwable)}
     */
    public InvalidXMLException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
    
    
    /**
     * @see {@link Exception#Exception(String)}
     */
    public InvalidXMLException(String detailMessage) {
        super(detailMessage);
    }
    
    
    /**
     * @see {@link Exception#Exception(Throwable)}
     */
    public InvalidXMLException(Throwable throwable) {
        super(throwable);
    }
    
    
    /**
     * Generates an {@link InvalidXMLException} because the two objects are not the same.
     * 
     * @param compareName
     * @param obj1
     * @param obj2
     */
    public InvalidXMLException(String compareName, Object obj1, Object obj2) {
        this(String.format(COMPARE_EXCEPTION, compareName, obj1 == null ? NULL_STR : obj1.toString(),
                obj2 == null ? NULL_STR : obj2.toString()));
    }
    
}
