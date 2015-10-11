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
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;
import de.unistuttgart.ipvs.pmp.PMPApplication;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.main.ActivityMain;
import de.unistuttgart.ipvs.pmp.gui.util.model.ModelProxy;
import de.unistuttgart.ipvs.pmp.model.DatabaseOpenHelper;
import de.unistuttgart.ipvs.pmp.model.Model;
import de.unistuttgart.ipvs.pmp.model.ModelCache;
import de.unistuttgart.ipvs.pmp.model.PersistenceProvider;
import de.unistuttgart.ipvs.pmp.model.element.IModelElement;
import de.unistuttgart.ipvs.pmp.model.element.app.App;
import de.unistuttgart.ipvs.pmp.model.element.contextannotation.IContextAnnotation;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.element.preset.Preset;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.PrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.ResourceGroup;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.ServiceFeature;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidPluginException;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidXMLException;
import de.unistuttgart.ipvs.pmp.model.plugin.PluginProvider;
import de.unistuttgart.ipvs.pmp.service.ServiceNotification;
import de.unistuttgart.ipvs.pmp.shared.Log;
import de.unistuttgart.ipvs.pmp.shared.api.ipc.IPCConnection;
import de.unistuttgart.ipvs.pmp.util.BootReceiver;

public class PMPDeveloperConsoleActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_developer_console);
        setTitle("PMP Developer Console");
        EditText rgPath = (EditText) findViewById(R.id.pdc_rg_path);
        rgPath.setText(Environment.getExternalStorageDirectory().getAbsolutePath());
        registerListener();
    }
    
    
    @Override
    protected void onResume() {
        super.onResume();
        
        ToggleButton mockup = (ToggleButton) findViewById(R.id.pdc_switch_mockup_btn);
        mockup.setChecked(ModelProxy.isMockup());
        setRealModelBtnStates(mockup.isChecked());
    }
    
    
    /**
     * dis/enables the buttons that only operate on the real model
     * 
     * @param isMockup
     */
    private void setRealModelBtnStates(boolean isMockup) {
        int[] ids = new int[] { R.id.pdc_precache_btn, R.id.pdc_clearcache_btn, R.id.pdc_clean_tables_btn };
        
        for (int id : ids) {
            Button b = (Button) findViewById(id);
            b.setEnabled(!isMockup);
        }
    }
    
    
    protected void registerListener() {
        /*
         * Launcher Activity
         */
        Button launcherActivity = (Button) findViewById(R.id.pdc_main_activity_btn);
        launcherActivity.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ActivityMain.class));
            }
        });
        
        /*
         * Switch mockup
         */
        ToggleButton mockup = (ToggleButton) findViewById(R.id.pdc_switch_mockup_btn);
        mockup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ModelProxy.set(isChecked, PMPDeveloperConsoleActivity.this);
                setRealModelBtnStates(isChecked);
            }
        });
        
        /*
         * precache real model
         */
        Button precache = (Button) findViewById(R.id.pdc_precache_btn);
        precache.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                PersistenceProvider.getInstance().cacheEverythingNow();
                Toast.makeText(PMPDeveloperConsoleActivity.this, "Precached", Toast.LENGTH_SHORT).show();
            }
        });
        
        /*
         * clearcache real model
         */
        Button clearcache = (Button) findViewById(R.id.pdc_clearcache_btn);
        clearcache.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                PersistenceProvider.getInstance().releaseCache();
                Toast.makeText(PMPDeveloperConsoleActivity.this, "Cache cleaned", Toast.LENGTH_SHORT).show();
            }
        });
        
        /*
         * cleantables real model
         */
        Button cleantables = (Button) findViewById(R.id.pdc_clean_tables_btn);
        cleantables.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                PersistenceProvider pp = PersistenceProvider.getInstance();
                try {
                    // it's a developer console, what'ya expect?
                    pp.reloadDatabaseConnection();
                    Field dohf = pp.getClass().getDeclaredField("doh");
                    dohf.setAccessible(true);
                    DatabaseOpenHelper doh = (DatabaseOpenHelper) dohf.get(pp);
                    doh.cleanTables();
                    
                    Toast.makeText(PMPDeveloperConsoleActivity.this, "Cleaned tables", Toast.LENGTH_SHORT).show();
                    
                } catch (Throwable t) {
                    Log.e(this, "While cleaning tables: ", t);
                }
            }
        });
        
        /*
         * bind app service
         */
        Button bindService = (Button) findViewById(R.id.pdc_appservice_bind_btn);
        bindService.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                EditText nameEdit = (EditText) findViewById(R.id.pdc_appservice_name_edit);
                testBindService(nameEdit.getText().toString());
            }
        });
        
        /*
         * install rg
         */
        Button installRG = (Button) findViewById(R.id.pdc_rg_install_btn);
        installRG.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                try {
                    EditText rgPath = (EditText) findViewById(R.id.pdc_rg_path);
                    EditText rgId = (EditText) findViewById(R.id.pdc_rg_name);
                    
                    if (ModelProxy.get().getResourceGroup(rgId.getText().toString()) != null) {
                        complain("Already installed",
                                "Installing the same thing twice, eh? Unexpected UserIntelligenceTooLow"
                                        + " Exception...");
                        return;
                    }
                    
                    InputStream rgStream = new FileInputStream(rgPath.getText().toString());
                    try {
                        PluginProvider.getInstance().injectFile(rgId.getText().toString(), rgStream);
                    } finally {
                        rgStream.close();
                    }
                    try {
                        ModelProxy.get().installResourceGroup(rgId.getText().toString(), true);
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
                }
                
            }
        });
        
        /*
         * total debug model
         */
        Button tocModel = (Button) findViewById(R.id.pdc_toc_model);
        tocModel.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (ModelProxy.isMockup()) {
                    throw new UnsupportedOperationException("Srsly? We need that for mockup as well?");
                }
                
                try {
                    Model m = (Model) ModelProxy.get();
                    // yeah here's the actual nasty part
                    Field f = m.getClass().getDeclaredField("cache");
                    f.setAccessible(true);
                    ModelCache mc = (ModelCache) f.get(m);
                    
                    if (mc == null) {
                        Log.d(this, "MODEL CACHE == NULL");
                        return;
                    }
                    
                    // and off you go
                    Log.d(this, "LIST OF APPs");
                    for (Entry<String, App> e : mc.getApps().entrySet()) {
                        Log.d(this, String.format("  %s => %s", e.getKey(), e.getValue()));
                    }
                    
                    Log.d(this, "LIST OF SFs");
                    for (Entry<App, Map<String, ServiceFeature>> e : mc.getServiceFeatures().entrySet()) {
                        Log.d(this, String.format("  MAP FOR %s", e.getKey()));
                        for (Entry<String, ServiceFeature> e2 : e.getValue().entrySet()) {
                            Log.d(this, String.format("     %s => %s", e2.getKey(), e2.getValue()));
                        }
                    }
                    
                    Log.d(this, "LIST OF RGs");
                    for (Entry<String, ResourceGroup> e : mc.getResourceGroups().entrySet()) {
                        Log.d(this, String.format("  %s => %s", e.getKey(), e.getValue()));
                    }
                    
                    Log.d(this, "LIST OF PSs");
                    for (Entry<ResourceGroup, Map<String, PrivacySetting>> e : mc.getPrivacySettings().entrySet()) {
                        Log.d(this, String.format("  MAP FOR %s", e.getKey()));
                        for (Entry<String, PrivacySetting> e2 : e.getValue().entrySet()) {
                            Log.d(this, String.format("     %s => %s", e2.getKey(), e2.getValue()));
                        }
                    }
                    
                    Log.d(this, "LIST OF PRESETs");
                    for (Entry<IModelElement, Map<String, Preset>> e : mc.getPresets().entrySet()) {
                        Log.d(this, String.format("  MAP FOR %s", e.getKey()));
                        for (Entry<String, Preset> e2 : e.getValue().entrySet()) {
                            Log.d(this, String.format("     %s => %s", e2.getKey(), e2.getValue()));
                        }
                    }
                    
                } catch (Throwable t) {
                    Log.e(this, "While debugging model: ", t);
                }
                
            }
        });
        
        /* 
         * total debug db
         */
        Button tocDB = (Button) findViewById(R.id.pdc_toc_db);
        tocDB.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                PersistenceProvider pp = PersistenceProvider.getInstance();
                try {
                    // it's a developer console, what'ya expect?
                    pp.reloadDatabaseConnection();
                    Field dohf = pp.getClass().getDeclaredField("doh");
                    dohf.setAccessible(true);
                    DatabaseOpenHelper doh = (DatabaseOpenHelper) dohf.get(pp);
                    doh.debug();
                    
                } catch (Throwable t) {
                    Log.e(this, "While debugging db: ", t);
                }
            }
            
        });
        
        /*
         * total debug conflicts
         */
        Button tocConflicts = (Button) findViewById(R.id.pdc_toc_conflicts);
        tocConflicts.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                try {
                    
                    Log.d(this, ":: LIST OF CONFLICTS ::");
                    for (IPreset p : Model.getInstance().getPresets()) {
                        Log.d(this, "PRESET ('" + p.getIdentifier() + "')");
                        
                        for (IPrivacySetting ps : p.getGrantedPrivacySettings()) {
                            Log.d(this, "   # PS ('" + ps.getIdentifier() + "')");
                            for (IContextAnnotation ca : p.getContextAnnotations(ps)) {
                                Log.d(this, "   #   - CA ('" + ca + "')");
                                
                                // actual check here
                                for (IPreset other : Model.getInstance().getPresets()) {
                                    if (!other.equals(p)) {
                                        for (IContextAnnotation otherCA : ca.getConflictingContextAnnotations(other)) {
                                            Log.d(this, "   #   -   CA/CA conflict ('" + other.getIdentifier()
                                                    + "' : '" + otherCA + "')");
                                        }
                                        
                                        if (ca.isPrivacySettingConflicting(other)) {
                                            Log.d(this, "   #   -   CA/PS conflict ('" + other.getIdentifier() + "')");
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                } catch (Throwable t) {
                    Log.e(this, "While debugging db: ", t);
                }
            }
        });
        
        /*
         * svc nfc bound
         */
        ToggleButton svc_nfc_bound = (ToggleButton) findViewById(R.id.svc_nfc_bound_switch);
        svc_nfc_bound.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ServiceNotification.setBound(isChecked);
            }
        });
        
        /*
         * svc nfc working
         */
        ToggleButton svc_nfc_working = (ToggleButton) findViewById(R.id.svc_nfc_working_switch);
        svc_nfc_working.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ServiceNotification.setWorking(isChecked);
            }
        });
        
        /*
         * svc context recalc 
         */
        Button svc_ctx_calc = (Button) findViewById(R.id.pdc_svc_calc);
        svc_ctx_calc.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                BootReceiver.startService(PMPApplication.getContext());
            }
        });
        
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
    protected void complain(String title, String msg) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(msg)
                .setPositiveButton("Ok, I will fix it", new DialogInterface.OnClickListener() {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(false).show();
    }
    
    
    private void testBindService(final String serviceName) {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Binding service");
        pd.setMessage("Please wait while Android tries to talk to the service.");
        
        LongTaskProgressDialog<Void, Void, Void> ltpd = new LongTaskProgressDialog<Void, Void, Void>(pd) {
            
            @Override
            public Void run(Void... params) {
                IPCConnection ipcc = new IPCConnection(getApplication());
                
                IBinder service = ipcc.getBinder();
                if (service != null) {
                    new AlertDialog.Builder(PMPDeveloperConsoleActivity.this).setMessage("Connection successful.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setCancelable(true).show();
                    
                } else { // service == null                    
                    new AlertDialog.Builder(PMPDeveloperConsoleActivity.this).setMessage("Connection failed.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setCancelable(true).show();
                    
                }
                
                return null;
            }
        };
        ltpd.execute();
    }
}
