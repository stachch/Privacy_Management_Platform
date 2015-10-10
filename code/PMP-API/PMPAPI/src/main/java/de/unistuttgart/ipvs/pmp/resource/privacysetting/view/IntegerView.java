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
package de.unistuttgart.ipvs.pmp.resource.privacysetting.view;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.IPrivacySettingView;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.PrivacySettingValueException;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.library.IntegerPrivacySetting;

/**
 * {@link IPrivacySettingView} for {@link IntegerPrivacySetting}
 * 
 * @author Jakob Jarosch
 * 
 */
public class IntegerView extends LinearLayout implements IPrivacySettingView<Integer> {
    
    private EditText editText;
    
    
    @SuppressWarnings("deprecation")
    public IntegerView(Context context) {
        super(context);
        this.editText = new EditText(context);
        this.editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        this.editText.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(this.editText);
    }
    
    
    @Override
    public View asView() {
        return this;
    }
    
    
    @Override
    public void setViewValue(Integer value) throws PrivacySettingValueException {
        this.editText.setText("" + value.toString());
    }
    
    
    @Override
    public Integer getViewValue() {
        try {
            return Integer.valueOf(this.editText.getText().toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
}
