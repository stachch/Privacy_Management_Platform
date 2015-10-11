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
package de.unistuttgart.ipvs.pmp.model.assertion;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import de.unistuttgart.ipvs.pmp.model.element.app.IApp;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.IResourceGroup;
import de.unistuttgart.ipvs.pmp.shared.Log;

/**
 * A static assertion class much like in the old model. Performs all kinds of checks and reacts to errors.
 * 
 * @author Tobias Kuhn
 * 
 */
public class Assert {
    
    private static final String TAG = "Assert";
    
    /*
     * all kinds of strings used in error messages
     */
    public static final String ILLEGAL_NULL = "'%s' was expected to be an Object, found null instead. (%s)";
    public static final String ILLEGAL_NOT_NULL = "'%s' was expected to be null, found Object instead. (%s)";
    public static final String ILLEGAL_CREATOR = "'%s' was expected to be a Creator parameter, found something else instead. (%s)";
    public static final String ILLEGAL_METHOD = "'%s' was expecting this call to never happen, found call anyhow. (%s)";
    public static final String ILLEGAL_DB = "'%s' was expecting a database query to return results, found none instead. (%s)";
    public static final String ILLEGAL_CLASS = "'%s' was expected to be a model class, found a different one instead. (%s)";
    public static final String ILLEGAL_UNCACHED = "'%s' was expected to be cached, found no cache however. (%s)";
    public static final String ILLEGAL_SIMPLE_MODE = "'%s' was expected to be prepared for simple model, found expert mode configuration instead. (%s)";
    public static final String ILLEGAL_TYPE = "'%s' was expected to be of a type known to the model, found an unknown instead. (%s)";
    public static final String ILLEGAL_UNINSTALLED_ACCESS = "'%s' was expected to be installed, found nothing instead. (%s)";
    public static final String ILLEGAL_ALREADY_INSTALLED = "'%s' was expected to be not installed, found it in the model however. (%s)";
    public static final String ILLEGAL_MISSING_FILE = "'%s' was expected to be an existing file, found it missing however. (%s)";
    public static final String ILLEGAL_PACKAGE = "'%s' was expected to be a valid plugin package name, found plugin not downloadable however. (%s)";
    public static final String ILLEGAL_INTERRUPT = "'%s' was not expected while rolling out IPC, got an Interrupt however. (%s)";
    public static final String ILLEGAL_SIGBUS_INSTALL = "'%s' is not expected be re-installed without a restart, since this will cause a SIGBUS Android error. (%s)";
    public static final String ILLEGAL_MISSING_CONTEXT = "'%s' is expected to be an existing context, but was not found; likely an import error. (%s)";
    public static final String ILLEGAL_PLUGIN_FAULT = "'%s' was expected to be an installed, working plugin but failed somehow. (%s)";
    
    
    /**
     * Formats a predefined error message for object reference.
     * 
     * @param errString
     * @param refName
     * @param reference
     * @return an error message mentioning correct references.
     */
    public static String format(String errString, String refName, Object reference) {
        if (reference != null) {
            return String.format(errString, reference.toString(), refName);
        } else {
            return String.format(errString, "null", refName);
        }
    }
    
    
    /**
     * Constructs an instance of reaction with the parameters given.
     * 
     * @param reaction
     * @param formatText
     * @param referenceName
     * @param reference
     * @return
     */
    private static AssertError construct(Class<? extends AssertError> reaction, String formatText,
            String referenceName, Object reference) {
        try {
            Constructor<? extends AssertError> aec = reaction.getConstructor(String.class);
            AssertError ae = aec.newInstance(format(formatText, referenceName, reference));
            return ae;
        } catch (SecurityException e) {
            Log.e(TAG, "Was not allowed to get constructor: ", e);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "Constructor not present in AssertError descendant: ", e);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Constructor was unable to process String argument: ", e);
        } catch (InstantiationException e) {
            Log.e(TAG, "AssertError descendant was not instantiable: ", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Constructor was not accessible: ", e);
        } catch (InvocationTargetException e) {
            Log.e(TAG, "Unexpected exception ind constructor: ", e);
        }
        return new AssertError("Reflection error while trying to print error message.");
    }
    
    
    /**
     * Checks whether check is not null. Throws reaction, if check == null.
     * 
     * @param check
     * @param reaction
     */
    public static void nonNull(Object check, Class<? extends AssertError> reaction, String formatText,
            String referenceName, Object reference) {
        if (check == null) {
            AssertError ae = construct(reaction, formatText, referenceName, reference);
            Log.e(TAG, "Assertion", ae);
            throw ae;
        }
    }
    
    
    /**
     * Checks whether check is null. Throws reaction, if check != null.
     * 
     * @param check
     * @param reaction
     */
    public static void isNull(Object check, Class<? extends AssertError> reaction, String formatText,
            String referenceName, Object reference) {
        if (check != null) {
            AssertError ae = construct(reaction, formatText, referenceName, reference);
            Log.e(TAG, "Assertion", ae);
            throw ae;
        }
    }
    
    
    /**
     * Checks whether check is a valid creator. Throws reaction, if check is not.
     * 
     * @param check
     * @param reaction
     */
    public static void isValidCreator(Object check, Class<? extends AssertError> reaction, String formatText,
            String referenceName, Object reference) {
        if ((check != null) && !(check instanceof IApp) && !(check instanceof IResourceGroup)) {
            AssertError ae = construct(reaction, formatText, referenceName, reference);
            Log.e(TAG, "Assertion", ae);
            throw ae;
        }
    }
    
    
    /**
     * Checks whether check is instanceof clazz. Throws reaction, if check is not.
     * 
     * @param check
     * @param clazz
     * @param reaction
     */
    public static void instanceOf(Object check, Class<?> clazz, Class<? extends AssertError> reaction,
            String formatText, String referenceName, Object reference) {
        if (!clazz.isAssignableFrom(check.getClass())) {
            AssertError ae = construct(reaction, formatText, referenceName, reference);
            Log.e(TAG, "Assertion", ae);
            throw ae;
        }
    }
    
}
