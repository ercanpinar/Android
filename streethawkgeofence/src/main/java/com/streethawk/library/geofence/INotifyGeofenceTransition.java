/*
 * Copyright (c) StreetHawk, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package com.streethawk.library.geofence;

/**
 * Observers to notify when device enters and exits a given geofence
 */
public interface INotifyGeofenceTransition {

    /**
     * @deprecated
     * Function called when device enters a geofence region registered with StreetHawk
     */
    public void onDeviceEnteringGeofence();

    /**
     * Function called when device leaves a geofence region registered with StreetHawk
     */
    public void onDeviceLeavingGeofence();

}
