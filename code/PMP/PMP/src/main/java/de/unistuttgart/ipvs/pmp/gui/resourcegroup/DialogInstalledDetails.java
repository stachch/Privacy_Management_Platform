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
package de.unistuttgart.ipvs.pmp.gui.resourcegroup;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.privacysetting.ViewPrivacySettingBasicInformation;
import de.unistuttgart.ipvs.pmp.gui.util.dialog.DialogConfirmDelete;
import de.unistuttgart.ipvs.pmp.gui.util.model.ModelProxy;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.IResourceGroup;
import de.unistuttgart.ipvs.pmp.shared.gui.view.BasicTitleView;
import de.unistuttgart.ipvs.pmp.util.Restarter;
import de.unistuttgart.ipvs.pmp.xmlutil.revision.RevisionReader;

/**
 * The {@link DialogAvailableDetails} displays informations about an at PMP registered Resourcegroup.
 * 
 * @author Jakob Jarosch
 */
public class DialogInstalledDetails extends Dialog {
    
    protected IResourceGroup resourcegroup;
    protected TabInstalled parent;
    
    
    /**
     * Creates a new {@link Dialog} for displaying informations about an installed Resourcegroup.
     * 
     * @param context
     *            Context which is used to display the {@link Dialog}.
     * @param parent
     *            The Tab which contains the list installed Resourcegroups. If value is NULL no list will be updated on
     *            uninstall.
     * @param resourcegroup
     *            The informations about the Resourcegroup.
     */
    public DialogInstalledDetails(Context context, TabInstalled parent, IResourceGroup resourcegroup) {
        super(context);
        
        this.resourcegroup = resourcegroup;
        this.parent = parent;
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_resourcegroup_installed);
        
        BasicTitleView btv = (BasicTitleView) findViewById(R.id.Title);
        btv.setIcon(resourcegroup.getIcon());
        btv.setTitle(resourcegroup.getName());
        
        TextView tv = (TextView) findViewById(R.id.TextView_Description);
        tv.setText(resourcegroup.getDescription());
        
        TextView tvRev = (TextView) findViewById(R.id.TextView_Revision);
        tvRev.setText(RevisionReader.get().toHumanReadable(resourcegroup.getRevision()));
        
        LinearLayout psList = (LinearLayout) findViewById(R.id.LinearLayout_PrivacySettings);
        for (IPrivacySetting privacySetting : resourcegroup.getPrivacySettings()) {
            psList.addView(new ViewPrivacySettingBasicInformation(getContext(), privacySetting));
        }
        
        addListener();
    }
    
    
    /**
     * Adds the listener to the Activity layout.
     */
    private void addListener() {
        ((Button) findViewById(R.id.Button_Uninstall)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                new DialogConfirmDelete(getContext(), getContext().getString(R.string.rg_confirm_remove),
                        getContext().getString(R.string.rg_confirm_description,
                                DialogInstalledDetails.this.resourcegroup.getName()),
                        new DialogConfirmDelete.ICallback() {
                            
                            @Override
                            public void callback(boolean confirmed) {
                                if (confirmed) {
                                    ModelProxy.get().uninstallResourceGroup(
                                            DialogInstalledDetails.this.resourcegroup.getIdentifier());
                                    
                                    Toast.makeText(getContext(), getContext().getString(R.string.rg_removed_success),
                                            Toast.LENGTH_LONG).show();
                                    
                                    dismiss();
                                    DialogInstalledDetails.this.parent.refreshList();
                                    
                                    /* Here we use a bad code style because android has a bug. see ticket #485 in redmine */
                                    new Thread() {
                                        
                                        @Override
                                        public void run() {
                                            try {
                                                Thread.sleep(2000);
                                            } catch (InterruptedException e) {
                                            }
                                            Restarter.killAppAndRestartActivity(DialogInstalledDetails.this.parent
                                                    .getParent());
                                        };
                                    }.start();
                                }
                            }
                        }).show();
            }
        });
        
        ((Button) findViewById(R.id.Button_Close)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                DialogInstalledDetails.this.dismiss();
            }
        });
    }
}
