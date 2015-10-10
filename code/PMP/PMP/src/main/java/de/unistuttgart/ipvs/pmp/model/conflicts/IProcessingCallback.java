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
package de.unistuttgart.ipvs.pmp.model.conflicts;

/**
 * The {@link IProcessingCallback} is used to inform the caller about the current progress of conflict calculation. The
 * finished method is invoked when the calculation process is ended and the conflict list is up to date.
 * 
 * @author Jakob Jarosch
 */
public interface IProcessingCallback {
    
    /**
     * Is invoked when the next Preset is being processed.
     * 
     * @param completed
     *            Number of completed tasks.
     * @param fullCount
     *            Number of all tasks.
     */
    public void progressUpdate(int completed, int fullCount);
    
    
    /**
     * Message which informs the user about the current step in calculation process.
     * 
     * @param message
     *            Internationalized message.
     */
    public void stepMessage(String message);
    
    
    /**
     * The method is called on completion of calculation.
     */
    public void finished();
}
