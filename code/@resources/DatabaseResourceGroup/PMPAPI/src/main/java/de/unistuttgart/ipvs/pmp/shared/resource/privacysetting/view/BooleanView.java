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
package de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.view;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.IPrivacySettingView;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.PrivacySettingValueException;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.library.BooleanPrivacySetting;

/**
 * {@link IPrivacySettingView} for {@link BooleanPrivacySetting}
 * 
 * @author Jakob Jarosch
 * 
 */
public class BooleanView extends LinearLayout implements IPrivacySettingView<Boolean> {
    
    private CheckBox checkBox;
    
    
    public BooleanView(Context context) {
        super(context);
        this.checkBox = new CheckBox(context);
        addView(this.checkBox);
    }
    
    
    @Override
    public View asView() {
        return this;
    }
    
    
    @Override
    public void setViewValue(Boolean value) throws PrivacySettingValueException {
        this.checkBox.setChecked(value);
    }
    
    
    @Override
    public Boolean getViewValue() {
        return this.checkBox.isChecked();
    }
    
}
