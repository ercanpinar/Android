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

interface Constants {
    String PARENT_GEOFENCE_ID = "parentgeofenceid";
    String IS_GEOFENCE_ENABLE = "shgeofenceenable";
    String PERMISSION_MSG = "msg";
    String PERMISSION_BOOL = "permission_bool";
    int CODE_USER_DISABLES_LOCATION = 8112;
    int CODE_GEOFENCE_UPDATES = 22;       // place in geofence modules
}
