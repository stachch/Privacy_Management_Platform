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
package de.unistuttgart.ipvs.pmp.gui.util.model;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.gui.util.model.mockup.MockupApp;
import de.unistuttgart.ipvs.pmp.gui.util.model.mockup.MockupModel;
import de.unistuttgart.ipvs.pmp.gui.util.model.mockup.MockupPrivacySetting;
import de.unistuttgart.ipvs.pmp.gui.util.model.mockup.MockupRG;
import de.unistuttgart.ipvs.pmp.gui.util.model.mockup.MockupServiceFeature;
import de.unistuttgart.ipvs.pmp.model.activity.LongTaskProgressDialog;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.IPrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.IResourceGroup;
import de.unistuttgart.ipvs.pmp.shared.Log;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.PrivacySettingValueException;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.library.BooleanPrivacySetting;
import de.unistuttgart.ipvs.pmp.xmlutil.XMLUtilityProxy;
import de.unistuttgart.ipvs.pmp.xmlutil.ais.IAIS;
import de.unistuttgart.ipvs.pmp.xmlutil.ais.IAISRequiredPrivacySetting;
import de.unistuttgart.ipvs.pmp.xmlutil.ais.IAISRequiredResourceGroup;
import de.unistuttgart.ipvs.pmp.xmlutil.ais.IAISServiceFeature;
import de.unistuttgart.ipvs.pmp.xmlutil.parser.common.ParserException;
import de.unistuttgart.ipvs.pmp.xmlutil.rgis.IRGIS;
import de.unistuttgart.ipvs.pmp.xmlutil.rgis.IRGISPrivacySetting;

/**
 * 
 * @author Tobias Kuhn
 *         
 */
public class MockupControl {
    
    private static final List<Throwable> youreDoingItWrong = new ArrayList<Throwable>();
    
    private static final String TAG = "MockupControl";
    
    private static MockupApp app1;
    private static MockupApp app2;
    private static MockupApp app3;
    private static MockupApp app4;
    
    private static IPreset p1;
    private static IPreset p2;
    private static IPreset p3;
    private static IPreset p4;
    
    private static MockupRG rg1;
    private static MockupRG rg2;
    private static MockupRG rg3;
    
    
    public static void init(final Context activityContext) {
        
        ProgressDialog pd = new ProgressDialog(activityContext);
        pd.setTitle("Mocking Model");
        pd.setMessage("Loading mockups...");
        pd.setCancelable(false);
        LongTaskProgressDialog<Void, Void, Void> ltpd = new LongTaskProgressDialog<Void, Void, Void>(pd) {
            
            @Override
            public Void run(Void... params) {
                initRGs(activityContext);
                initApps(activityContext);
                try {
                    initPresets();
                } catch (PrivacySettingValueException psve) {
                    youreDoingItWrong.add(psve);
                }
                return null;
            }
            
            
            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                for (Throwable t : youreDoingItWrong) {
                    String msg = t.getMessage();
                    if (t instanceof ParserException) {
                        // yeah cause we like it intricate
                        msg = ((ParserException) t).getMessage();
                    }
                    
                    new AlertDialog.Builder(activityContext).setTitle("Mockup Error")
                            .setMessage(t.getClass().getCanonicalName() + ": " + msg + " (see LogCat)")
                            .setPositiveButton("Ok, I will fix it", new DialogInterface.OnClickListener() {
                            
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setCancelable(false).show();
                }
            }
            
        };
        ltpd.execute();
    }
    
    
    /**
     * 
     */
    private static void initPresets() throws PrivacySettingValueException {
        String ident;
        String name;
        String description;
        
        ident = "preset_1";
        name = "My first Preset";
        description = "Wooohooo, I've created a cool preset.. wow!";
        p1 = MockupModel.instance.addPreset(null, ident, name, description);
        if ((app1 != null) && (app2 != null) && (app3 != null) && (rg1 != null)) {
            p1.assignApp(app1);
            p1.assignApp(app2);
            p1.assignApp(app3);
            p1.assignPrivacySetting(rg1.getPrivacySetting("READ"), "true");
            p1.assignPrivacySetting(rg1.getPrivacySetting("modify"), "true");
            p1.assignPrivacySetting(rg1.getPrivacySetting("create"), "true");
            p1.assignPrivacySetting(rg2.getPrivacySetting("findloc"), "true");
            p1.assignPrivacySetting(rg2.getPrivacySetting("modify"), "true");
            p1.assignPrivacySetting(rg2.getPrivacySetting("missiles"), "true");
            p1.assignPrivacySetting(rg3.getPrivacySetting("facebook"), "true");
            p1.assignPrivacySetting(rg3.getPrivacySetting("psw"), "true");
            p1.assignPrivacySetting(rg3.getPrivacySetting("kk"), "true");
        } else {
            Log.e(TAG, "app1, app2, app3 or rg1 was null. Could not assign the preset 1.");
        }
        
        ident = "preset_2";
        name = "My second Preset";
        description = "Wooohooo, I've created another very cool preset.. wow!";
        p2 = MockupModel.instance.addPreset(null, ident, name, description);
        if ((app3 != null) && (app4 != null) && (rg1 != null)) {
            p2.assignApp(app3);
            p2.assignApp(app4);
            p2.assignPrivacySetting(rg1.getPrivacySetting("READ"), "true");
            p2.assignPrivacySetting(rg1.getPrivacySetting("create"), "true");
            p2.assignPrivacySetting(rg2.getPrivacySetting("findloc"), "true");
            p2.assignPrivacySetting(rg2.getPrivacySetting("missiles"), "true");
        } else {
            Log.e(TAG, "app3, app4 or rg1 was null. Could not assign the preset 2.");
        }
        
        ident = "preset_3";
        name = "My third Preset";
        description = "Yeah, now I know how to create presets! I'm an expert!!! And therfore I write a veeeeeeeeery long description... can the gui show this?! Yes, of course!";
        p3 = MockupModel.instance.addPreset(null, ident, name, description);
        if ((app1 != null) && (rg1 != null)) {
            p3.assignApp(app1);
            p3.assignPrivacySetting(rg1.getPrivacySetting("modify"), "true");
            p3.assignPrivacySetting(rg2.getPrivacySetting("findloc"), "true");
            p3.assignPrivacySetting(rg3.getPrivacySetting("facebook"), "true");
            p3.assignPrivacySetting(rg3.getPrivacySetting("kk"), "true");
        } else {
            Log.e(TAG, "app1 or rg1 was null. Could not assign the preset 3.");
        }
        
        ident = "preset_4";
        name = "My first deleted Preset";
        description = "Wow, i was able to delete a preset!";
        p4 = MockupModel.instance.addPreset(null, ident, name, description);
        if ((app1 != null) && (app3 != null) && (rg1 != null)) {
            p4.assignApp(app1);
            p4.assignApp(app3);
            p4.setDeleted(true);
            p4.assignPrivacySetting(rg1.getPrivacySetting("READ"), "true");
            p4.assignPrivacySetting(rg3.getPrivacySetting("kk"), "true");
        } else {
            Log.e(TAG, "app1, app3 or rg1 was null. Could not assign the preset 4.");
        }
    }
    
    
    /**
     * @param activityContext
     */
    private static void initApps(Context activityContext) {
        String ident;
        MockupApp app;
        IAIS ais;
        
        ident = "org.barcode.scanner";
        if ((ais = getAIS(activityContext, "barcode.xml")) != null) {
            app1 = new MockupApp(ident, getDrawable(activityContext, R.drawable.test_icon1), ais);
            createSF(ais, app1);
            MockupModel.instance.registerApp(ident, app1);
        }
        
        ident = "com.google.calendar";
        if ((ais = getAIS(activityContext, "calendar.xml")) != null) {
            app2 = new MockupApp(ident, getDrawable(activityContext, R.drawable.test_icon2), ais);
            createSF(ais, app2);
            MockupModel.instance.registerApp(ident, app2);
        }
        
        ident = "com.facebook.apps";
        if ((ais = getAIS(activityContext, "facebook.xml")) != null) {
            app3 = new MockupApp(ident, getDrawable(activityContext, R.drawable.test_icon3), ais);
            createSF(ais, app3);
            MockupModel.instance.registerApp(ident, app3);
        }
        
        ident = "com.google.mail";
        if ((ais = getAIS(activityContext, "gmail.xml")) != null) {
            app4 = new MockupApp(ident, getDrawable(activityContext, R.drawable.test_icon4), ais);
            createSF(ais, app4);
            MockupModel.instance.registerApp(ident, app4);
        }
        
        ident = "com.imdb.android";
        if ((ais = getAIS(activityContext, "imdb.xml")) != null) {
            app = new MockupApp(ident, getDrawable(activityContext, R.drawable.test_icon5), ais);
            createSF(ais, app);
            MockupModel.instance.registerApp(ident, app);
        }
        
        ident = "com.google.sms";
        if ((ais = getAIS(activityContext, "sms.xml")) != null) {
            app = new MockupApp(ident, getDrawable(activityContext, R.drawable.test_icon6), ais);
            createSF(ais, app);
            MockupModel.instance.registerApp(ident, app);
        }
        
        ident = "tv.sony.android";
        if ((ais = getAIS(activityContext, "tv.xml")) != null) {
            app = new MockupApp(ident, getDrawable(activityContext, R.drawable.test_icon7), ais);
            createSF(ais, app);
            MockupModel.instance.registerApp(ident, app);
        }
        
        ident = "com.google.compass";
        if ((ais = getAIS(activityContext, "compass.xml")) != null) {
            app = new MockupApp(ident, getDrawable(activityContext, R.drawable.test_icon8), ais);
            createSF(ais, app);
            MockupModel.instance.registerApp(ident, app);
        }
        
        ident = "com.adobe.rss";
        if ((ais = getAIS(activityContext, "rss.xml")) != null) {
            app = new MockupApp(ident, getDrawable(activityContext, R.drawable.test_icon9), ais);
            createSF(ais, app);
            MockupModel.instance.registerApp(ident, app);
        }
        
        ident = "org.wikipedia.android";
        if ((ais = getAIS(activityContext, "wikipedia.xml")) != null) {
            app = new MockupApp(ident, getDrawable(activityContext, R.drawable.test_icon10), ais);
            createSF(ais, app);
            MockupModel.instance.registerApp(ident, app);
        }
    }
    
    
    /**
     * @param activityContext
     */
    private static void initRGs(Context activityContext) {
        String ident;
        IRGIS rgis;
        
        ident = "org.oracle.db";
        if ((rgis = getRGIS(activityContext, "db.xml")) != null) {
            rg1 = new MockupRG(ident, getDrawable(activityContext, R.drawable.icon_rgs), rgis);
            createPS(rgis, rg1);
            /*rg1.setInstalled(true);*/
            MockupModel.instance.installResourceGroup(ident, rg1);
        }
        
        ident = "gov.gps";
        if ((rgis = getRGIS(activityContext, "gps.xml")) != null) {
            rg2 = new MockupRG(ident, getDrawable(activityContext, R.drawable.test_icon8), rgis);
            createPS(rgis, rg2);
            MockupModel.instance.installResourceGroup(ident, rg2);
        }
        
        ident = "de.bka.bundestrojaner";
        if ((rgis = getRGIS(activityContext, "privacy.xml")) != null) {
            rg3 = new MockupRG(ident, getDrawable(activityContext, R.drawable.icon_search), rgis);
            createPS(rgis, rg3);
            MockupModel.instance.installResourceGroup(ident, rg3);
        }
    }
    
    
    public static void createPS(IRGIS rgis, MockupRG rg) {
        MockupPrivacySetting ps;
        for (IRGISPrivacySetting privacySetting : rgis.getPrivacySettings()) {
            ps = new MockupPrivacySetting(rg, privacySetting.getIdentifier(), new BooleanPrivacySetting());
            rg.addPS(privacySetting.getIdentifier(), ps);
        }
    }
    
    
    public static void createSF(IAIS ais, MockupApp app) {
        MockupServiceFeature sf;
        for (IAISServiceFeature serviceFeature : ais.getServiceFeatures()) {
            
            boolean available = true;
            for (IAISRequiredResourceGroup rrg : serviceFeature.getRequiredResourceGroups()) {
                IResourceGroup rg = MockupModel.instance.getResourceGroup(rrg.getIdentifier());
                if (rg == null) {
                    available = false;
                    break;
                    
                } else {
                    for (IAISRequiredPrivacySetting privacySetting : rrg.getRequiredPrivacySettings()) {
                        IPrivacySetting ps = rg.getPrivacySetting(privacySetting.getIdentifier());
                        if (ps == null) {
                            available = false;
                            break;
                        }
                    }
                }
            }
            
            sf = new MockupServiceFeature(app, serviceFeature.getIdentifier(), available);
            for (IAISRequiredResourceGroup rrg : serviceFeature.getRequiredResourceGroups()) {
                IResourceGroup rg = MockupModel.instance.getResourceGroup(rrg.getIdentifier());
                if (rg != null) {
                    for (IAISRequiredPrivacySetting privacySetting : rrg.getRequiredPrivacySettings()) {
                        IPrivacySetting ps = rg.getPrivacySetting(privacySetting.getIdentifier());
                        if (ps != null) {
                            sf.addPS((MockupPrivacySetting) ps, privacySetting.getValue());
                        }
                    }
                }
            }
            app.addSF(serviceFeature.getIdentifier(), sf);
        }
    }
    
    
    public static IRGIS getRGIS(Context context, String fileName) {
        IRGIS result = null;
        try {
            result = XMLUtilityProxy.getRGUtil().parse(context.getAssets().open("samples2/rg/" + fileName));
        } catch (Throwable t) {
            youreDoingItWrong.add(t);
            Log.e(TAG, "Could not mock RGIS", t);
        }
        return result;
    }
    
    
    public static IAIS getAIS(Context context, String fileName) {
        IAIS result = null;
        try {
            result = XMLUtilityProxy.getAppUtil().parse(context.getAssets().open("samples2/app/" + fileName));
        } catch (Throwable t) {
            youreDoingItWrong.add(t);
            Log.e(TAG, "Could not mock AIS", t);
        }
        return result;
    }
    
    
    @SuppressWarnings("deprecation")
    private static Drawable getDrawable(Context context, int id) {
        return context.getResources().getDrawable(id);
    }
    
}
