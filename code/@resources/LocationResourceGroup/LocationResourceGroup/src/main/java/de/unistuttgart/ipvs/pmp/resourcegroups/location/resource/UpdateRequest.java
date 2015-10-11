/*
 * Copyright 2012 pmp-android development team
 * Project: LocationResourceGroup
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
package de.unistuttgart.ipvs.pmp.resourcegroups.location.resource;

class UpdateRequest {
    
    public static final long MAX_TIME_BETWEEN_REQUEST = 30 * 1000;
    
    private long minTime;
    private float minDistance;
    private long lastRequest;
    
    
    public UpdateRequest(long minTime, float minDistance) {
        this.minTime = minTime;
        this.minDistance = minDistance;
        this.lastRequest = System.currentTimeMillis();
    }
    
    
    public long getMinTime() {
        return this.minTime;
    }
    
    
    public void setMinTime(long minTime) {
        this.minTime = minTime;
    }
    
    
    public float getMinDistance() {
        return this.minDistance;
    }
    
    
    public void setMinDistance(float minDistance) {
        this.minDistance = minDistance;
    }
    
    
    public long getLastRequest() {
        return this.lastRequest;
    }
    
    
    public boolean isOutdated() {
        return (System.currentTimeMillis() > (this.lastRequest + MAX_TIME_BETWEEN_REQUEST));
    }
    
    
    public void setLastRequest(long lastRequest) {
        this.lastRequest = lastRequest;
    }
}
