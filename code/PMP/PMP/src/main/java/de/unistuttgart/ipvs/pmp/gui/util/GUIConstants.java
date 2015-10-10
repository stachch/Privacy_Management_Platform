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
package de.unistuttgart.ipvs.pmp.gui.util;

import android.graphics.Color;

/**
 * {@link GUIConstants} provides some general guidelines as consistent background colors or equal text colors.
 */
public class GUIConstants {
    
    /**
     * Identifiers
     */
    public static final String APP_IDENTIFIER = "appIdentifier";
    public static final String PRESET_IDENTIFIER = "presetIdentifier";
    
    /**
     * Text colors
     */
    public static final int COLOR_TEXT_GRAYED_OUT = Color.GRAY;
    
    /**
     * Background colors
     */
    public static final int COLOR_BG_GREEN = Color.parseColor("#004800");
    public static final int COLOR_BG_RED = Color.parseColor("#480000");
    public static final int COLOR_BG_GRAY = Color.parseColor("#333333");
    
    /**
     * Intent Actions
     */
    public static final String ACTIVITY_ACTION = "activityAction";
    public static final String CHANGE_SERVICEFEATURE = "changeServiceFeature";
    public static final String FILTER_AVAILABLE_RGS = "filterRGs";
    public static final String DOWNLOAD_PRESET_SET = "downloadPresetSet";
    
    /**
     * Intent Parameters
     */
    public static final String REQUIRED_SERVICE_FEATURE = "requiredServiceFeature";
    public static final String RGS_FILTER = "rgsFilter";
    public static final String PRESET_SET_ID = "presetSetId";
    
}
