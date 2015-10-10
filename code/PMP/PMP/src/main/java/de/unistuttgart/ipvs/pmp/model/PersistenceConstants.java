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
package de.unistuttgart.ipvs.pmp.model;

/**
 * A class that stores all the persistence strings as constants. Turned out to be an interface, so you can easily add it
 * inside a {@link PersistenceProvider} class via inheritance.
 * 
 * @author Tobias Kuhn
 * 
 */
public interface PersistenceConstants {
    
    /**
     * table storing all the apps
     */
    static final String TBL_APP = "App";
    
    /**
     * table storing all the service features
     */
    static final String TBL_SERVICEFEATURE = "ServiceFeature";
    
    /**
     * table storing all the resource groups
     */
    static final String TBL_RESOURCEGROUP = "ResourceGroup";
    
    /**
     * table storing all the privacy settings
     */
    static final String TBL_PRIVACYSETTING = "PrivacySetting";
    
    /**
     * table storing all the presets
     */
    static final String TBL_PRESET = "Preset";
    
    /**
     * table storing the required privacy setting values for a service feature
     */
    static final String TBL_SFReqPSValue = "ServiceFeature_RequiredPrivacySettingValue";
    
    /**
     * table storing the granted privacy setting values for a preset
     */
    static final String TBL_GrantPSValue = "Preset_GrantedPrivacySettingValue";
    
    /**
     * table storing the assigned apps for a preset
     */
    static final String TBL_PresetAssignedApp = "Preset_AssignedApp";
    
    /**
     * table storing the context annotations for a preset
     */
    static final String TBL_CONTEXT_ANNOTATIONS = "Context_AnnotatedPrivacySettingValue";
    
    /*
     * identifying columns
     */
    static final String PACKAGE = "Package";
    static final String IDENTIFIER = "Identifier";
    static final String CREATOR = "Creator";
    static final String PRESET_PRIVACY_SETTING_ANNOTATION_ID = "PresetPrivacySettingAnnotationID";
    
    /*
     * referencing, identifying columns
     */
    static final String APP_PACKAGE = TBL_APP + PACKAGE;
    static final String RESOURCEGROUP_PACKAGE = TBL_RESOURCEGROUP + PACKAGE;
    static final String PRIVACYSETTING_RESOURCEGROUP_PACKAGE = TBL_PRIVACYSETTING + TBL_RESOURCEGROUP + PACKAGE;
    static final String PRIVACYSETTING_IDENTIFIER = TBL_PRIVACYSETTING + IDENTIFIER;
    static final String SERVICEFEATURE_APP_PACKAGE = TBL_SERVICEFEATURE + TBL_APP + PACKAGE;
    static final String SERVICEFEATURE_IDENTIFIER = TBL_SERVICEFEATURE + IDENTIFIER;
    static final String PRESET_CREATOR = TBL_PRESET + CREATOR;
    static final String PRESET_IDENTIFIER = TBL_PRESET + IDENTIFIER;
    
    /* 
     * data columns
     */
    static final String GRANTEDVALUE = "GrantedValue";
    static final String REQUIREDVALUE = "RequiredValue";
    static final String NAME = "Name";
    static final String DESCRIPTION = "Description";
    static final String DELETED = "Deleted";
    static final String CONTEXT_TYPE = "ContextType";
    static final String CONTEXT_CONDITION = "ContextCondition";
    static final String OVERRIDE_GRANTED_VALUE = "OverrideGrantedValue";
    static final String REQUESTABLE = "Requestable";
    
    /*
     * meta data constants
     */
    
    /**
     * constant that should never appear in package names.
     */
    static final String PACKAGE_SEPARATOR = ":";
    
    /**
     * The XML file name for apps.
     */
    public static final String APP_XML_NAME = "ais.xml";
    
    /**
     * the implicit mode-privacy setting name for mock/cloak functionality
     */
    public static final String MODE_PRIVACY_SETTING = "Mode";
}
