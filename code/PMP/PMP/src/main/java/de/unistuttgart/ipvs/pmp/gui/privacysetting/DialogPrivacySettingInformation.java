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
package de.unistuttgart.ipvs.pmp.gui.privacysetting;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.shared.gui.view.BasicTitleView;

/**
 * {@link DialogPrivacySettingInformation} shows all details about a {@link IPrivacySetting}.
 * 
 * @author Jakob Jarosch
 */
public class DialogPrivacySettingInformation extends Dialog {
    
    /**
     * The {@link IPrivacySetting} which is represented by the {@link Dialog}.
     */
    private IPrivacySetting privacySetting;
    
    
    /**
     * Creates a new {@link DialogPrivacySettingInformation}.
     * 
     * @param context
     *            {@link Context} for {@link Dialog} creation.
     * @param privacySetting
     *            {@link IPrivacySetting} which is represented by the {@link Dialog}.
     */
    public DialogPrivacySettingInformation(Context context, IPrivacySetting privacySetting) {
        super(context);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_privacysetting);
        
        this.privacySetting = privacySetting;
        
        refresh();
        addListener();
    }
    
    
    /**
     * Updates the UI.
     */
    private void refresh() {
        /*
         * Updates the Title with the privacy name.
         */
        ((BasicTitleView) findViewById(R.id.Title)).setTitle(this.privacySetting.getName());
        Drawable icon = this.privacySetting.getResourceGroup().getIcon();
        if (icon != null) {
            ((BasicTitleView) findViewById(R.id.Title)).setIcon(icon);
        }
        
        /*
         * Update Description, Resource Group name and identifier.
         */
        ((TextView) findViewById(R.id.TextView_Description)).setText(this.privacySetting.getDescription());
        ((TextView) findViewById(R.id.TextView_RG_Name)).setText(this.privacySetting.getResourceGroup().getName());
        ((TextView) findViewById(R.id.TextView_Identifier)).setText(this.privacySetting.getIdentifier());
    }
    
    
    /**
     * Add listeners to clickable UI elements.
     */
    private void addListener() {
        ((Button) findViewById(R.id.Button_Close)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                DialogPrivacySettingInformation.this.dismiss();
            }
        });
    }
}
