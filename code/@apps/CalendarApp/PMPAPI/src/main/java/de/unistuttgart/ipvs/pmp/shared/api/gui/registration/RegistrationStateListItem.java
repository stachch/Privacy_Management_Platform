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
package de.unistuttgart.ipvs.pmp.shared.api.gui.registration;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import de.unistuttgart.ipvs.pmp.shared.R;

/**
 * An Item in the registration state list.
 * 
 * @author Jakob Jarosch
 */
public class RegistrationStateListItem extends LinearLayout {
    
    /**
     * The number of the element.
     */
    private int itemNumber;
    
    /**
     * The name of the element.
     */
    private String itemName;
    
    /**
     * The inherited text field for displaying the number of the element.
     */
    private TextView itemNumberTv;
    
    /**
     * The inherited text field for displaying the name of the element.
     */
    private TextView itemNameTv;
    
    /**
     * The inherited progress bar icon on the right.
     */
    private ProgressBar itemProgessPb;
    
    /**
     * The inherited image icon on the right.
     */
    private ImageView itemStateIv;
    
    /**
     * The State of the item.
     */
    public enum State {
        /**
         * Standard initial state, no right side icon or progress.
         */
        NONE,
        
        /**
         * Displaying the progress bar on the right side.
         */
        PROCESSING,
        
        /**
         * Displaying the tick icon on the right side.
         */
        SUCCESS,
        
        /**
         * Displaying the cross icon on the right side.
         */
        FAIL,
        
        /**
         * Displaying the arrow to left icon on the right side.
         */
        NEW,
        
        /**
         * Displaying a grey tick icon on the right side.
         */
        SKIPPED
    }
    
    
    /**
     * Creates a new {@link RegistrationStateListItem}.
     * 
     * @param context
     *            The context which is required to create the item.
     * @param itemNumber
     *            The number of the item.
     * @param itemName
     *            The name of the item.
     */
    @SuppressWarnings("deprecation")
    public RegistrationStateListItem(Context context, int itemNumber, String itemName) {
        super(context);
        
        this.itemName = itemName;
        this.itemNumber = itemNumber;
        
        /* create the layout */
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        View view = layoutInflater.inflate(R.layout.pmp_api_registration_list_state_item, null);
        addView(view);
        
        /* Strech the layout to the whole width. */
        setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        view.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
                
        /* Load all layout elements. */
        this.itemNumberTv = (TextView) findViewById(R.id.TextView_ItemNumber);
        this.itemNameTv = (TextView) findViewById(R.id.TextView_ItemName);
        this.itemProgessPb = (ProgressBar) findViewById(R.id.ProgressBar_ItemProcessing);
        this.itemStateIv = (ImageView) findViewById(R.id.ImageView_ItemState);
        
        this.itemNumberTv.setText("" + this.itemNumber + ".");
        this.itemNameTv.setText(this.itemName);
    }
    
    
    /**
     * Sets a new state to the {@link RegistrationStateListItem}.
     * 
     * @param state
     *            The new state which should be displayed.
     */
    public void setState(State state) {
        switch (state) {
            case NONE:
                this.itemProgessPb.setVisibility(View.GONE);
                this.itemStateIv.setVisibility(View.GONE);
                break;
                
            case PROCESSING:
                this.itemProgessPb.setVisibility(View.VISIBLE);
                this.itemStateIv.setVisibility(View.GONE);
                break;
                
            case SUCCESS:
                this.itemProgessPb.setVisibility(View.GONE);
                this.itemStateIv.setVisibility(View.VISIBLE);
                this.itemStateIv.setImageResource(R.drawable.pmp_api_icon_success);
                break;
                
            case FAIL:
                this.itemProgessPb.setVisibility(View.GONE);
                this.itemStateIv.setVisibility(View.VISIBLE);
                this.itemStateIv.setImageResource(R.drawable.pmp_api_icon_failed);
                break;
                
            case NEW:
                this.itemProgessPb.setVisibility(View.GONE);
                this.itemStateIv.setVisibility(View.VISIBLE);
                this.itemStateIv.setImageResource(R.drawable.pmp_api_icon_arrow_left);
                break;
                
            case SKIPPED:
                this.itemProgessPb.setVisibility(View.GONE);
                this.itemStateIv.setVisibility(View.VISIBLE);
                this.itemStateIv.setImageResource(R.drawable.pmp_api_icon_semi_success);
                break;
                
        }
    }
}
