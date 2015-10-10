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
package de.unistuttgart.ipvs.pmp.gui.app;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.servicefeature.AdapterServiceFeatures;
import de.unistuttgart.ipvs.pmp.gui.servicefeature.ListItemServiceFeature;
import de.unistuttgart.ipvs.pmp.gui.util.ActivityKillReceiver;
import de.unistuttgart.ipvs.pmp.gui.util.GUIConstants;
import de.unistuttgart.ipvs.pmp.gui.util.GUITools;
import de.unistuttgart.ipvs.pmp.gui.util.PMPPreferences;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.IServiceFeature;

/**
 * The {@link TabServiceFeatures} displays all Service Features which are offered by the App.
 * 
 * @author Jakob Jarosch
 */
public class TabServiceFeatures extends Activity {
    
    private static final class OpenSFDialogItemClickListener implements OnItemClickListener {
        
        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
            ((ListItemServiceFeature) view).openServiceFeatureDialog();
        }
    }
    
    /**
     * The reference to the real App in the model.
     */
    private IApp app;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.tab_app_sfs);
    }
    
    
    @Override
    protected void onResume() {
        super.onResume();
        
        addListener();
        
        try {
            checkExtendedIntentActions();
        } catch (IllegalArgumentException iae) {
            // The intent was illegal, close the activity
            finish();
        }
        
        /* Switch between Expert Mode and Normal Mode */
        TextView tvDescriptionNormalMode = (TextView) findViewById(R.id.TextView_Description_Normal);
        TextView tvDescriptionExpertMode = (TextView) findViewById(R.id.TextView_Description_Expert);
        if (PMPPreferences.getInstance().isExpertMode()) {
            tvDescriptionNormalMode.setVisibility(View.GONE);
            tvDescriptionExpertMode.setVisibility(View.VISIBLE);
        } else {
            tvDescriptionNormalMode.setVisibility(View.VISIBLE);
            tvDescriptionExpertMode.setVisibility(View.GONE);
        }
        
        /* Load the offered Service Features into the list. */
        List<IServiceFeature> sfs = this.app.getServiceFeatures();
        
        ListView serviceFeaturesView = (ListView) findViewById(R.id.ListView_SFs);
        
        serviceFeaturesView.setClickable(true);
        serviceFeaturesView.setOnItemClickListener(new OpenSFDialogItemClickListener());
        
        AdapterServiceFeatures sFsAdapter = new AdapterServiceFeatures(this, sfs);
        serviceFeaturesView.setAdapter(sFsAdapter);
    }
    
    
    private void addListener() {
        ((Button) findViewById(R.id.Button_Close)).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                ActivityKillReceiver.sendKillBroadcast(TabServiceFeatures.this);
            }
        });
    }
    
    
    /**
     * Checks if the Activity has been started with extended parameters like requested service features.
     */
    private void checkExtendedIntentActions() {
        this.app = GUITools.getIAppFromIntent(getIntent());
        if (GUITools.getIntentAction(getIntent()) != null
                && GUITools.getIntentAction(getIntent()).equals(GUIConstants.CHANGE_SERVICEFEATURE)) {
            ((Button) findViewById(R.id.Button_Close)).setVisibility(View.VISIBLE);
        }
    }
}
