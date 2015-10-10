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
package de.unistuttgart.ipvs.pmp.api.gui.registration;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.api.IPMP;
import de.unistuttgart.ipvs.pmp.api.PMP;
import de.unistuttgart.ipvs.pmp.api.gui.registration.RegistrationStateListItem.State;
import de.unistuttgart.ipvs.pmp.api.handler.PMPRegistrationHandler;
import de.unistuttgart.ipvs.pmp.api.handler.PMPRequestServiceFeaturesHandler;

/**
 * The {@link RegistrationActivity} provides an activity based registration at the privacy management platform.
 * It automatically registers the compatible app at PMP and opens after success the main activity of the app.
 * 
 * The app needs to be defined in the AndroidManifest.xml as the main launcher activity, also a meta tag with the normal
 * main activity (like the following example) has to be added to the activity definition.
 * 
 * <pre>
 * &lt;activity android:name="de.unistuttgart.ipvs.pmp.api.activity.RegistrationActivity" android:label="@string/app_name"&gt;
 *    &lt;intent-filter&gt;
 *       &lt;action android:name="android.intent.action.MAIN" /&gt;
 *       &lt;category android:name="android.intent.category.LAUNCHER" /&gt;
 *    &lt;/intent-filter&gt;
 *    &lt;meta-data android:name="mainActivity" android:value=".gui.MainActivity" /&gt;
 * &lt;/activity&gt;
 * </pre>
 * 
 * @author Jakob Jarosch
 */
public class RegistrationActivity extends Activity implements IRegistrationUI {
    
    /**
     * Event number which is used to identify the reply of the startActivityWithResult()-call.
     */
    private static final int CLOSE_ON_RESULT = 111;
    
    /**
     * The mainActivityIntent which is used to start the MainActivity of the App.
     */
    private Intent mainActivityIntent = null;
    
    /**
     * The layout elements.
     */
    protected RegistrationElements elements;
    
    /**
     * {@link IPMP} instance.
     */
    protected IPMP pmp;
    
    /**
     * Handler which can be used to invoke actions in the ui Thread.
     */
    private Handler handler;
    
    /**
     * The last event which was invoked.
     */
    private RegistrationEventTypes lastEvent;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        this.handler = new Handler();
        this.pmp = PMP.get(getApplication());
        
        loadMetaData();
        
        checkRegistration();
    }
    
    
    /**
     * Checks if a registration is possible.
     */
    private void checkRegistration() {
        if (this.mainActivityIntent == null) {
            invokeEvent(RegistrationEventTypes.NO_ACITIVTY_DEFINED);
        } else {
            RegistrationHandler regHandler = new RegistrationHandler(this);
            this.pmp.register(regHandler);
        }
    }
    
    
    /**
     * Loads all GUI elements.
     */
    protected void loadGUI() {
        setContentView(R.layout.pmp_api_registration);
        
        this.elements = new RegistrationElements(this);
        
        /* Initiating, processing step 1 */
        this.elements.setState(1, State.PROCESSING);
    }
    
    
    @Override
    protected void onResume() {
        super.onResume();
        
        /* Invoke the SF_SCREEN_CLOSED-event when coming back from the Service Features selection. */
        if (this.lastEvent == RegistrationEventTypes.SF_SCREEN_OPENED) {
            invokeEvent(RegistrationEventTypes.SF_SCREEN_CLOSED);
            overridePendingTransition(0, 0);
        }
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /* Closing the Activity when coming back from the MainActivity of the App. */
        if (requestCode == RegistrationActivity.CLOSE_ON_RESULT) {
            finish();
        }
    }
    
    
    @Override
    public void invokeEvent(final RegistrationEventTypes eventType, final Object... parameters) {
        this.lastEvent = eventType;
        
        this.handler.post(new Runnable() {
            
            @Override
            public void run() {
                switch (eventType) {
                    case NO_ACITIVTY_DEFINED:
                        loadGUI();
                        RegistrationActivity.this.elements.tvFailureMissingActivity.setVisibility(View.VISIBLE);
                        RegistrationActivity.this.elements.buttonClose.setVisibility(View.VISIBLE);
                        
                        RegistrationActivity.this.elements.setState(1, State.FAIL);
                        break;
                    
                    case PMP_NOT_INSTALLED:
                        loadGUI();
                        RegistrationActivity.this.elements.tvFailureMissingPMP.setVisibility(View.VISIBLE);
                        RegistrationActivity.this.elements.buttonClose.setVisibility(View.VISIBLE);
                        
                        RegistrationActivity.this.elements.setState(1, State.FAIL);
                        RegistrationActivity.this.elements.setState(2, State.NONE);
                        RegistrationActivity.this.elements.setState(3, State.NONE);
                        break;
                    
                    case START_REGISTRATION:
                        loadGUI();
                        RegistrationActivity.this.elements.setState(1, State.SUCCESS);
                        RegistrationActivity.this.elements.setState(2, State.SUCCESS);
                        RegistrationActivity.this.elements.setState(3, State.PROCESSING);
                        break;
                    
                    case REGISTRATION_SUCCEED:
                        RegistrationActivity.this.elements.tvSelectInitialSF.setVisibility(View.VISIBLE);
                        RegistrationActivity.this.elements.buttonSelectInitialSF.setVisibility(View.VISIBLE);
                        
                        RegistrationActivity.this.elements.setState(2, State.SUCCESS);
                        RegistrationActivity.this.elements.setState(3, State.SUCCESS);
                        RegistrationActivity.this.elements.setState(4, State.NEW);
                        break;
                    
                    case REGISTRATION_FAILED:
                        RegistrationActivity.this.elements.tvFailureError.setVisibility(View.VISIBLE);
                        RegistrationActivity.this.elements.tvFailureErrorMessage.setText((String) parameters[0]);
                        RegistrationActivity.this.elements.tvFailureErrorMessage.setVisibility(View.VISIBLE);
                        RegistrationActivity.this.elements.buttonClose.setVisibility(View.VISIBLE);
                        
                        RegistrationActivity.this.elements.setState(2, State.FAIL);
                        RegistrationActivity.this.elements.setState(3, State.FAIL);
                        break;
                    
                    case SF_SCREEN_OPENED:
                        RegistrationActivity.this.elements.setState(4, State.PROCESSING);
                        RegistrationActivity.this.pmp.requestServiceFeatures(new ArrayList<String>(),
                                new ServiceFeaturesHandler());
                        break;
                    
                    case SF_SCREEN_CLOSED:
                        RegistrationActivity.this.elements.tvSelectInitialSF.setVisibility(View.GONE);
                        RegistrationActivity.this.elements.buttonSelectInitialSF.setVisibility(View.GONE);
                        RegistrationActivity.this.elements.tvOpenApp.setVisibility(View.VISIBLE);
                        RegistrationActivity.this.elements.buttonOpenApp.setVisibility(View.VISIBLE);
                        
                        RegistrationActivity.this.elements.setState(4, State.SUCCESS);
                        RegistrationActivity.this.elements.setState(5, State.NEW);
                        break;
                    
                    case OPEN_APP:
                        RegistrationActivity.this.elements.setState(5, State.PROCESSING);
                        switchToMainActivity(true);
                        break;
                    
                    case ALREADY_REGISTERED:
                        break;
                }
            }
        });
    }
    
    
    /**
     * Load the meta data from the AndroidManifest.xml
     */
    private void loadMetaData() {
        try {
            ActivityInfo ai = getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            String activityName = bundle.getString("mainActivity");
            
            this.mainActivityIntent = createMainActivityIntent(activityName);
        } catch (NameNotFoundException e) {
            Log.e(this, "Failed to load package details from android package manager.", e);
        }
    }
    
    
    /**
     * Create a new Intent for opening the main activity of the app.
     * 
     * @param activityName
     *            The Name of the activity class which should be opened.
     * @return The created intent.
     */
    private Intent createMainActivityIntent(String activityName) {
        Intent intent = new Intent();
        intent.setClassName(getApplicationContext(), activityName);
        
        return intent;
    }
    
    
    /**
     * Switches the {@link RegistrationActivity} to the MainActivity of the App.
     * 
     * @param transitionAnimation
     *            If false then no animation is used to switch to the MainActivity.
     */
    public void switchToMainActivity(boolean transitionAnimation) {
        if (!transitionAnimation) {
            this.mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(this.mainActivityIntent, RegistrationActivity.CLOSE_ON_RESULT);
            overridePendingTransition(0, 0);
        } else {
            startActivityForResult(this.mainActivityIntent, RegistrationActivity.CLOSE_ON_RESULT);
        }
    }
    
    
    @Override
    public Context getContext() {
        return getApplicationContext();
    }
    
    
    @Override
    public void close() {
        finish();
    }
}

/**
 * The {@link RegistrationHandler} reacts on events generated by the registration at PMP.
 * 
 * @author Jakob Jarosch
 */
class RegistrationHandler extends PMPRegistrationHandler {
    
    /**
     * The used activity.
     */
    private RegistrationActivity activity;
    
    
    /**
     * Creates a new {@link RegistrationHandler}.
     * 
     * @param activity
     *            The activity which uses the {@link RegistrationHandler}.
     */
    public RegistrationHandler(RegistrationActivity activity) {
        this.activity = activity;
    }
    
    
    @Override
    public void onBindingFailed() {
        this.activity.invokeEvent(RegistrationEventTypes.PMP_NOT_INSTALLED);
    }
    
    
    @Override
    public void onAlreadyRegistered() {
        this.activity.switchToMainActivity(false);
    }
    
    
    @Override
    public void onRegistration() {
        this.activity.invokeEvent(RegistrationEventTypes.START_REGISTRATION);
    }
    
    
    @Override
    public void onSuccess() {
        this.activity.invokeEvent(RegistrationEventTypes.REGISTRATION_SUCCEED);
    }
    
    
    @Override
    public void onFailure(String message) {
        this.activity.invokeEvent(RegistrationEventTypes.REGISTRATION_FAILED, message);
    }
}

/**
 * A default {@link PMPRequestServiceFeaturesHandler}.
 * 
 * @author Jakob Jarosch
 */
class ServiceFeaturesHandler extends PMPRequestServiceFeaturesHandler {
}
