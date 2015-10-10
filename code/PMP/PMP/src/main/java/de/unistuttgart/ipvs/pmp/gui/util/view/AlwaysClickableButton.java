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
package de.unistuttgart.ipvs.pmp.gui.util.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * For my fellow team members a {@link Button} that can handle clicks even when disabled. To react on such clicks use
 * {@link AlwaysClickableButton#setDisabledClickListener(OnClickListener)}.
 * 
 * @author Tobias Kuhn
 * 
 */
public class AlwaysClickableButton extends Button {
    
    /**
     * Click listener for handling clicks when this button is disabled.
     */
    protected OnClickListener disabledClickListener;
    
    
    /**
     * @see {@link Button#Button(Context)}
     */
    public AlwaysClickableButton(Context context) {
        super(context);
    }
    
    
    /**
     * @see {@link Button#Button(Context, AttributeSet)}
     */
    public AlwaysClickableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    
    /**
     * @see {@link Button#Button(Context, AttributeSet, int)}
     */
    public AlwaysClickableButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    
    public OnClickListener getDisabledClickListener() {
        return this.disabledClickListener;
    }
    
    
    public void setDisabledOnClickListener(OnClickListener disabledClickListener) {
        this.disabledClickListener = disabledClickListener;
    }
    
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = false;
        if (!isEnabled() && (event.getAction() == MotionEvent.ACTION_UP) && (this.disabledClickListener != null)) {
            this.disabledClickListener.onClick(this);
            handled = true;
        } else {
            handled = super.onTouchEvent(event);
        }
        return handled;
    }
    
}
