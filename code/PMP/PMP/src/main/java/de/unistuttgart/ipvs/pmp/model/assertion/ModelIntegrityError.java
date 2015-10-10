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
package de.unistuttgart.ipvs.pmp.model.assertion;

/**
 * If the model finds itself in a state that it cannot recover from, then this {@link ModelIntegrityError} is thrown.
 * 
 * @author Tobias Kuhn
 * 
 */
public class ModelIntegrityError extends AssertError {
    
    public ModelIntegrityError(String text) {
        super(text);
    }
    
    private static final long serialVersionUID = -478608329593768065L;
    
}
