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
import android.app.Dialog;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.api.IPMP;
import de.unistuttgart.ipvs.pmp.api.PMP;
import de.unistuttgart.ipvs.pmp.api.gui.registration.RegistrationStateListItem.State;
import de.unistuttgart.ipvs.pmp.api.handler.PMPRequestServiceFeaturesHandler;
import de.unistuttgart.ipvs.pmp.api.handler._default.PMPDefaultRegistrationHandler;

/**
 * {@link RegistrationDialog} which is used by the {@link PMPDefaultRegistrationHandler}.
 * 
 * @author Jakob Jarosch
 */
public class RegistrationDialog extends Dialog implements IRegistrationUI {
    
    /**
     * Handler which can be used to invoke actions in the ui Thread.
     */
    private Handler handler;
    
    /**
     * The layout elements.
     */
    protected RegistrationElements elements;
    
    /**
     * {@link IPMP} instance.
     */
    protected IPMP pmp;
    
    /**
     * The MainActivity of the App.
     */
    private Activity activity;
    
    
    /**
     * Creates a new {@link RegistrationDialog}.
     * 
     * @param activity
     *            The activity which invoked the registration.
     */
    public RegistrationDialog(Activity activity) {
        super(activity);
        this.activity = activity;
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.pmp_api_registration);
        
        setCancelable(false);
        
        this.handler = new Handler();
        this.elements = new RegistrationElements(this);
        this.pmp = PMP.get();
    }
    
    
    @Override
    public void invokeEvent(final RegistrationEventTypes eventType, final Object... parameters) {
        this.handler.post(new Runnable() {
            
            @Override
            public void run() {
                switch (eventType) {
                    case NO_ACITIVTY_DEFINED:
                        RegistrationDialog.this.elements.tvFailureMissingActivity.setVisibility(View.VISIBLE);
                        RegistrationDialog.this.elements.buttonClose.setVisibility(View.VISIBLE);
                        
                        RegistrationDialog.this.elements.setState(1, State.FAIL);
                        break;
                    
                    case PMP_NOT_INSTALLED:
                        RegistrationDialog.this.elements.tvFailureMissingPMP.setVisibility(View.VISIBLE);
                        RegistrationDialog.this.elements.buttonClose.setVisibility(View.VISIBLE);
                        
                        RegistrationDialog.this.elements.setState(1, State.FAIL);
                        RegistrationDialog.this.elements.setState(2, State.NONE);
                        RegistrationDialog.this.elements.setState(3, State.NONE);
                        break;
                    
                    case START_REGISTRATION:
                        RegistrationDialog.this.elements.setState(1, State.SUCCESS);
                        RegistrationDialog.this.elements.setState(2, State.SUCCESS);
                        RegistrationDialog.this.elements.setState(3, State.PROCESSING);
                        break;
                    
                    case REGISTRATION_SUCCEED:
                        RegistrationDialog.this.elements.tvSelectInitialSF.setVisibility(View.VISIBLE);
                        RegistrationDialog.this.elements.buttonSelectInitialSF.setVisibility(View.VISIBLE);
                        
                        RegistrationDialog.this.elements.setState(2, State.SUCCESS);
                        RegistrationDialog.this.elements.setState(3, State.SUCCESS);
                        RegistrationDialog.this.elements.setState(4, State.NEW);
                        break;
                    
                    case REGISTRATION_FAILED:
                        RegistrationDialog.this.elements.tvFailureError.setVisibility(View.VISIBLE);
                        RegistrationDialog.this.elements.tvFailureErrorMessage.setText((String) parameters[0]);
                        RegistrationDialog.this.elements.tvFailureErrorMessage.setVisibility(View.VISIBLE);
                        RegistrationDialog.this.elements.buttonClose.setVisibility(View.VISIBLE);
                        
                        RegistrationDialog.this.elements.setState(2, State.FAIL);
                        RegistrationDialog.this.elements.setState(3, State.FAIL);
                        break;
                    
                    case SF_SCREEN_OPENED:
                        RegistrationDialog.this.elements.setState(4, State.PROCESSING);
                        RegistrationDialog.this.pmp.requestServiceFeatures(new ArrayList<String>(),
                                new DialogServiceFeaturesHandler(RegistrationDialog.this));
                        break;
                    
                    case SF_SCREEN_CLOSED:
                        RegistrationDialog.this.elements.tvSelectInitialSF.setVisibility(View.GONE);
                        RegistrationDialog.this.elements.buttonSelectInitialSF.setVisibility(View.GONE);
                        RegistrationDialog.this.elements.tvOpenApp.setVisibility(View.VISIBLE);
                        RegistrationDialog.this.elements.buttonOpenApp.setVisibility(View.VISIBLE);
                        
                        RegistrationDialog.this.elements.setState(4, State.SUCCESS);
                        RegistrationDialog.this.elements.setState(5, State.NEW);
                        break;
                    
                    case OPEN_APP:
                        RegistrationDialog.this.elements.setState(5, State.PROCESSING);
                        RegistrationDialog.this.dismiss();
                        break;
                    
                    case ALREADY_REGISTERED:
                        break;
                }
            }
        });
    }
    
    
    @Override
    public void close() {
        dismiss();
        this.activity.finish();
    }
}

/**
 * Implementation of a custom very simple {@link PMPRequestServiceFeaturesHandler}.
 * 
 * @author Jakob Jarosch
 */
class DialogServiceFeaturesHandler extends PMPRequestServiceFeaturesHandler {
    
    private RegistrationDialog dialog;
    
    
    /**
     * Creates a new {@link DialogServiceFeaturesHandler}.
     * 
     * @param dialog
     *            The Dialog which uses the handler.
     */
    public DialogServiceFeaturesHandler(RegistrationDialog dialog) {
        this.dialog = dialog;
    }
    
    
    @Override
    public void onFinalize() {
        super.onFinalize();
        
        this.dialog.invokeEvent(RegistrationEventTypes.SF_SCREEN_CLOSED);
    }
}
