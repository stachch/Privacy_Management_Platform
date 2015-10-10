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
package de.unistuttgart.ipvs.pmp.resource.privacysetting.library;

import android.content.Context;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.DefaultPrivacySetting;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.IPrivacySettingView;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.PrivacySettingValueException;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.view.BooleanView;

/**
 * {@link DefaultPrivacySetting} for {@link Boolean}.
 * 
 * @author Tobias Kuhn, Jakob Jarosch
 * 
 */
public class BooleanPrivacySetting extends DefaultPrivacySetting<Boolean> {
    
    @Override
    public Boolean parseValue(String value) throws PrivacySettingValueException {
        return StringConverter.forBooleanSafe.valueOf(value);
    }
    
    
    @Override
    public String valueToString(Boolean value) {
        return StringConverter.forBooleanSafe.toString(value);
    }
    
    
    @Override
    public IPrivacySettingView<Boolean> makeView(Context context) {
        return new BooleanView(context);
    }
    
}
