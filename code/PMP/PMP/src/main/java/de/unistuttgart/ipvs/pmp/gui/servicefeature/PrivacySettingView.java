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
package de.unistuttgart.ipvs.pmp.gui.servicefeature;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.privacysetting.DialogPrivacySettingInformation;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.IServiceFeature;
import de.unistuttgart.ipvs.pmp.shared.Log;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.PrivacySettingValueException;

/**
 * The {@link PrivacySettingView} displays basic informations about an Privacy Setting (name, value, statisfied or not).
 * 
 * @author Jakob Jarosch
 */
public class PrivacySettingView extends LinearLayout {
    
    /**
     * The Service Feature which uses the Privacy Setting. It is used to display the satisfaction of the current setting
     * in the Presets.
     */
    public IServiceFeature serviceFeature;
    
    /**
     * The reference to the real Privacy Setting in the model.
     */
    public IPrivacySetting privacySetting;
    
    
    /**
     * Creates a new {@link PrivacySettingView} with the given {@link IServiceFeature} and {@link IPrivacySetting} as
     * display configuration.
     * 
     * @param context
     *            The context of the Activity which invoked the dialog.
     * @param serviceFeature
     *            The Service Feature which uses the {@link IPrivacySetting}
     * @param privacySetting
     *            The Privacy Setting which should be displayed
     */
    @SuppressLint("InflateParams")
    @SuppressWarnings("deprecation")
    public PrivacySettingView(Context context, IServiceFeature serviceFeature, IPrivacySetting privacySetting) {
        super(context);
        
        this.serviceFeature = serviceFeature;
        this.privacySetting = privacySetting;
        
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.listitem_app_ps, null);
        addView(v);
        
        setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
                
        addListener();
        
        refresh();
    }
    
    
    /**
     * Refreshes all displayed contents in the view.
     */
    @SuppressWarnings("deprecation")
    public void refresh() {
        TextView tvTitle = (TextView) findViewById(R.id.TextView_Title);
        TextView tvDescription = (TextView) findViewById(R.id.TextView_Description);
        ImageView stateImage = (ImageView) findViewById(R.id.ImageView_State);
        
        // Update name
        tvTitle.setText(Html.fromHtml("<b>" + this.privacySetting.getResourceGroup().getName() + " - <i>"
                + this.privacySetting.getName() + "</i></b>"));
                
        // Update description
        try {
            tvDescription.setText(getContext().getResources().getString(R.string.required_value) + ": "
                    + this.privacySetting.getHumanReadableValue(
                            this.serviceFeature.getRequiredPrivacySettingValue(this.privacySetting)));
        } catch (PrivacySettingValueException e) {
            Log.e(this,
                    "The Privacy Setting '" + this.privacySetting.getName() + "' of Service Feature '"
                            + this.serviceFeature.getName() + " (" + this.serviceFeature.getApp().getName()
                            + ")' has an invalid value set '"
                            + this.serviceFeature.getRequiredPrivacySettingValue(this.privacySetting) + "'",
                    e);
                    
            tvDescription.setText(Html.fromHtml("<span style=\"color:red;\">"
                    + getContext().getResources().getString(R.string.ps_invalid_value) + "</span>"));
        }
        
        // Try to updated the displayed Privacy Settings state (tick or cross).
        try {
            String assignedPSValue = this.serviceFeature.getApp()
                    .getBestAssignedPrivacySettingValue(this.privacySetting);
                    
            if (this.privacySetting.permits(this.serviceFeature.getRequiredPrivacySettingValue(this.privacySetting),
                    assignedPSValue)) {
                stateImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_success));
            } else {
                stateImage.setImageDrawable(getResources().getDrawable(R.drawable.icon_delete));
            }
        } catch (PrivacySettingValueException e) {
            Log.e(this, "A given Privacy Setting could not be compared with another one (ps.permits() failed).", e);
        }
    }
    
    
    /**
     * Adds the listeners to the GUI components.
     */
    private void addListener() {
        setClickable(true);
        setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                new DialogPrivacySettingInformation(getContext(), PrivacySettingView.this.privacySetting).show();
            }
        });
    }
}
