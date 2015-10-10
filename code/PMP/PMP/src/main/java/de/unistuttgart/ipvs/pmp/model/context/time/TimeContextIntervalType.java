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
package de.unistuttgart.ipvs.pmp.model.context.time;

import java.util.ArrayList;
import java.util.List;

/**
 * Type to identify the intervals in a {@link TimeContextCondition}.
 * 
 * @author Tobias Kuhn
 * 
 */
public enum TimeContextIntervalType {
    REPEAT_DAILY('D'),
    REPEAT_WEEKLY('W'),
    REPEAT_MONTHLY('M'),
    REPEAT_YEARLY('Y');
    
    private static final String DAY_SEPARATOR = ",";
    
    /**
     * The character that identifies this type in a {@link TimeContextCondition} string.
     */
    private Character identifier;
    
    
    private TimeContextIntervalType(Character identifier) {
        this.identifier = identifier;
    }
    
    
    public Character getIdentifier() {
        return this.identifier;
    }
    
    
    /**
     * Converts a {@link TimeContextConditionDay} list into a string for this interval
     * 
     * @param days
     * @return
     */
    public String makeList(List<Integer> days) {
        StringBuffer sb = new StringBuffer();
        
        for (Integer tccd : days) {
            sb.append(tccd);
            sb.append(DAY_SEPARATOR);
        }
        
        return sb.toString();
    }
    
    
    /**
     * Converts a string into a {@link TimeContextConditionDay} list for this interval
     * 
     * @param list
     * @return
     */
    public List<Integer> makeDays(String list) {
        List<Integer> result = new ArrayList<Integer>();
        
        if (!list.contains(DAY_SEPARATOR)) {
            return result;
        }
        
        for (String day : list.split(DAY_SEPARATOR)) {
            result.add(Integer.parseInt(day));
        }
        
        return result;
    }
    
    
    public static TimeContextIntervalType getForIdentifier(Character identifier) {
        for (TimeContextIntervalType tccit : values()) {
            if (tccit.getIdentifier().equals(identifier)) {
                return tccit;
            }
        }
        
        return null;
    }
}
