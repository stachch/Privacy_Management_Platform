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

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.util.GUIConstants;
import de.unistuttgart.ipvs.pmp.gui.util.GUITools;
import de.unistuttgart.ipvs.pmp.gui.util.PMPPreferences;
import de.unistuttgart.ipvs.pmp.gui.util.dialog.DialogLongRunningTask;
import de.unistuttgart.ipvs.pmp.gui.util.model.ModelProxy;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.IServiceFeature;
import de.unistuttgart.ipvs.pmp.model.simple.SimpleModel;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.PrivacySettingValueException;

/**
 * Displays the some Basic informations (name, description, state) about a Service Feature.
 * 
 * @author Jakob Jarosch
 */
public class ListItemServiceFeature extends LinearLayout {
    
    /**
     * The reference to the real Service Feature in the model.
     */
    protected IServiceFeature serviceFeature;
    
    protected AdapterServiceFeatures adapterServiceFeatures;
    
    private Activity activity;
    
    
    /**
     * Creates a new {@link ListItemServiceFeature}.
     * 
     * @param context
     *            The context of the {@link Activity} which creates this view
     * @param serviceFeature
     *            The corresponding {@link IServiceFeature}
     * @param adapterServiceFeatures
     *            Can be set to false it is not required to update all other Service Feature views on a change.
     */
    public ListItemServiceFeature(Activity activity, IServiceFeature serviceFeature,
            AdapterServiceFeatures adapterServiceFeatures) {
        super(activity);
        
        this.activity = activity;
        this.serviceFeature = serviceFeature;
        this.adapterServiceFeatures = adapterServiceFeatures;
        
        if (!isInEditMode()) {
            /* Not in edit mode, load the xml-layout. */
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            View v = layoutInflater.inflate(R.layout.listitem_app_sf, null);
            addView(v);
            
            setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
            
            addListener();
            
            refresh();
        } else {
            /* In edit mode, just load a very basic representation of the real contents. */
            setOrientation(LinearLayout.VERTICAL);
            
            TextView tv = new TextView(getContext());
            tv.setText("[EditViewMode] ListItemServiceFeature");
            tv.setPadding(5, 10, 5, 10);
            addView(tv);
        }
    }
    
    
    /**
     * Updates all displayed informations of the {@link ListItemServiceFeature}.
     * Informations are directly fetched from the model.
     */
    public void refresh() {
        refresh(true);
    }
    
    
    /**
     * Normally just use the method without a parameter.
     * 
     * @param recursive
     *            If it is set to true, all other element in the List will be refreshed. If set to false, only this item
     *            will be refreshed.
     */
    public void refresh(boolean recursive) {
        TextView tvName = (TextView) findViewById(R.id.TextView_Name);
        TextView tvDescription = (TextView) findViewById(R.id.TextView_Description);
        CheckBox cb = (CheckBox) findViewById(R.id.CheckBox_SFState);
        View highlightBox = findViewById(R.id.View_HighlightBox);
        
        // Update name
        if (tvName != null) {
            tvName.setText(this.serviceFeature.getName());
        }
        
        // Update description
        if (tvDescription != null) {
            tvDescription.setText(this.serviceFeature.getDescription());
        }
        
        // Update Checkbox
        if (cb != null) {
            cb.setChecked(this.serviceFeature.isActive());
            cb.setEnabled(this.serviceFeature.isAvailable());
            
            if (!PMPPreferences.getInstance().isExpertMode()) {
                cb.setVisibility(View.VISIBLE);
            } else {
                cb.setVisibility(View.INVISIBLE);
            }
            
            if (this.serviceFeature.isActive() && this.serviceFeature.isAvailable()) {
                setBackgroundColor(GUIConstants.COLOR_BG_GREEN);
            } else if (!this.serviceFeature.isActive() && this.serviceFeature.isAvailable()) {
                setBackgroundColor(GUIConstants.COLOR_BG_RED);
            } else {
                setBackgroundColor(GUIConstants.COLOR_BG_GRAY);
            }
        }
        
        // Update highlight box
        if (highlightBox != null) {
            if (GUITools.isServiceFeatureRequested(this.activity.getIntent(), this.serviceFeature)) {
                highlightBox.setVisibility(View.VISIBLE);
            } else {
                highlightBox.setVisibility(View.INVISIBLE);
            }
        }
        
        /* When the flag is set all other elements in the list will be also refreshed. */
        if (recursive && this.adapterServiceFeatures != null) {
            this.adapterServiceFeatures.refreshAllViews();
        }
    }
    
    
    /**
     * Adds the listeners to all components in the view.
     */
    private void addListener() {
        // Listener of the check box
        ((CheckBox) findViewById(R.id.CheckBox_SFState)).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                boolean newState = ((CheckBox) v).isChecked();
                
                reactOnChange(newState);
            }
        });
    }
    
    
    /**
     * Method is invoked when the state of the Service Feature has changed.
     * 
     * @param newState
     *            The new State of the Service Feature.
     */
    public void reactOnChange(final boolean newState) {
        Runnable runnable = new Runnable() {
            
            @Override
            public void run() {
                try {
                    SimpleModel.getInstance().setServiceFeatureActive(ModelProxy.get(),
                            ListItemServiceFeature.this.serviceFeature, newState);
                } catch (PrivacySettingValueException e) {
                    Log.e(ListItemServiceFeature.this, "Couldn't set Service Feature as active, PSVE", e);
                    GUITools.showToast(getContext(), getContext().getString(R.string.failure_active_sf),
                            Toast.LENGTH_LONG);
                }
                
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    
                    @Override
                    public void run() {
                        refresh();
                    }
                });
                
            }
        };
        
        new DialogLongRunningTask(getContext(), runnable).setTitle("Changing Service Feature")
                .setMessage("Please wait a moment until the Service Feature has been changed.").start();
        
    }
    
    
    /**
     * The method is can be called when the details dialog should be opened.
     */
    public void openServiceFeatureDialog() {
        new DialogServiceFeature(ListItemServiceFeature.this.getContext(), ListItemServiceFeature.this.serviceFeature,
                ListItemServiceFeature.this).show();
    }
}
