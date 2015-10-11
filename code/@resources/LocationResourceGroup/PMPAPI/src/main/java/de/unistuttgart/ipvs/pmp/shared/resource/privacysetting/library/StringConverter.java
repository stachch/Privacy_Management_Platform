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

import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.ISafeStringConverter;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.IStringConverter;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.PrivacySettingValueException;

/**
 * Implements various state-less {@link IStringConverter} singletons.
 * 
 * @author Jakob Jarosch, Tobias Kuhn
 *         
 */
public final class StringConverter {
    
    /*
     * Singleton instances
     */
    
    public static final IStringConverter<String> forString = new StringConverter2();
    public static final IStringConverter<Boolean> forBoolean = new BooleanConverter();
    public static final IStringConverter<Long> forLong = new LongConverter();
    public static final IStringConverter<Integer> forInteger = new IntegerConverter();
    public static final IStringConverter<Double> forDouble = new DoubleConverter();
    public static final IStringConverter<Float> forFloat = new FloatConverter();
    
    /*
     * Actual converters
     */
    
    static class StringConverter2 implements IStringConverter<String> {
        
        @Override
        public String valueOf(String string) {
            return string;
        }
        
        
        @Override
        public String toString(String value) {
            return value;
        }
    }
    
    static class BooleanConverter implements IStringConverter<Boolean> {
        
        @Override
        public Boolean valueOf(String string) {
            boolean result = Boolean.valueOf(string);
            if (!result && !string.equalsIgnoreCase(Boolean.FALSE.toString())) {
                throw new IllegalArgumentException();
            }
            return result;
        }
        
        
        @Override
        public String toString(Boolean value) {
            return value.toString();
        }
    }
    
    static class LongConverter implements IStringConverter<Long> {
        
        @Override
        public Long valueOf(String string) {
            return Long.parseLong(string);
        }
        
        
        @Override
        public String toString(Long value) {
            return value.toString();
        }
    }
    
    static class IntegerConverter implements IStringConverter<Integer> {
        
        @Override
        public Integer valueOf(String string) {
            return Integer.parseInt(string);
        }
        
        
        @Override
        public String toString(Integer value) {
            return value.toString();
        }
    }
    
    static class DoubleConverter implements IStringConverter<Double> {
        
        @Override
        public Double valueOf(String string) {
            return Double.parseDouble(string);
        }
        
        
        @Override
        public String toString(Double value) {
            return value.toString();
        }
    }
    
    static class FloatConverter implements IStringConverter<Float> {
        
        @Override
        public Float valueOf(String string) {
            return Float.parseFloat(string);
        }
        
        
        @Override
        public String toString(Float value) {
            return value.toString();
        }
    }
    
    /*
     * PS library wrapped converters
     */
    
    public static final ISafeStringConverter<String> forStringSafe = wrapSafe(forString, "");
    public static final ISafeStringConverter<Boolean> forBooleanSafe = wrapSafe(forBoolean, Boolean.FALSE);
    public static final ISafeStringConverter<Long> forLongSafe = wrapSafe(forLong, 0L);
    public static final ISafeStringConverter<Integer> forIntegerSafe = wrapSafe(forInteger, 0);
    public static final ISafeStringConverter<Double> forDoubleSafe = wrapSafe(forDouble, 0.0);
    public static final ISafeStringConverter<Float> forFloatSafe = wrapSafe(forFloat, 0f);
    
    
    /**
     * Wraps a converter into another one and catches all exceptions, so {@link PrivacySettingValueException}s are
     * thrown. Handles default values via null and empty strings.
     * 
     * @param converter
     * @param defaultValue
     * @return <code>converter</code> wrapped so that only {@link PrivacySettingValueException} will be thrown and
     *         default null values are handled
     */
    public static <T> ISafeStringConverter<T> wrapSafe(final IStringConverter<T> converter, final T defaultValue) {
        return new ISafeStringConverter<T>() {
            
            @Override
            public T valueOf(String string) throws PrivacySettingValueException {
                if (string == null || string.equals("")) {
                    return defaultValue;
                }
                
                try {
                    return converter.valueOf(string);
                } catch (Throwable t) {
                    throw new PrivacySettingValueException(t.getMessage(), t);
                }
            }
            
            
            @Override
            public String toString(T value) {
                if (value == null) {
                    return "";
                }
                
                return converter.toString(value);
            }
        };
    }
}
