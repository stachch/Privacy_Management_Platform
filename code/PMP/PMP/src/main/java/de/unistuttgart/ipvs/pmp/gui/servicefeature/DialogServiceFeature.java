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

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.preset.DialogPresetEdit;
import de.unistuttgart.ipvs.pmp.gui.util.GUITools;
import de.unistuttgart.ipvs.pmp.gui.util.PMPPreferences;
import de.unistuttgart.ipvs.pmp.gui.util.RGInstaller;
import de.unistuttgart.ipvs.pmp.gui.util.model.ModelProxy;
import de.unistuttgart.ipvs.pmp.gui.view.BasicTitleView;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.IServiceFeature;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.PrivacySettingValueException;

/**
 * The {@link DialogServiceFeature} displays details about a {@link IServiceFeature}.
 * The User gets the name, a description and all required Privacy Settings.
 * For each Privacy Setting is also the required value (in a human readable representation)
 * and the current state (satisfied or not) shown.
 * 
 * @author Jakob Jarosch
 */
public class DialogServiceFeature extends Dialog {
    
    /**
     * The Service Feature which is displayed in the dialog.
     */
    protected IServiceFeature serviceFeature;
    
    /**
     * The View of the Service Feature (in the Service Feature list) which corresponds to the current displayed one.
     */
    protected ListItemServiceFeature serviceFeatureView = null;
    
    /**
     * Privacy Setting container.
     */
    private LinearLayout psContainer;
    
    /**
     * Resource Group information Container.
     */
    private LinearLayout rgContainer;
    
    
    /**
     * @see DialogServiceFeature#ServiceFeatureDialog(Context, IServiceFeature, ListItemServiceFeature)
     */
    public DialogServiceFeature(Context context, IServiceFeature serviceFeature) {
        this(context, serviceFeature, null);
    }
    
    
    /**
     * Creates a new {@link DialogServiceFeature}.
     * 
     * @param context
     *            the {@link Context} is required for Dialog creation
     * @param serviceFeature
     *            the {@link IServiceFeature} which should be displayed
     * @param serviceFeatureView
     *            the corresponding view of the {@link IServiceFeature} in the {@link ListView}.
     */
    public DialogServiceFeature(Context context, IServiceFeature serviceFeature,
            ListItemServiceFeature serviceFeatureView) {
        super(context);
        
        this.serviceFeature = serviceFeature;
        this.serviceFeatureView = serviceFeatureView;
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.dialog_sf);
        
        BasicTitleView btv = (BasicTitleView) findViewById(R.id.Title);
        btv.setTitle(String.format(getContext().getResources().getString(R.string.service_feature_title),
                this.serviceFeature.getName()));
        
        TextView descriptionTv = (TextView) findViewById(R.id.TextView_Description);
        descriptionTv.setText(serviceFeature.getDescription());
        
        this.psContainer = (LinearLayout) findViewById(R.id.LinearLayout_Container_PS_Information);
        this.rgContainer = (LinearLayout) findViewById(R.id.LinearLayout_Container_RG_Information);
        
        refresh();
        
        addListener();
    }
    
    
    public void refresh() {
        /* Fill in all PrivacySettings */
        LinearLayout psLayout = (LinearLayout) findViewById(R.id.LinearLayout_PrivacySettings);
        psLayout.removeAllViews();
        
        for (IPrivacySetting privacySetting : this.serviceFeature.getRequiredPrivacySettings()) {
            psLayout.addView(new PrivacySettingView(getContext(), this.serviceFeature, privacySetting));
        }
        
        /* Update the Buttons */
        Button enableDisableButton = (Button) findViewById(R.id.Button_EnableDisable);
        Button showMissingRGs = (Button) findViewById(R.id.Button_ViewMissingRGs);
        Button oneClickInstallRGs = (Button) findViewById(R.id.Button_OneClickInstall);
        Button createNewPreset = (Button) findViewById(R.id.Button_CreateNewPreset);
        Button addToPreset = (Button) findViewById(R.id.Button_AddToPreset);
        
        /* Update the label of the enable/disable button */
        if (this.serviceFeature.isActive()) {
            enableDisableButton.setText(getContext().getResources().getString(R.string.disable));
        } else {
            enableDisableButton.setText(getContext().getResources().getString(R.string.enable));
        }
        
        /* Disable all Buttons and enable them by request */
        enableDisableButton.setVisibility(View.GONE);
        showMissingRGs.setVisibility(View.GONE);
        oneClickInstallRGs.setVisibility(View.GONE);
        createNewPreset.setVisibility(View.GONE);
        addToPreset.setVisibility(View.GONE);
        
        /* Now enable the buttons by request. */
        
        /* Select between available Service Feature and unavailable*/
        if (this.serviceFeature.isAvailable()) {
            this.psContainer.setVisibility(View.VISIBLE);
            this.rgContainer.setVisibility(View.GONE);
            
            /* Select between expert mode and simple mode */
            if (PMPPreferences.getInstance().isExpertMode()) {
                createNewPreset.setVisibility(View.VISIBLE);
                addToPreset.setVisibility(View.VISIBLE);
            } else {
                enableDisableButton.setVisibility(View.VISIBLE);
            }
        } else {
            this.psContainer.setVisibility(View.GONE);
            this.rgContainer.setVisibility(View.VISIBLE);
            
            oneClickInstallRGs.setVisibility(View.VISIBLE);
            showMissingRGs.setVisibility(View.VISIBLE);
        }
        
        /* Update as well the list, because they should be now enabled or disabled instead of unavailable. */
        if (DialogServiceFeature.this.serviceFeatureView != null) {
            DialogServiceFeature.this.serviceFeatureView.refresh();
        }
    }
    
    
    public IServiceFeature getServiceFeature() {
        return this.serviceFeature;
    }
    
    
    /**
     * Adds the listeners to all the GUI components.
     */
    private void addListener() {
        Button enableDisableButton = (Button) findViewById(R.id.Button_EnableDisable);
        Button showMissingRGs = (Button) findViewById(R.id.Button_ViewMissingRGs);
        Button oneClickInstallRGs = (Button) findViewById(R.id.Button_OneClickInstall);
        Button createNewPreset = (Button) findViewById(R.id.Button_CreateNewPreset);
        Button addToPreset = (Button) findViewById(R.id.Button_AddToPreset);
        Button closeButton = (Button) findViewById(R.id.Button_Close);
        
        /* Add a listener to the close button */
        closeButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                DialogServiceFeature.this.cancel();
            }
        });
        
        /* Add a listener to the enable/disable button */
        enableDisableButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                boolean newState = !DialogServiceFeature.this.serviceFeature.isActive();
                
                if (DialogServiceFeature.this.serviceFeatureView != null) {
                    DialogServiceFeature.this.serviceFeatureView.reactOnChange(newState);
                }
                
                DialogServiceFeature.this.cancel();
            }
        });
        
        showMissingRGs.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                String[] missingResourceGroup = RGInstaller
                        .getMissingResourceGroups(DialogServiceFeature.this.serviceFeature);
                Intent intent = GUITools.createRgFilterIntent(missingResourceGroup);
                getContext().startActivity(intent);
                dismiss();
            }
        });
        
        oneClickInstallRGs.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                String[] missingResourceGroup = RGInstaller
                        .getMissingResourceGroups(DialogServiceFeature.this.serviceFeature);
                RGInstaller.installResourceGroups(getContext(), missingResourceGroup, new RGInstaller.ICallback() {
                    
                    @Override
                    public void callback() {
                        refresh();
                    }
                });
                
            }
        });
        
        createNewPreset.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                try {
                    final IPreset preset = ModelProxy.get().addUserPreset(
                            DialogServiceFeature.this.serviceFeature.getApp().getName() + " - "
                                    + DialogServiceFeature.this.serviceFeature.getName(), "");
                    preset.getTransaction().start();
                    preset.assignApp(DialogServiceFeature.this.serviceFeature.getApp());
                    preset.assignServiceFeature(DialogServiceFeature.this.serviceFeature);
                    preset.getTransaction().commit();
                    
                    /* Open dialog for changing the name and description */
                    new DialogPresetEdit(getContext(), preset, new DialogPresetEdit.ICallback() {
                        
                        @Override
                        public void refresh() {
                            /* Open Preset-Details */
                            Intent intent = GUITools.createPresetIntent(preset);
                            GUITools.startIntent(intent);
                        }
                        
                        
                        @Override
                        public void openPreset(IPreset preset) {
                        }
                    }).show();
                } catch (PrivacySettingValueException e) {
                    Log.e(DialogServiceFeature.this, "Couldn't add Service Feature to Preset, PSVE", e);
                    GUITools.showToast(getContext(), getContext().getString(R.string.failure_invalid_ps_in_sf),
                            Toast.LENGTH_LONG);
                }
                
                refresh();
                
                DialogServiceFeature.this.dismiss();
            }
        });
        
        addToPreset.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                new DialogAddSFtoPreset(getContext(), DialogServiceFeature.this).show();
            }
        });
    }
}
