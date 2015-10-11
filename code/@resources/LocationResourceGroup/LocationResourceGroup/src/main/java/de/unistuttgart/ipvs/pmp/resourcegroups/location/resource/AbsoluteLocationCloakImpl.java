/*
 * Copyright 2012 pmp-android development team
 * Project: LocationResourceGroup
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
package de.unistuttgart.ipvs.pmp.resourcegroups.location.resource;

import android.os.RemoteException;
import de.unistuttgart.ipvs.pmp.resourcegroups.location.aidl.IAbsoluteLocation;

public class AbsoluteLocationCloakImpl extends IAbsoluteLocation.Stub {
    
    @Override
    public void startLocationLookup(long minTime, float minDistance) throws RemoteException {
        // TODO Auto-generated method stub
        
    }
    
    
    @Override
    public void endLocationLookup() throws RemoteException {
        // TODO Auto-generated method stub
        
    }
    
    
    @Override
    public boolean isGpsEnabled() throws RemoteException {
        // TODO Auto-generated method stub
        return false;
    }
    
    
    @Override
    public boolean isActive() throws RemoteException {
        // TODO Auto-generated method stub
        return false;
    }
    
    
    @Override
    public boolean isFixed() throws RemoteException {
        // TODO Auto-generated method stub
        return false;
    }
    
    
    @Override
    public boolean isUpdateAvailable() throws RemoteException {
        // TODO Auto-generated method stub
        return false;
    }
    
    
    @Override
    public double getLongitude() throws RemoteException {
        // TODO Auto-generated method stub
        return 0;
    }
    
    
    @Override
    public double getLatitude() throws RemoteException {
        // TODO Auto-generated method stub
        return 0;
    }
    
    
    @Override
    public float getAccuracy() throws RemoteException {
        // TODO Auto-generated method stub
        return 0;
    }
    
    
    @Override
    public float getSpeed() throws RemoteException {
        // TODO Auto-generated method stub
        return 0;
    }
    
    
    @Override
    public String getCountryCode() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }
    
    
    @Override
    public String getCountryName() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }
    
    
    @Override
    public String getLocality() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }
    
    
    @Override
    public String getPostalCode() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }
    
    
    @Override
    public String getAddress() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }
    
}
