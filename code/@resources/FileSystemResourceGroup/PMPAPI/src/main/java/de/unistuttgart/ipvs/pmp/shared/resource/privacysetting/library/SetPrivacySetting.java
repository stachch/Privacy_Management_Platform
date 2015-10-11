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

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.AbstractPrivacySetting;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.IPrivacySettingView;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.IStringConverter;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.PrivacySettingValueException;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.view.SetView;

/**
 * Privacy setting for set types.
 * 
 * @author Tobias Kuhn, Jakob Jarosch
 * 
 * @param <T>
 *            the {@link Serializable} type to be stored
 */
public class SetPrivacySetting<T> extends AbstractPrivacySetting<Set<T>> {
    
    private static final String SEPARATOR = ";";
    private static final String SEPARATOR_REGEX = "\\;";
    private static final String ESCAPE_SEPARATOR = "\\;";
    
    private IStringConverter<T> converter;
    
    private Constructor<? extends IPrivacySettingView<T>> childViewConstructor;
    private Object[] childViewConstructorInvocation;
    
    
    public SetPrivacySetting(IStringConverter<T> converter,
            Constructor<? extends IPrivacySettingView<T>> childViewConstructor,
            Object... childViewConstructorInvocation) {
        super();
        this.converter = converter;
        this.childViewConstructor = childViewConstructor;
        this.childViewConstructorInvocation = childViewConstructorInvocation;
    }
    
    
    @Override
    public Set<T> parseValue(String value) throws PrivacySettingValueException {
        if ((value == null) || value.equals("")) {
            return new HashSet<T>();
        }
        
        Set<T> set = new HashSet<T>();
        for (String item : value.split(SEPARATOR_REGEX)) {
            set.add(this.converter.valueOf(item.replace(ESCAPE_SEPARATOR, SEPARATOR)));
        }
        
        return set;
    }
    
    
    @Override
    public String valueToString(Set<T> value) {
        if (value == null) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        for (T item : value) {
            sb.append(this.converter.toString(item).replace(SEPARATOR, ESCAPE_SEPARATOR));
            sb.append(SEPARATOR);
        }
        
        if (sb.length() > 0) {
            return sb.toString().substring(0, sb.length() - SEPARATOR.length());
        } else {
            return "";
        }
        
    }
    
    
    @Override
    public boolean permits(Set<T> value, Set<T> reference) {
        return value.containsAll(reference);
    }
    
    
    @Override
    public String getHumanReadableValue(String value) throws PrivacySettingValueException {
        Set<T> set = parseValue(value);
        if (set.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (T item : set) {
            sb.append(item.toString());
            sb.append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }
    
    
    @Override
    public IPrivacySettingView<Set<T>> makeView(Context context) {
        return new SetView<T>(context, this.childViewConstructor, this.childViewConstructorInvocation);
    }
    
}
