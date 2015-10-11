/*
 * Copyright 2012 pmp-android development team
 * Project: DatabaseResourceGroup
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
package de.unistuttgart.ipvs.pmp.resourcegroups.database;

import android.content.Context;
import de.unistuttgart.ipvs.pmp.shared.resource.IPMPConnectionInterface;
import de.unistuttgart.ipvs.pmp.shared.resource.ResourceGroup;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.library.BooleanPrivacySetting;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.library.SetPrivacySetting;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.library.StringConverter;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.view.StringView;

/**
 * @author Jakob Jarosch
 * 
 */
public class Database extends ResourceGroup {
    
    public static final String R_DATABASE = "databaseResource";
    public static final String PS_READ = "read";
    public static final String PS_MODIFY = "modify";
    public static final String PS_CREATE = "create";
    public static final String PS_ALLOWED_DATABASES = "allowedDatabases";
    public static final String DATABASENAME_SEPARATOR = ";";
    
    
    public Database(IPMPConnectionInterface pmpci) {
        super("de.unistuttgart.ipvs.pmp.resourcegroups.database", pmpci);
        
        registerResource(R_DATABASE, new DatabaseResource());
        registerPrivacySetting(PS_READ, new BooleanPrivacySetting());
        registerPrivacySetting(PS_MODIFY, new BooleanPrivacySetting());
        registerPrivacySetting(PS_CREATE, new BooleanPrivacySetting());
        try {
            registerPrivacySetting(PS_ALLOWED_DATABASES, new SetPrivacySetting<String>(StringConverter.forString,
                    StringView.class.getConstructor(Context.class)));
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
