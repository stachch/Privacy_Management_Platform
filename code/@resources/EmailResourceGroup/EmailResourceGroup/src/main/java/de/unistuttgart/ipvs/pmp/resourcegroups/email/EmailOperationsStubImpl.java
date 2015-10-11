/*
 * Copyright 2012 pmp-android development team
 * Project: EmailResourceGroup
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
package de.unistuttgart.ipvs.pmp.resourcegroups.email;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.PrivacySettingValueException;
import de.unistuttgart.ipvs.pmp.shared.resource.privacysetting.library.BooleanPrivacySetting;

public class EmailOperationsStubImpl extends IEmailOperations.Stub {
    
    private String appIdentifier;
    private EmailResource resource;
    private Context context;
    
    
    public EmailOperationsStubImpl(String appIdentifier, EmailResource resource, Context context) {
        this.appIdentifier = appIdentifier;
        this.resource = resource;
        this.context = context;
    }
    
    
    @Override
    public void sendEmail(String to, String subject, String body) throws RemoteException {
        BooleanPrivacySetting bps = (BooleanPrivacySetting) this.resource
                .getPrivacySetting(Email.PRIVACY_SETTING_SEND_EMAIL);
        
        try {
            if (!bps.permits(this.appIdentifier, true)) {
                throw new SecurityException();
            }
        } catch (PrivacySettingValueException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        emailIntent.setType("plain/text");
        
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { to });
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
        
        Intent startIntent = Intent.createChooser(emailIntent, "Send mail...");
        startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.context.startActivity(startIntent);
    }
    
}
