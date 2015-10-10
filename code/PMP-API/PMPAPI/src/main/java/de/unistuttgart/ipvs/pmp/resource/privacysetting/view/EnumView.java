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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.IPrivacySettingView;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.PrivacySettingValueException;

public class EnumView<T extends Enum<T>> implements IPrivacySettingView<T> {
    
    private Spinner spinner;
    
    
    public EnumView(Context context, Class<T> clazz) {
        this.spinner = new Spinner(context);
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<T>(context, android.R.layout.simple_spinner_item,
                clazz.getEnumConstants());
        this.spinner.setAdapter(spinnerAdapter);
    }
    
    
    @Override
    public View asView() {
        return this.spinner;
    }
    
    
    @Override
    public void setViewValue(T value) throws PrivacySettingValueException {
        this.spinner.setSelection(value.ordinal());
    }
    
    
    @SuppressWarnings("unchecked")
    @Override
    public T getViewValue() {
        return (T) this.spinner.getSelectedItem();
    }
    
}
