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
 * An exception that is thrown whenever a plugin is registered that is somehow corrupt.
 * 
 * @author Tobias Kuhn
 * 
 */
public class InvalidPluginException extends Exception {
    
    private static final long serialVersionUID = -4691956490321050959L;
    
    
    /**
     * @see {@link Exception#Exception()}
     */
    public InvalidPluginException() {
        super();
    }
    
    
    /**
     * @see {@link Exception#Exception(String, Throwable)}
     */
    public InvalidPluginException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
    
    
    /**
     * @see {@link Exception#Exception(String)}
     */
    public InvalidPluginException(String detailMessage) {
        super(detailMessage);
    }
    
    
    /**
     * @see {@link Exception#Exception(Throwable)}
     */
    public InvalidPluginException(Throwable throwable) {
        super(throwable);
    }
    
}
