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
package de.unistuttgart.ipvs.pmp.api.gui.servicefeature;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.api.handler._default.PMPDefaultRequestSFHandler;

public class ServiceFeatureDialog extends Dialog {
    
    protected PMPDefaultRequestSFHandler defaultRegistrationHandler;
    
    
    public ServiceFeatureDialog(Activity activity, PMPDefaultRequestSFHandler defaultRegistrationHandler) {
        super(activity);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.pmp_api_dialog_servicefeature);
        
        setCancelable(false);
        
        this.defaultRegistrationHandler = defaultRegistrationHandler;
        
        addListener();
    }
    
    
    private void addListener() {
        ((Button) findViewById(R.id.Button_Continue)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                ServiceFeatureDialog.this.dismiss();
                ServiceFeatureDialog.this.defaultRegistrationHandler.unblockHandler();
            }
        });
        
        ((Button) findViewById(R.id.Button_Close)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                ServiceFeatureDialog.this.dismiss();
                ServiceFeatureDialog.this.defaultRegistrationHandler.killServiceFeatureRequest();
                ServiceFeatureDialog.this.defaultRegistrationHandler.unblockHandler();
            }
        });
    }
}
