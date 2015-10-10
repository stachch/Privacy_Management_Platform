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
package de.unistuttgart.ipvs.pmp.gui.preset;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.util.model.ModelProxy;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;

/**
 * Dialog for assigning Apps to the Preset
 * 
 * @author Marcus Vetter
 */
public class DialogAppsAssign extends Dialog {
    
    /**
     * The button to confirm the dialog
     */
    private Button confirm;
    
    /**
     * The button to cancel the dialog
     */
    private Button cancel;
    
    /**
     * The PresetAppsTab
     */
    private TabApps activity;
    
    /**
     * The Preset
     */
    protected IPreset preset;
    
    /**
     * The instance of the adapter
     */
    protected AdapterAppsAssign appsAdapter;
    
    
    /**
     * Necessary constructor
     * 
     * @param context
     *            the context
     * @param preset
     *            the Preset
     */
    public DialogAppsAssign(Context context, IPreset preset) {
        super(context);
        this.preset = preset;
        this.activity = (TabApps) context;
    }
    
    
    /**
     * Called when the dialog is first created. Gets all elements of the gui
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.dialog_preset_assign_apps);
        
        this.confirm = (Button) findViewById(R.id.presets_dialog_confirm);
        this.cancel = (Button) findViewById(R.id.presets_dialog_cancel);
        
        this.confirm.setOnClickListener(new ConfirmListener(this.activity));
        this.cancel.setOnClickListener(new CancelListener());
        
        // Apps
        List<IApp> apps = calcDisplayApps();
        
        ListView appsList = (ListView) findViewById(R.id.listview_assigned_apps);
        appsList.setClickable(true);
        
        this.appsAdapter = new AdapterAppsAssign(this.activity, apps);
        appsList.setAdapter(this.appsAdapter);
        
    }
    
    
    /**
     * Calc Apps to display = All registered Apps without assigned Apps
     * 
     * @return Apps to display
     */
    public List<IApp> calcDisplayApps() {
        List<IApp> allAppsList = ModelProxy.get().getApps();
        List<IApp> allAssignedAppsList = this.preset.getAssignedApps();
        List<IApp> displayList = new ArrayList<IApp>();
        
        AllAppsLoop: for (IApp app : allAppsList) {
            for (IApp assignedApp : allAssignedAppsList) {
                if (app.equals(assignedApp)) {
                    continue AllAppsLoop;
                }
            }
            displayList.add(app);
            
        }
        
        return displayList;
    }
    
    /**
     * Listener class needed for the confirm button
     * 
     */
    private class ConfirmListener implements android.view.View.OnClickListener {
        
        /**
         * The PresetAppsTabActivity
         */
        private TabApps activity;
        
        
        public ConfirmListener(TabApps activity) {
            this.activity = activity;
        }
        
        
        @Override
        public void onClick(View v) {
            
            // Store
            if (DialogAppsAssign.this.appsAdapter != null) {
                for (IApp app : DialogAppsAssign.this.appsAdapter.getCheckBoxMap().keySet()) {
                    if (DialogAppsAssign.this.appsAdapter.getCheckBoxMap().get(app)) {
                        DialogAppsAssign.this.preset.assignApp(app);
                    }
                }
            }
            this.activity.updateList();
            
            // Dismiss
            dismiss();
            
        }
        
    }
    
    /**
     * Listener class needed for the cancel button
     * 
     */
    private class CancelListener implements android.view.View.OnClickListener {
        
        @Override
        public void onClick(View v) {
            // Dismiss
            dismiss();
        }
        
    }
    
}
