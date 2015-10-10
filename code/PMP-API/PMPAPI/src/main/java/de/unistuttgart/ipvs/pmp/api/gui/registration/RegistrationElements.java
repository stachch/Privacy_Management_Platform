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
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.R;

/**
 * The Elements class provides all elements defined in the xml.
 * 
 * @author Jakob Jarosch
 */
class RegistrationElements {
    
    /**
     * The parent UI. Also see ({@link IRegistrationUI}.
     */
    protected IRegistrationUI parentUi;
    
    /**
     * The Failure-TextView "missing activity".
     */
    public TextView tvFailureMissingActivity;
    
    /**
     * The Failure-TextView "missing pmp".
     */
    public TextView tvFailureMissingPMP;
    
    /**
     * The Failure-TextView "error during installation".
     */
    public TextView tvFailureError;
    
    /**
     * The Failure-TextView "error message during installation".
     */
    public TextView tvFailureErrorMessage;
    
    /**
     * The TextView "description for opening the app".
     */
    public TextView tvOpenApp;
    
    /**
     * The TextView "description for selecting initial service features".
     */
    public TextView tvSelectInitialSF;
    
    /**
     * The Button for closing the App.
     */
    public Button buttonClose;
    
    /**
     * The Button for bringing the main activity to the front.
     */
    public Button buttonOpenApp;
    
    /**
     * The Button for selecting the initial Service Features.
     */
    public Button buttonSelectInitialSF;
    
    /**
     * The List of {@link RegistrationStateListItem} which indicates the current state of registration.
     */
    private List<RegistrationStateListItem> registrationStateList = new ArrayList<RegistrationStateListItem>();
    
    
    /**
     * Create a new {@link RegistrationElements} instance.
     * 
     * @param parentUi
     *            The UI which uses the layout components described here.
     */
    public RegistrationElements(IRegistrationUI parentUi) {
        this.parentUi = parentUi;
        
        this.tvFailureMissingActivity = (TextView) this.parentUi.findViewById(R.id.TextView_Failure_MissingActivity);
        this.tvFailureMissingPMP = (TextView) this.parentUi.findViewById(R.id.TextView_Failure_MissingPMP);
        this.tvFailureError = (TextView) this.parentUi.findViewById(R.id.TextView_Failure_Error);
        this.tvFailureErrorMessage = (TextView) this.parentUi.findViewById(R.id.TextView_Failure_ErrorMessage);
        this.tvOpenApp = (TextView) this.parentUi.findViewById(R.id.TextView_OpenApp);
        this.tvSelectInitialSF = (TextView) this.parentUi.findViewById(R.id.TextView_OpenSFList);
        
        this.buttonClose = (Button) this.parentUi.findViewById(R.id.Button_Close);
        this.buttonOpenApp = (Button) this.parentUi.findViewById(R.id.Button_OpenApp);
        this.buttonSelectInitialSF = (Button) this.parentUi.findViewById(R.id.Button_OpenSFList);
        
        fillRegistrationStateList();
        
        addListener();
    }
    
    
    /**
     * Updates the state of an item from the list.
     * 
     * @param item
     *            Number of the item, begins with 1.
     * @param state
     *            New state of the item.
     */
    public void setState(int item, RegistrationStateListItem.State state) {
        this.registrationStateList.get(item - 1).setState(state);
    }
    
    
    /**
     * Fills the registration state list.
     */
    private void fillRegistrationStateList() {
        this.registrationStateList.add(new RegistrationStateListItem(this.parentUi.getContext(), 1, this.parentUi
                .getContext().getString(R.string.pmp_api_registration_step_1)));
        this.registrationStateList.add(new RegistrationStateListItem(this.parentUi.getContext(), 2, this.parentUi
                .getContext().getString(R.string.pmp_api_registration_step_2)));
        this.registrationStateList.add(new RegistrationStateListItem(this.parentUi.getContext(), 3, this.parentUi
                .getContext().getString(R.string.pmp_api_registration_step_3)));
        this.registrationStateList.add(new RegistrationStateListItem(this.parentUi.getContext(), 4, this.parentUi
                .getContext().getString(R.string.pmp_api_registration_step_4)));
        this.registrationStateList.add(new RegistrationStateListItem(this.parentUi.getContext(), 5, this.parentUi
                .getContext().getString(R.string.pmp_api_registration_step_5)));
        
        ((LinearLayout) this.parentUi.findViewById(R.id.LinearLayout_States)).removeAllViews();
        
        for (RegistrationStateListItem item : this.registrationStateList) {
            ((LinearLayout) this.parentUi.findViewById(R.id.LinearLayout_States)).addView(item);
        }
    }
    
    
    /**
     * Adds the listeners.
     */
    private void addListener() {
        this.buttonClose.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                RegistrationElements.this.parentUi.close();
            }
        });
        
        this.buttonSelectInitialSF.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                RegistrationElements.this.parentUi.invokeEvent(RegistrationEventTypes.SF_SCREEN_OPENED);
            }
        });
        
        this.buttonOpenApp.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                RegistrationElements.this.parentUi.invokeEvent(RegistrationEventTypes.OPEN_APP);
            }
        });
    }
}
