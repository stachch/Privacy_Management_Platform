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

import java.io.ObjectInputStream;

/**
 * Interface for implementing callbacks when the {@link ServerProvider} is currently working.
 * 
 * @author Tobias Kuhn
 * 
 */
public interface IServerDownloadCallback {
    
    /**
     * Called for the progress during one download.
     * 
     * @param position
     *            the amount of bytes already READ
     * @param length
     *            the amount of bytes total
     * @deprecated pretty much impossible to use with {@link ObjectInputStream}.
     */
    @Deprecated
    void download(int position, int length);
    
    
    /**
     * Called for the amount of steps to be performed.
     * 
     * @param position
     *            the amount of steps already completed
     * @param length
     *            the amount of steps total
     */
    void step(int position, int length);
    
}
