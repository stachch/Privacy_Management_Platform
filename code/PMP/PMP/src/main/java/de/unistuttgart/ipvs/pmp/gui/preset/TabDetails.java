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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.util.GUIConstants;
import de.unistuttgart.ipvs.pmp.gui.util.GUITools;
import de.unistuttgart.ipvs.pmp.gui.util.RGInstaller;
import de.unistuttgart.ipvs.pmp.gui.util.model.ModelProxy;
import de.unistuttgart.ipvs.pmp.model.element.contextannotation.IContextAnnotation;
import de.unistuttgart.ipvs.pmp.model.element.missing.MissingApp;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;

public class TabDetails extends Activity {
    
    protected IPreset preset;
    
    private DialogPresetEdit.ICallback callback = new DialogPresetEdit.ICallback() {
        
        @Override
        public void refresh() {
            TabDetails.this.refresh();
        }
        
        
        @Override
        public void openPreset(IPreset preset) {
            // Do nothing
        }
    };
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Get the preset
        String presetIdentifier = super.getIntent().getStringExtra(GUIConstants.PRESET_IDENTIFIER);
        this.preset = ModelProxy.get().getPreset(null, presetIdentifier);
        
        // Set view
        setContentView(R.layout.tab_preset_details);
        
        addListener();
    }
    
    
    @Override
    protected void onResume() {
        super.onResume();
        
        refresh();
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.preset_tab_details_change_description:
                new DialogPresetEdit(this, this.preset, this.callback).show();
                break;
            
            case R.id.preset_tab_details_remove:
                this.preset.setDeleted(true);
                finish();
                break;
        }
        
        return true;
    }
    
    
    /**
     * Create the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.preset_menu_details_tab, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    
    protected void refresh() {
        if (getParent() != null && getParent() instanceof ActivityPreset) {
            ((ActivityPreset) getParent()).refresh();
        }
        
        int contextCount = 0;
        int activeContextCount = 0;
        
        for (IContextAnnotation ca : ModelProxy.get().getContextAnnotations()) {
            if (ca.getPreset().equals(this.preset)) {
                contextCount++;
                if (ca.isActive()) {
                    activeContextCount++;
                }
            }
        }
        
        ((TextView) findViewById(R.id.TextView_Description)).setText(this.preset.getDescription());
        ((TextView) findViewById(R.id.TextView_Statistics)).setText(Html.fromHtml("<html><b>Assigned Apps:</b> "
                + this.preset.getAssignedApps().size() + "<br/>" + "<b>Assigned Privacy Settings:</b> "
                + this.preset.getGrantedPrivacySettings().size() + "<br/><br/>" + "<b>Used Contexts:</b> "
                + contextCount + "<br/>" + "<b>Active Contexts:</b> " + activeContextCount + "<br/><br/>"
                + "<b>Missing Apps:</b> " + this.preset.getMissingApps().size() + "<br/>"
                + "<b>Missing Resource Groups:</b> " + RGInstaller.getMissingResourceGroups(this.preset).length
                + "</html>"));
        
        // TODO Implement the context count.
        
        LinearLayout buttonContainer = (LinearLayout) findViewById(R.id.LinearLayout_Problems);
        Button oneClickInstall = (Button) findViewById(R.id.Button_OneClickInstall);
        Button viewMissingRGs = (Button) findViewById(R.id.Button_ViewMissingRGs);
        Button removeMissingApps = (Button) findViewById(R.id.Button_RemoveMissingApps);
        if (this.preset.getMissingApps().size() > 0 || RGInstaller.getMissingResourceGroups(this.preset).length > 0) {
            
            buttonContainer.setVisibility(View.VISIBLE);
            
            if (this.preset.getMissingApps().size() > 0) {
                removeMissingApps.setEnabled(true);
            } else {
                removeMissingApps.setEnabled(false);
            }
            
            if (RGInstaller.getMissingResourceGroups(this.preset).length > 0) {
                oneClickInstall.setEnabled(true);
                viewMissingRGs.setEnabled(true);
            } else {
                oneClickInstall.setEnabled(false);
                viewMissingRGs.setEnabled(false);
            }
        } else {
            buttonContainer.setVisibility(View.GONE);
        }
    }
    
    
    private void addListener() {
        Button oneClickInstall = (Button) findViewById(R.id.Button_OneClickInstall);
        oneClickInstall.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                String[] missingRGs = RGInstaller.getMissingResourceGroups(TabDetails.this.preset);
                RGInstaller.installResourceGroups(TabDetails.this, missingRGs, new RGInstaller.ICallback() {
                    
                    @Override
                    public void callback() {
                        refresh();
                    }
                });
            }
        });
        
        Button viewMissingRGs = (Button) findViewById(R.id.Button_ViewMissingRGs);
        viewMissingRGs.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                String[] missingRGs = RGInstaller.getMissingResourceGroups(TabDetails.this.preset);
                Intent intent = GUITools.createRgFilterIntent(missingRGs);
                startActivity(intent);
            }
        });
        
        Button removeMissingApps = (Button) findViewById(R.id.Button_RemoveMissingApps);
        removeMissingApps.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                for (MissingApp app : TabDetails.this.preset.getMissingApps()) {
                    TabDetails.this.preset.removeMissingApp(app);
                }
            }
        });
        
    }
}
