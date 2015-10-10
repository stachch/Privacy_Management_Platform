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
package de.unistuttgart.ipvs.pmp.model.activity;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.gui.util.model.ModelProxy;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidPluginException;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidXMLException;
import de.unistuttgart.ipvs.pmp.model.plugin.PluginProvider;
import de.unistuttgart.ipvs.pmp.util.Restarter;

public class DebugInstallRGActivity extends Activity {
    
    protected Handler handler;
    protected Dialog dialog;
    protected ProgressDialog pd;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.handler = new Handler();
    }
    
    
    @Override
    protected void onResume() {
        super.onResume();
        
        final String pkg = getIntent().getStringExtra("pkg");
        
        this.pd = new ProgressDialog(this);
        this.pd.setTitle("Install Resource Group");
        this.pd.setMessage("Injecting '" + pkg + "'...");
        this.pd.setCancelable(false);
        LongTaskProgressDialog<Void, Void, Void> ltpd = new LongTaskProgressDialog<Void, Void, Void>(this.pd) {
            
            @Override
            public Void run(Void... params) {
                if (ModelProxy.get().getResourceGroup(pkg) != null) {
                    final boolean uninstall = ModelProxy.get().uninstallResourceGroup(pkg);
                    DebugInstallRGActivity.this.handler.post(new Runnable() {
                        
                        @Override
                        public void run() {
                            if (uninstall) {
                                Toast.makeText(DebugInstallRGActivity.this, "Removed existing RG. Restarting...",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(DebugInstallRGActivity.this, "Error while removing existing RG!",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    
                    // we need to restart PMP since we need a clean DexClassLoader
                    if (uninstall) {
                        Restarter.killAppAndRestartActivity(DebugInstallRGActivity.this);
                    }
                    
                } else {
                    
                    try {
                        String path = getPackageManager().getApplicationInfo(pkg, 0).sourceDir;
                        InputStream rgStream = new FileInputStream(path);
                        try {
                            PluginProvider.getInstance().injectFile(pkg, rgStream);
                        } finally {
                            rgStream.close();
                        }
                        try {
                            ModelProxy.get().installResourceGroup(pkg, true);
                        } catch (InvalidXMLException ixmle) {
                            Log.e(this, "Invalid XML", ixmle);
                            complain("Invalid XML", ixmle);
                        } catch (InvalidPluginException ipe) {
                            Log.e(this, "Invalid Plugin", ipe);
                            complain("Invalid Plugin", ipe);
                        }
                    } catch (IOException ioe) {
                        Log.e(this, "Cannot install RG", ioe);
                        complain("Cannot install RG", ioe);
                    } catch (NameNotFoundException e) {
                        Log.e(this, "DebugInstallRGActivity not found the " + pkg + " details: ", e);
                    }
                    
                }
                return null;
            }
            
            
            @Override
            protected void onPostExecute(Void result) {
                DebugInstallRGActivity.this.pd.dismiss();
                if (DebugInstallRGActivity.this.dialog == null) {
                    Toast.makeText(DebugInstallRGActivity.this, "Installed RG successfully.", Toast.LENGTH_SHORT)
                            .show();
                    DebugInstallRGActivity.this.finish();
                }
            }
        };
        ltpd.execute();
    }
    
    
    /**
     * Complains about an exception
     * 
     * @param title
     * @param t
     */
    protected void complain(String title, Throwable t) {
        complain(title, t.getClass().getCanonicalName() + ": " + t.getMessage() + " (see LogCat)");
    }
    
    
    /**
     * Complains to the user (mostly about the user being dumb as ****.
     * 
     * @param title
     * @param msg
     */
    protected void complain(final String title, final String msg) {
        this.handler.post(new Runnable() {
            
            @Override
            public void run() {
                Builder dialog = new AlertDialog.Builder(DebugInstallRGActivity.this);
                dialog.setTitle(title);
                dialog.setMessage(msg);
                dialog.setPositiveButton("Ok, I will fix it", new DialogInterface.OnClickListener() {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DebugInstallRGActivity.this.pd.dismiss();
                        dialog.dismiss();
                        DebugInstallRGActivity.this.finish();
                    }
                });
                DebugInstallRGActivity.this.dialog = dialog.show();
            }
        });
    }
}
