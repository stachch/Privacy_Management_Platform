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
package de.unistuttgart.ipvs.pmp.resource.privacysetting;

import java.util.Comparator;

/**
 * {@link ComparablePrivacySetting} that uses the basic toString() method of the type T for human readable values.
 * 
 * @author Tobias Kuhn
 * 
 * @param <T>
 *            the type that is stored in this {@link DefaultPrivacySetting}.
 */
public abstract class DefaultPrivacySetting<T extends Comparable<T>> extends ComparablePrivacySetting<T> {
    
    /**
     * Creates a {@link DefaultPrivacySetting} using the {@link Comparable} implementation of T.
     */
    public DefaultPrivacySetting() {
        super();
    }
    
    
    /**
     * Creates a {@link DefaultPrivacySetting} using the {@link Comparator} implementation for T.
     * 
     * @param comparator
     *            Comparator to represent the "permit more or equal" partial order.
     */
    public DefaultPrivacySetting(Comparator<T> comparator) {
        super(comparator);
    }
    
    
    @Override
    public String getHumanReadableValue(String value) throws PrivacySettingValueException {
        return parseValue(value).toString();
    }
    
}
