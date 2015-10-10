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
package de.unistuttgart.ipvs.pmp.model.server;

/**
 * Null object to do nothing at all but to save if (obj != null) checks.
 * 
 * @author Tobias Kuhn
 * 
 */
public class NullServerDownloadCallback implements IServerDownloadCallback {
    
    protected static final NullServerDownloadCallback instance = new NullServerDownloadCallback();
    
    
    private NullServerDownloadCallback() {
    }
    
    
    @Override
    public void download(int position, int length) {
    }
    
    
    @Override
    public void step(int position, int length) {
    }
    
}
