/*
 * Copyright 2012 pmp-android development team
 * Project: PMP-API
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
package de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.library;

import java.util.Comparator;

import android.content.Context;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.DefaultPrivacySetting;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.IPrivacySettingView;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.PrivacySettingValueException;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.view.IntegerView;

/**
 * {@link DefaultPrivacySetting} for {@link Integer}.
 * 
 * @author Tobias Kuhn, Jakob Jarosch
 *         
 */
public class IntegerPrivacySetting extends DefaultPrivacySetting<Integer> {
    
    private int worstValue;
    
    
    public IntegerPrivacySetting(final int worstValue, final int bestValue) {
        super(new Comparator<Integer>() {
            
            @Override
            public int compare(Integer object1, Integer object2) {
                if (worstValue < bestValue) {
                    return object1.compareTo(object2);
                } else {
                    return object2.compareTo(object1);
                }
            }
        });
        
        this.worstValue = worstValue;
    }
    
    
    @Override
    public Integer parseValue(String value) throws PrivacySettingValueException {
        if (value == null || value.equals("")) {
            return this.worstValue;
        }
        return StringConverter.forIntegerSafe.valueOf(value);
    }
    
    
    @Override
    public String valueToString(Integer value) {
        return StringConverter.forIntegerSafe.toString(value);
    }
    
    
    @Override
    public IPrivacySettingView<Integer> makeView(Context context) {
        return new IntegerView(context);
    }
}
