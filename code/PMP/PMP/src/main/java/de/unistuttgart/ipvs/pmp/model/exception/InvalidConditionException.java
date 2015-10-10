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

import de.unistuttgart.ipvs.pmp.model.context.IContext;
import de.unistuttgart.ipvs.pmp.model.context.IContextView;

/**
 * An exception that is thrown when a condition was supplied to an {@link IContext} or an {@link IContextView} that was
 * not compatible with the context.
 * 
 * @author Tobias Kuhn
 * 
 */
public class InvalidConditionException extends Exception {
    
    private static final long serialVersionUID = 4150641607569620296L;
    
    
    /**
     * @see {@link Exception#Exception()}
     */
    public InvalidConditionException() {
        super();
    }
    
    
    /**
     * @see {@link Exception#Exception(String, Throwable)}
     */
    public InvalidConditionException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
    
    
    /**
     * @see {@link Exception#Exception(String)}
     */
    public InvalidConditionException(String detailMessage) {
        super(detailMessage);
    }
    
    
    /**
     * @see {@link Exception#Exception(Throwable)}
     */
    public InvalidConditionException(Throwable throwable) {
        super(throwable);
    }
    
}
