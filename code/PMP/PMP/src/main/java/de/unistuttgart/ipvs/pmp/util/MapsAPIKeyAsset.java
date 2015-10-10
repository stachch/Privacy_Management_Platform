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
package de.unistuttgart.ipvs.pmp.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;
import de.unistuttgart.ipvs.pmp.Log;

/**
 * Utility to get a Maps API key using an Asset.
 * 
 * @author Tobias Kuhn
 * 
 */
public class MapsAPIKeyAsset {
    
    private static final String TAG = "MapsAPIKeyAsset";
    
    private static final String FILE_NAME = "mapsKey.properties";
    private static final String PROPERTY_NAME = "mapsKey";
    
    
    /**
     * 
     * @param context
     * @return the key stored in the mapsKey file, or null, if an error occured
     */
    public static String getKey(Context context) {
        try {
            InputStream is = context.getAssets().open(FILE_NAME);
            Properties p = new Properties();
            p.load(is);
            return p.getProperty(PROPERTY_NAME);
            
        } catch (IOException ioe) {
            Log.e(TAG, "Could not get the API key from " + PROPERTY_NAME + " in assets/" + FILE_NAME
                    + ". Does it exist?", ioe);
            return null;
        }
    }
}
