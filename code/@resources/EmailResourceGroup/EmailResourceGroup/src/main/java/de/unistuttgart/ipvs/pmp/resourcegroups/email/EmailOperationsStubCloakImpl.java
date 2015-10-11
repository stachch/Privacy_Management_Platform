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

import android.os.RemoteException;

public class EmailOperationsStubCloakImpl extends IEmailOperations.Stub {
    
    @Override
    public void sendEmail(String to, String subject, String body) throws RemoteException {
        // TODO Auto-generated method stub
        
    }
    
}
