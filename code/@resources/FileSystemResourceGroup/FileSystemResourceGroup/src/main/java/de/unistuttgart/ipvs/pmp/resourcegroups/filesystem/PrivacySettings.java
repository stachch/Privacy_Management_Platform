/*
 * Copyright 2012 pmp-android development team
 * Project: FileSystemResourceGroup
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
package de.unistuttgart.ipvs.pmp.resourcegroups.filesystem;

import android.util.Log;
import de.unistuttgart.ipvs.pmp.shared.resource.Resource;
import de.unistuttgart.ipvs.pmp.shared.resource.ResourceGroup;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.PrivacySettingValueException;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.library.BooleanPrivacySetting;

/**
 * Defines all privacy settings used by the resources for accessing the file system
 * 
 * @author Patrick Strobel
 * @version 0.3.0
 * 
 */
public class PrivacySettings {
    
    public static final String GENERIC_READ = "gen_r";
    public static final String GENERIC_WRITE = "gen_w";
    public static final String GENERIC_LIST = "gen_l";
    public static final String GENERIC_DELETE = "gen_d";
    public static final String GENERIC_MAKE_DIRS = "gen_mkdirs";
    
    /**
     * Privacy setting for reading all files stored on the SD-Card including files and directories controlled by the
     * other
     * privacy settings (e.g. data in the music directory). This means, even if <code>EXTERNAL_MUSIC_READ</code> is set
     * to
     * false and <code>EXTERNAL_BASE_DIR_READ</code> is enabled, the access to <code>Music/</code> is allowed.
     */
    public static final String EXTERNAL_BASE_DIR_READ = "ext_base_r";
    /**
     * Privacy Setting for writing files everywhere on the external SD-Card.
     * 
     * @see EXTERNAL_BASE_DIR_READ
     */
    public static final String EXTERNAL_BASE_DIR_WRITE = "ext_base_w";
    public static final String EXTERNAL_BASE_DIR_LIST = "ext_base_l";
    public static final String EXTERNAL_BASE_DIR_DELETE = "ext_base_d";
    public static final String EXTERNAL_BASE_DIR_MAKE_DIRS = "ext_base_mkdirs";
    
    /**
     * Privacy Setting for reading all files stored in the external <code>Music/</code> directory
     */
    public static final String EXTERNAL_MUSIC_READ = "ext_music_r";
    public static final String EXTERNAL_MUSIC_WRITE = "ext_music_w";
    public static final String EXTERNAL_MUSIC_LIST = "ext_music_l";
    public static final String EXTERNAL_MUSIC_DELETE = "ext_music_d";
    public static final String EXTERNAL_MUSIC_MAKE_DIRS = "ext_music_mkdirs";
    
    /**
     * Privacy Setting for reading all files stored in the external <code>Podcasts/</code> directory
     */
    public static final String EXTERNAL_PODCASTS_READ = "ext_podcasts_r";
    public static final String EXTERNAL_PODCASTS_WRITE = "ext_podcasts_w";
    public static final String EXTERNAL_PODCASTS_LIST = "ext_podcasts_l";
    public static final String EXTERNAL_PODCASTS_DELETE = "ext_podcasts_d";
    public static final String EXTERNAL_PODCASTS_MAKE_DIRS = "ext_podcasts_mkdirs";
    
    /**
     * Privacy Setting for reading all files stored in the external <code>Ringtones/</code> directory
     */
    public static final String EXTERNAL_RINGTONES_READ = "ext_ringtones_r";
    public static final String EXTERNAL_RINGTONES_WRITE = "ext_ringtones_w";
    public static final String EXTERNAL_RINGTONES_LIST = "ext_ringtones_l";
    public static final String EXTERNAL_RINGTONES_DELETE = "ext_ringtones_d";
    public static final String EXTERNAL_RINGTONES_MAKE_DIRS = "ext_ringtones_mkdirs";
    
    /**
     * Privacy Setting for reading all files stored in the external <code>Alarms/</code> directory
     */
    public static final String EXTERNAL_ALARMS_READ = "ext_alarms_r";
    public static final String EXTERNAL_ALARMS_WRITE = "ext_alarms_w";
    public static final String EXTERNAL_ALARMS_LIST = "ext_alarms_l";
    public static final String EXTERNAL_ALARMS_DELETE = "ext_alarms_d";
    public static final String EXTERNAL_ALARMS_MAKE_DIRS = "ext_alarms_mkdirs";
    
    /**
     * Privacy Setting for reading all files stored in the external <code>Notifications/</code> directory
     */
    public static final String EXTERNAL_NOTIFICATIONS_READ = "ext_notifications_r";
    public static final String EXTERNAL_NOTIFICATIONS_WRITE = "ext_notifications_w";
    public static final String EXTERNAL_NOTIFICATIONS_LIST = "ext_notifications_l";
    public static final String EXTERNAL_NOTIFICATIONS_DELETE = "ext_notifications_d";
    public static final String EXTERNAL_NOTIFICATIONS_MAKE_DIRS = "ext_notifications_mkdirs";
    
    /**
     * Privacy Setting for reading all files stored in the external <code>Pictures/</code> directory
     */
    public static final String EXTERNAL_PICTURES_READ = "ext_pictures_r";
    public static final String EXTERNAL_PICTURES_WRITE = "ext_pictures_w";
    public static final String EXTERNAL_PICTURES_LIST = "ext_pictures_l";
    public static final String EXTERNAL_PICTURES_DELETE = "ext_pictures_d";
    public static final String EXTERNAL_PICTURES_MAKE_DIRS = "ext_pictures_mkdirs";
    
    /**
     * Privacy Setting for reading all files stored in the external <code>Movies/</code> directory
     */
    public static final String EXTERNAL_MOVIES_READ = "ext_movies_r";
    public static final String EXTERNAL_MOVIES_WRITE = "ext_movies_w";
    public static final String EXTERNAL_MOVIES_LIST = "ext_movies_l";
    public static final String EXTERNAL_MOVIES_DELETE = "ext_movies_d";
    public static final String EXTERNAL_MOVIES_MAKE_DIRS = "ext_movies_mkdirs";
    
    /**
     * Privacy Setting for reading all files stored in the external <code>Download/</code> directory
     */
    public static final String EXTERNAL_DOWNLOAD_READ = "ext_download_r";
    public static final String EXTERNAL_DOWNLOAD_WRITE = "ext_download_w";
    public static final String EXTERNAL_DOWNLOAD_LIST = "ext_download_l";
    public static final String EXTERNAL_DOWNLOAD_DELETE = "ext_download_d";
    public static final String EXTERNAL_DOWNLOAD_MAKE_DIRS = "ext_download_mkdirs";
    
    private ResourceGroup rg;
    
    
    /**
     * This creates all privacy settings for reading, writing, deleting and listing files in different locations.
     * 
     * @throws Exception
     *             Thrown,
     */
    public PrivacySettings(ResourceGroup rg) {
        this.rg = rg;
    }
    
    
    /**
     * Adds all generated privacy settings to a resource-group
     * 
     * @param rg
     *            Resource-group to which the privacy settings should be added
     */
    public void registerPrivacySettings() {
        this.rg.registerPrivacySetting(GENERIC_READ, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(GENERIC_WRITE, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(GENERIC_LIST, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(GENERIC_DELETE, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(GENERIC_MAKE_DIRS, new BooleanPrivacySetting());
        
        this.rg.registerPrivacySetting(EXTERNAL_BASE_DIR_READ, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_BASE_DIR_WRITE, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_BASE_DIR_LIST, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_BASE_DIR_DELETE, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_BASE_DIR_MAKE_DIRS, new BooleanPrivacySetting());
        
        this.rg.registerPrivacySetting(EXTERNAL_MUSIC_READ, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_MUSIC_WRITE, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_MUSIC_LIST, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_MUSIC_DELETE, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_MUSIC_MAKE_DIRS, new BooleanPrivacySetting());
        
        this.rg.registerPrivacySetting(EXTERNAL_PODCASTS_READ, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_PODCASTS_WRITE, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_PODCASTS_LIST, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_PODCASTS_DELETE, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_PODCASTS_MAKE_DIRS, new BooleanPrivacySetting());
        
        this.rg.registerPrivacySetting(EXTERNAL_RINGTONES_READ, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_RINGTONES_WRITE, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_RINGTONES_LIST, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_RINGTONES_DELETE, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_RINGTONES_MAKE_DIRS, new BooleanPrivacySetting());
        
        this.rg.registerPrivacySetting(EXTERNAL_ALARMS_READ, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_ALARMS_WRITE, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_ALARMS_LIST, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_ALARMS_DELETE, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_ALARMS_MAKE_DIRS, new BooleanPrivacySetting());
        
        this.rg.registerPrivacySetting(EXTERNAL_NOTIFICATIONS_READ, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_NOTIFICATIONS_WRITE, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_NOTIFICATIONS_LIST, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_NOTIFICATIONS_DELETE, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_NOTIFICATIONS_MAKE_DIRS, new BooleanPrivacySetting());
        
        this.rg.registerPrivacySetting(EXTERNAL_PICTURES_READ, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_PICTURES_WRITE, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_PICTURES_LIST, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_PICTURES_DELETE, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_PICTURES_MAKE_DIRS, new BooleanPrivacySetting());
        
        this.rg.registerPrivacySetting(EXTERNAL_MOVIES_READ, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_MOVIES_WRITE, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_MOVIES_LIST, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_MOVIES_DELETE, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_MOVIES_MAKE_DIRS, new BooleanPrivacySetting());
        
        this.rg.registerPrivacySetting(EXTERNAL_DOWNLOAD_READ, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_DOWNLOAD_WRITE, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_DOWNLOAD_LIST, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_DOWNLOAD_DELETE, new BooleanPrivacySetting());
        this.rg.registerPrivacySetting(EXTERNAL_DOWNLOAD_MAKE_DIRS, new BooleanPrivacySetting());
    }
    
    
    /**
     * Checks if a specific privacy setting is set for an application
     * 
     * @param privacySettingName
     *            The privacy setting to check
     * @param app
     *            The app to check
     * @param resource
     *            Resource
     * @return True, if privacy setting is set for this application
     */
    public static boolean privacySettingSet(String privacySettingName, String app, Resource resource) {
        if (privacySettingName == null) {
            Log.e("PrivacySettings", "Name of the privacy setting cannot be null");
            return false;
        }
        
        BooleanPrivacySetting privacySetting = (BooleanPrivacySetting) resource.getPrivacySetting(privacySettingName);
        try {
            return privacySetting.permits(app, true);
        } catch (PrivacySettingValueException e) {
            e.printStackTrace();
        }
        return false;
    }
    
}
