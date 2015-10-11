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

import android.content.Context;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.DefaultPrivacySetting;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.IPrivacySettingView;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.PrivacySettingValueException;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.view.EnumView;

/**
 * {@link DefaultPrivacySetting} for {@link Enum}s.
 * 
 * @author Tobias Kuhn, Jakob Jarosch
 * 
 */
public class EnumPrivacySetting<T extends Enum<T>> extends DefaultPrivacySetting<T> {
    
    private final Class<T> clazz;
    
    private final T defaultValue;
    
    
    public EnumPrivacySetting(Class<T> enumClass) {
        super();
        this.clazz = enumClass;
        this.defaultValue = enumClass.getEnumConstants()[0];
    }
    
    
    public EnumPrivacySetting(Class<T> enumClass, T defaultValue) {
        super();
        this.clazz = enumClass;
        this.defaultValue = defaultValue;
    }
    
    
    @Override
    public T parseValue(String value) throws PrivacySettingValueException {
        try {
            if (value == null) {
                return this.defaultValue;
            }
            return Enum.valueOf(this.clazz, value);
        } catch (IllegalArgumentException iae) {
            throw new PrivacySettingValueException(iae);
        }
    }
    
    
    @Override
    public String valueToString(T value) {
        if (value == null) {
            return null;
        }
        return value.name();
    }
    
    
    @Override
    public IPrivacySettingView<T> makeView(Context context) {
        return new EnumView<T>(context, this.clazz);
    }
    
}
