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
package de.unistuttgart.ipvs.pmp.gui.setting;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ListView;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.util.ActivityKillReceiver;
import de.unistuttgart.ipvs.pmp.gui.util.PMPPreferences;
import de.unistuttgart.ipvs.pmp.model.activity.LongTaskProgressDialog;
import de.unistuttgart.ipvs.pmp.util.FileLog;

/**
 * The {@link ActivitySettings} enables the user to select between the expert mode and the normal mode.
 * In the export mode the presets feature is enabled.
 * 
 * @author Jakob Jarosch, Marcus Vetter
 */
public class ActivitySettings extends Activity {
    
    private final class ExpertModeSettingEvaluator implements ISettingEvaluator<Boolean> {
        
        @Override
        public void setValue(Boolean newValue) {
            
            ProgressDialog pd = new ProgressDialog(ActivitySettings.this);
            pd.setTitle(R.string.expert_mode);
            pd.setCancelable(false);
            LongTaskProgressDialog<Boolean, Void, Void> ltpd = new LongTaskProgressDialog<Boolean, Void, Void>(pd) {
                
                @Override
                public Void run(Boolean... params) {
                    PMPPreferences.getInstance().setExpertMode(params[0]);
                    return null;
                };
            };
            
            ltpd.execute(newValue);
            
        }
        
        
        @Override
        public Boolean getValue() {
            return PMPPreferences.getInstance().isExpertMode();
        }
    }
    
    private final static class LogGranularityEvaluator implements ISettingEvaluator<Boolean> {
        
        private int level;
        
        
        public LogGranularityEvaluator(int level) {
            this.level = level;
        }
        
        
        @Override
        public Boolean getValue() {
            return (PMPPreferences.getInstance().getLoggingGranularity() & this.level) > 0;
        }
        
        
        @Override
        public void setValue(Boolean newValue) {
            int granularities = PMPPreferences.getInstance().getLoggingGranularity();
            
            if (newValue) {
                granularities |= this.level;
            } else {
                granularities &= ~this.level;
            }
            
            PMPPreferences.getInstance().setLoggingGranularity(granularities);
        }
    }
    
    /**
     * ListView of all Settings
     */
    private ListView settingsListView;
    
    /**
     * List of all Settings
     */
    private List<SettingAbstract<?>> settingsList = new ArrayList<SettingAbstract<?>>();
    
    /**
     * The {@link ActivityKillReceiver}.
     */
    private ActivityKillReceiver akr;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set the Activity Layout
        setContentView(R.layout.activity_settings);
        
        // Get the ListView
        this.settingsListView = (ListView) findViewById(R.id.ListView_Settings);
        
        // Instantiate and add the adapter
        SettingsAdapter settingsAdapter = new SettingsAdapter(this, this.settingsList);
        this.settingsListView.setAdapter(settingsAdapter);
        
        // Add all available Settings
        addSettings(settingsAdapter);
        
        /* Initiating the ActivityKillReceiver. */
        this.akr = new ActivityKillReceiver(this);
    }
    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        unregisterReceiver(this.akr);
    }
    
    
    /**
     * Add all Settings to the settingsList
     */
    private void addSettings(SettingsAdapter sa) {
        // add the ExpertMode-SettingCheckBox
        this.settingsList.add(new SettingCheckBox(sa, R.string.expert_mode, R.string.settings_expertmode_description,
                R.drawable.icon_expertmode, new ExpertModeSettingEvaluator()));
        
        // add the log granularities
        this.settingsList.add(new SettingCheckBox(sa, R.string.settings_log_granularity_component_change,
                R.string.settings_log_granularity_component_change_desc, R.drawable.icon_edit,
                new LogGranularityEvaluator(FileLog.GRANULARITY_COMPONENT_CHANGES)));
        
        this.settingsList.add(new SettingCheckBox(sa, R.string.settings_log_granularity_setting_change,
                R.string.settings_log_granularity_setting_change_desc, R.drawable.icon_sfs,
                new LogGranularityEvaluator(FileLog.GRANULARITY_SETTING_CHANGES)));
        
        this.settingsList.add(new SettingCheckBox(sa, R.string.settings_log_granularity_context_change,
                R.string.settings_log_granularity_context_change_desc, R.drawable.contexts_location_icon,
                new LogGranularityEvaluator(FileLog.GRANULARITY_CONTEXT_CHANGES)));
        
        this.settingsList.add(new SettingCheckBox(sa, R.string.settings_log_granularity_setting_request,
                R.string.settings_log_granularity_setting_request_desc, R.drawable.icon_statistics,
                new LogGranularityEvaluator(FileLog.GRANULARITY_SETTING_REQUESTS)));
    }
    
}
