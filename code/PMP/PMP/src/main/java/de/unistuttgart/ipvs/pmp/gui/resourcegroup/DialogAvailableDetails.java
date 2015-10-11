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
import android.app.ProgressDialog;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.util.model.ModelProxy;
import de.unistuttgart.ipvs.pmp.jpmpps.model.LocalizedResourceGroup;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidPluginException;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidXMLException;
import de.unistuttgart.ipvs.pmp.model.server.IServerDownloadCallback;
import de.unistuttgart.ipvs.pmp.model.server.ServerProvider;
import de.unistuttgart.ipvs.pmp.shared.gui.view.BasicTitleView;

/**
 * The {@link DialogAvailableDetails} displays informations about an available Resourcegroup.
 * 
 * @author Jakob Jarosch
 */
public class DialogAvailableDetails extends Dialog {
    
    protected LocalizedResourceGroup rgInformation;
    
    protected Handler handler;
    
    protected TabAvailable tab;
    
    
    /**
     * Creates a new {@link Dialog} for displaying informations about an available Resourcegroup.
     * 
     * @param tab
     *            Tab which is used to display the {@link Dialog}.
     * @param rgInformation
     *            The informations about the Resourcegroup.
     */
    public DialogAvailableDetails(TabAvailable tab, LocalizedResourceGroup rgInformation) {
        super(tab);
        
        this.tab = tab;
        
        this.handler = new Handler();
        
        this.rgInformation = rgInformation;
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_resourcegroup_available);
        
        BasicTitleView btv = (BasicTitleView) findViewById(R.id.Title);
        String title = rgInformation.getName();
        btv.setTitle(title);
        
        TextView tv = (TextView) findViewById(R.id.TextView_Description);
        String description = rgInformation.getDescription();
        tv.setText(description);
        
        /* Disable the install button when rg already installed. */
        Button installButton = (Button) findViewById(R.id.Button_Install);
        installButton.setEnabled(ModelProxy.get().getResourceGroup(rgInformation.getIdentifier()) == null);
        
        addListener();
    }
    
    
    /**
     * Adds the listener to the Activity layout.
     */
    private void addListener() {
        ((Button) findViewById(R.id.Button_Install)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                final ProgressDialog pd = new ProgressDialog(DialogAvailableDetails.this.getContext());
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd.setTitle(DialogAvailableDetails.this.getContext().getString(R.string.rg_processing_installation));
                pd.setCancelable(false);
                pd.setProgress(0);
                pd.setMax(1);
                pd.show();
                
                ServerProvider.getInstance().setCallback(new IServerDownloadCallback() {
                    
                    @Override
                    public void step(int position, int length) {
                    }
                    
                    
                    @Override
                    public void download(final int position, final int length) {
                        /* Inform the user */
                        DialogAvailableDetails.this.handler.post(new Runnable() {
                            
                            @Override
                            public void run() {
                                pd.setProgress(position);
                                pd.setMax(length);
                            }
                        });
                    }
                });
                
                new Thread() {
                    
                    @Override
                    public void run() {
                        boolean success = false;
                        String error = "see LogCat";
                        
                        try {
                            success = ModelProxy.get().installResourceGroup(
                                    DialogAvailableDetails.this.rgInformation.getIdentifier(), false);
                        } catch (InvalidXMLException e) {
                            error = e.getMessage();
                        } catch (InvalidPluginException e) {
                            error = e.getMessage();
                        }
                        
                        final String message = (success ? "Installed the Resource successfully."
                                : "Failed to install the Resource:\n" + error);
                        
                        /* Inform the user */
                        DialogAvailableDetails.this.handler.post(new Runnable() {
                            
                            @Override
                            public void run() {
                                pd.dismiss();
                                Toast.makeText(DialogAvailableDetails.this.getContext(), message, Toast.LENGTH_LONG)
                                        .show();
                                
                                DialogAvailableDetails.this.tab.updateDownloadList();
                                
                                DialogAvailableDetails.this.dismiss();
                            }
                        });
                    };
                }.start();
                
            }
        });
        
        ((Button) findViewById(R.id.Button_Close)).setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                DialogAvailableDetails.this.dismiss();
            }
        });
    }
}
