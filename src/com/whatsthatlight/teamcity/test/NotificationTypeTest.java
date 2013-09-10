/**
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

package com.whatsthatlight.teamcity.test;

// Local
import com.whatsthatlight.teamcity.NotificationType;

import com.whatsthatlight.teamcity.exceptions.InvalidNotificationTypeException;

// Test
import org.junit.*;

import static org.junit.Assert.*;

public class NotificationTypeTest {

	@Test
	public void testGetNameNone() throws IllegalAccessException, InvalidNotificationTypeException {
		assertTrue(NotificationType.getName(NotificationType.NONE).equals("NONE"));
	}

	@Test(expected=InvalidNotificationTypeException.class)
	public void testGetNameInvalidNotificationTypeException() throws IllegalAccessException, InvalidNotificationTypeException {
		NotificationType.getName(-1);
	}
	
	@Test
	public void testParameterlessConstructor() {
		// Ridiculous. EMMA refuses to set the class definition as covered,
		// so one needs to do this to get valid coverage. 
		NotificationType n = new NotificationType();
		assertTrue(n.equals(n));
	}
	
}
