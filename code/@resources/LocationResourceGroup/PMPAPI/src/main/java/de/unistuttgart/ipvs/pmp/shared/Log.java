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
package de.unistuttgart.ipvs.pmp.shared;

/**
 * Internal {@link android.util.Log} class for the project. Logging is so much more simple. (Or maybe it is not)
 * 
 * @author Jakob Jarosch
 */
public class Log {
    
    private static String getTag(Object reference) {
        if (reference == null) {
            return "null";
        }
        if (reference instanceof String) {
            return (String) reference;
        }
        Class<? extends Object> clazz = reference.getClass();
        return clazz.isAnonymousClass() ? clazz.getName() : clazz.getSimpleName();
    }
    
    
    /**
     * @param reference
     *            most certainly <code>this</code> or a String describing the tag to use
     * @see android.util.Log#d(String, String)
     */
    public static void d(Object reference, String message) {
        android.util.Log.d(getTag(reference), message);
    }
    
    
    /**
     * @param reference
     *            most certainly <code>this</code> or a String describing the tag to use
     * @see android.util.Log#d(String, String, Throwable)
     */
    public static void d(Object reference, String message, Throwable t) {
        android.util.Log.d(getTag(reference), message, t);
    }
    
    
    /**
     * @param reference
     *            most certainly <code>this</code> or a String describing the tag to use
     * @see android.util.Log#e(String, String)
     */
    public static void e(Object reference, String message) {
        android.util.Log.e(getTag(reference), message);
    }
    
    
    /**
     * @param reference
     *            most certainly <code>this</code> or a String describing the tag to use
     * @see android.util.Log#e(String, String, Throwable)
     */
    public static void e(Object reference, String message, Throwable t) {
        android.util.Log.e(getTag(reference), message, t);
    }
    
    
    /**
     * @param reference
     *            most certainly <code>this</code> or a String describing the tag to use
     * @see android.util.Log#i(String, String)
     */
    public static void i(Object reference, String message) {
        android.util.Log.i(getTag(reference), message);
    }
    
    
    /**
     * @param reference
     *            most certainly <code>this</code> or a String describing the tag to use
     * @see android.util.Log#i(String, String, Throwable)
     */
    public static void i(Object reference, String message, Throwable t) {
        android.util.Log.i(getTag(reference), message, t);
    }
    
    
    /**
     * @param reference
     *            most certainly <code>this</code> or a String describing the tag to use
     * @see android.util.Log#v(String, String)
     */
    public static void v(Object reference, String message) {
        android.util.Log.v(getTag(reference), message);
    }
    
    
    /**
     * @param reference
     *            most certainly <code>this</code> or a String describing the tag to use
     * @see android.util.Log#v(String, String, Throwable)
     */
    public static void v(Object reference, String message, Throwable t) {
        android.util.Log.v(getTag(reference), message, t);
    }
    
    
    /**
     * @param reference
     *            most certainly <code>this</code> or a String describing the tag to use
     * @see android.util.Log#w(String, String)
     */
    public static void w(Object reference, String message) {
        android.util.Log.w(getTag(reference), message);
    }
    
    
    /**
     * @param reference
     *            most certainly <code>this</code> or a String describing the tag to use
     * @see android.util.Log#w(String, String, Throwable)
     */
    public static void w(Object reference, String message, Throwable t) {
        android.util.Log.w(getTag(reference), message, t);
    }
    
}
