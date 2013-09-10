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
import com.whatsthatlight.teamcity.Utils;

// Java
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.AbstractMap.SimpleEntry;

// Jetbrains
import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.TriggeredBy;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.UserSet;
import jetbrains.buildServer.vcs.SelectPrevBuildPolicy;

import com.intellij.openapi.diagnostic.Logger;

// Test
import static org.mockito.Mockito.*;

import org.junit.*;

import static org.junit.Assert.*;

public class UtilsTest {
	
	@Test
	public void testTypeNotTooLong() {
		assertTrue(Utils.TYPE.length() <= 20);
	}
	
	@Test
	public void testParameterlessConstructor() {
		// Ridiculous. EMMA refuses to set the class definition as covered,
		// so one needs to do this to get valid coverage. 
		Utils u = new Utils();
		assertTrue(u.equals(u));
	}
	
	@Test
	public void testLogInfo() {
		Logger logger = mock(Logger.class);
		Utils.logInfo(logger, "My info message");
	}

	@Test
	public void testLogError() {
		Logger logger = mock(Logger.class);
		Utils.logError(logger, "My error message");
	}
	
	@Test
	public void testLogStackTrace() {
		Logger logger = mock(Logger.class);
		Exception e = new Exception("No exception");
		Utils.logStackTrace(logger, e);
	}

	@Test
	public void testTryGetTriggeredByUserNothingNull() {
		String expectedUser = "user";
		SUser user = mock(SUser.class);
		TriggeredBy triggeredBy = mock(TriggeredBy.class);
		SRunningBuild build = mock(SRunningBuild.class);
		when(user.getUsername()).thenReturn(expectedUser);
		when(triggeredBy.getUser()).thenReturn(user);
		when(build.getTriggeredBy()).thenReturn(triggeredBy);
		String actualUser = Utils.tryGetTriggeredByUser(build);
		assertTrue(actualUser.equals(expectedUser));
	}

	@Test
	public void testTryGetTriggeredByUserTriggeredByNull() {
		SRunningBuild build = mock(SRunningBuild.class);
		when(build.getTriggeredBy()).thenReturn(null);
		String actualUser = Utils.tryGetTriggeredByUser(build);
		assertNull(actualUser);
	}
	
	@Test
	public void testTryGetTriggeredByUserGetUserNull() {
		SRunningBuild build = mock(SRunningBuild.class);
		TriggeredBy triggeredBy = mock(TriggeredBy.class);
		when(triggeredBy.getUser()).thenReturn(null);
		when(build.getTriggeredBy()).thenReturn(triggeredBy);
		String actualUser = Utils.tryGetTriggeredByUser(build);
		assertNull(actualUser);
	}

	@Test
	public void testCreateServerUpCommand() {
		String expectedCmd = "requesttypeid=4;status=1!";
		String actualCmd = Utils.createServerUpCommand();
		assertTrue(actualCmd.equals(expectedCmd));
	}
	
	@Test
	public void testCreateBuildNotificationCommand() {
		int notificationType = 1;
		String projectId = "project2";
		String buildConfigId = "buildconfig5";
		String recipients = "user1,user2";
		String expectedCmd = "notificationtypeid=1;projectid=project2;buildconfigid=buildconfig5;recipients=user1,user2!";
		String actualCmd = Utils.createBuildNotificationCommand(
				notificationType, projectId, buildConfigId, recipients);
		assertTrue(actualCmd.equals(expectedCmd));
	}

	@Test
	public void testCreateResponsibilityAssignedNotificationCommand() {
		int notificationType = 1;
		String projectId = "project2";
		String buildConfigId = "buildconfig5";
		String username = "user1";
		ResponsibilityEntry.State state = ResponsibilityEntry.State.TAKEN;
		String expectedCmd = "notificationtypeid=1;projectid=project2;buildconfigid=buildconfig5;username=user1;state=TAKEN!";
		String actualCmd = Utils.createResponsibilityAssignedNotificationCommand(
				notificationType, projectId, buildConfigId, username, state.toString());
		assertTrue(actualCmd.equals(expectedCmd));
	}
	
	@Test
	public void testCreateCommandWithThreeArgs() {
		LinkedList<SimpleEntry<String, String>> list = new LinkedList<SimpleEntry<String, String>>();
		list.add(new SimpleEntry<String, String>("foo", "Foo"));
		list.add(new SimpleEntry<String, String>("bar", "Bar"));
		list.add(new SimpleEntry<String, String>("baz", "Baz"));
		String expectedCmd = "foo=Foo;bar=Bar;baz=Baz!";
		String actualCmd = Utils.createCommand(list);
		assertTrue(actualCmd.equals(expectedCmd));
	}

	@Test
	public void testCreateCommandWithNoArgs() {
		LinkedList<SimpleEntry<String, String>> list = new LinkedList<SimpleEntry<String, String>>();
		String expectedCmd = "!";
		String actualCmd = Utils.createCommand(list);
		assertTrue(actualCmd.equals(expectedCmd));
	}

	@Test
	public void testConvertSUserToCsvForSingleUser() throws Exception {
		String expectedUser = "user";
		SUser user = mock(SUser.class);
		when(user.getUsername()).thenReturn(expectedUser);
		Set<SUser> userSet = new HashSet<SUser>();
		userSet.add(user);
		String actualUser = Utils.convertUserSetToCsv(userSet);
		assertEquals(expectedUser, actualUser);
	}

	@Test
	public void testConvertSUserToCsvForMultipleUsers() throws Exception {
		String expectedUser1 = "user1";
		String expectedUser2 = "user2";
		String expectedCsvRegex = "^(" + expectedUser1 + "," + expectedUser2
				+ ")|(" + expectedUser2 + "," + expectedUser1 + ")$";
		SUser user1 = mock(SUser.class);
		when(user1.getUsername()).thenReturn(expectedUser1);
		SUser user2 = mock(SUser.class);
		when(user2.getUsername()).thenReturn(expectedUser2);
		Set<SUser> userSet = new HashSet<SUser>();
		userSet.add(user1);
		userSet.add(user2);
		String actualCsv = Utils.convertUserSetToCsv(userSet);
		assertTrue(actualCsv.matches(expectedCsvRegex));
	}

	@Test
	public void testConvertSUserToCsvForNoUser() throws Exception {
		String expectedCsv = "";
		Set<SUser> userSet = new HashSet<SUser>();
		assertTrue(expectedCsv.equals(Utils.convertUserSetToCsv(userSet)));
	}

	@Test
	public void testMergeCsvsDuplicates() throws Exception {
		String firstCsv = "user1";
		String secondCsv = "user1";
		String expectedCsv = firstCsv;
		String actualCsv = Utils.mergeCsvs(firstCsv, secondCsv);
		assertTrue(actualCsv.equals(expectedCsv));
	}

	@Test
	public void testMergeCsvsMultipleDuplicates() throws Exception {
		String user1 = "user1";
		String user2 = "user2";
		String firstCsv = user1 + "," + user2;
		String secondCsv = user2 + "," + user1;
		String expectedCsvRegex = "^(" + user1 + "," + user2 + ")|(" + user2
				+ "," + user1 + ")$";
		String actualCsv = Utils.mergeCsvs(firstCsv, secondCsv);
		assertTrue(actualCsv.matches(expectedCsvRegex));
	}

	@Test
	public void testMergeCsvsNoneEmpty() throws Exception {
		String firstCsv = "user1";
		String secondCsv = "user2";
		String expectedCsvRegex = "^(" + firstCsv + "," + secondCsv + ")|("
				+ secondCsv + "," + firstCsv + ")$";
		String actualCsv = Utils.mergeCsvs(firstCsv, secondCsv);
		assertTrue(actualCsv.matches(expectedCsvRegex));
	}

	@Test
	public void testMergeCsvsFirstOneNull() throws Exception {
		String firstCsv = null;
		String secondCsv = "user2";
		String actualCsv = Utils.mergeCsvs(firstCsv, secondCsv);
		assertTrue(actualCsv.equals(secondCsv));
	}
	
	@Test
	public void testMergeCsvsSecondOneNull() throws Exception {
		String firstCsv = "user1";
		String secondCsv = null;
		String actualCsv = Utils.mergeCsvs(firstCsv, secondCsv);
		assertTrue(actualCsv.equals(firstCsv));
	}
	
	@Test
	public void testMergeCsvsFirstOneEmpty() throws Exception {
		String firstCsv = "";
		String secondCsv = "user2";
		String actualCsv = Utils.mergeCsvs(firstCsv, secondCsv);
		assertTrue(actualCsv.equals(secondCsv));
	}

	@Test
	public void testMergeCsvsSecondOneEmpty() throws Exception {
		String firstCsv = "user1";
		String secondCsv = "";
		String actualCsv = Utils.mergeCsvs(firstCsv, secondCsv);
		assertTrue(actualCsv.equals(firstCsv));
	}

	@Test
	public void testMergeCsvsBothEmpty() throws Exception {
		String firstCsv = "";
		String secondCsv = "";
		String expectedCsv = "";
		String actualCsv = Utils.mergeCsvs(firstCsv, secondCsv);
		assertTrue(actualCsv.equals(expectedCsv));
	}

	@Test
	public void testMergeCsvsBothNull() throws Exception {
		String firstCsv = null;
		String secondCsv = null;
		String expectedCsv = "";
		String actualCsv = Utils.mergeCsvs(firstCsv, secondCsv);
		assertTrue(actualCsv.equals(expectedCsv));
	}

	@Test
	public void testGetUsersThatCommittedToBuild()
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		String expectedUser = "user";
		SRunningBuild build = mock(SRunningBuild.class);
		HashSet<SUser> hashSet = new HashSet<SUser>();
		SUser sUser = mock(SUser.class);
		hashSet.add(sUser);
		@SuppressWarnings("unchecked")
		UserSet<SUser> userSet = (UserSet<SUser>) mock(UserSet.class);
		when(userSet.getUsers()).thenReturn(hashSet);
		when(build.getCommitters(SelectPrevBuildPolicy.SINCE_LAST_SUCCESSFULLY_FINISHED_BUILD)).thenReturn(userSet);
		when(sUser.getUsername()).thenReturn(expectedUser);
		String actualCsv = Utils.getUsersThatCommittedToBuild(build);
		assertTrue(actualCsv.equals(expectedUser));
	}
}
