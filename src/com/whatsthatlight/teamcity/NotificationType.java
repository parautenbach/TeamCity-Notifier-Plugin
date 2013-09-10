/*
Copyright 2013 Pieter Rautenbach

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.whatsthatlight.teamcity;

import java.lang.reflect.*;

import com.whatsthatlight.teamcity.exceptions.InvalidNotificationTypeException;

public class NotificationType {
    public static final int NONE                           = 0;
    public static final int UNKNOWN                        = 1;
    public static final int BUILD_BUILDING                 = 2;
    public static final int BUILD_FAILING                  = 3;
    public static final int BUILD_FAILED                   = 4;
    public static final int BUILD_FAILED_TO_START          = 5;
    public static final int BUILD_HANGING                  = 6;
    public static final int BUILD_SUCCESSFUL               = 7;
    public static final int BUILD_RESPONSIBILITY_ASSIGNED  = 8;
    public static final int TEST_RESPONSIBILITY_ASSIGNED   = 9;   
   
    public static String getName(Integer status) throws IllegalAccessException, InvalidNotificationTypeException
    {
    	Field[] fields = NotificationType.class.getFields();
    	for (int i = 0; i < fields.length; i++)
    	{
    		if (((Integer)fields[i].get(null)).equals(status))
    		{
    			return fields[i].getName();
    		}
    	}
    	throw new InvalidNotificationTypeException("Notification type not found");
    }
    
}
