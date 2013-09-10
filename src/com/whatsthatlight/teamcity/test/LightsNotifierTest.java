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

// Java
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

// Jetbrains
import jetbrains.buildServer.Build;
import jetbrains.buildServer.notification.NotificatorRegistry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.serverSide.ResponsibilityInfo;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.STest;
import jetbrains.buildServer.serverSide.TriggeredBy;
import jetbrains.buildServer.serverSide.mute.MuteInfo;
import jetbrains.buildServer.tests.TestName;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.users.UserSet;
import jetbrains.buildServer.vcs.SelectPrevBuildPolicy;
import jetbrains.buildServer.vcs.VcsRoot;

import com.intellij.openapi.diagnostic.Logger;
// Local
import com.whatsthatlight.teamcity.LightsNotifier;
import com.whatsthatlight.teamcity.Utils;

// Test
import static org.mockito.Mockito.*;

import org.junit.*;

import static org.junit.Assert.*;

// The tests in this class doesn't really test much, besides
// exercising the code for code coverage. 
public class LightsNotifierTest {

	@Test
	public void testGetDisplayName() {
		NotificatorRegistry reg = mock(NotificatorRegistry.class);
		LightsNotifier n = new LightsNotifier(reg);
		assertTrue(n.getDisplayName().equals(Utils.NAME));
	}
	
	@Test
	public void testGetNotificatorType() {
		NotificatorRegistry reg = mock(NotificatorRegistry.class);
		LightsNotifier n = new LightsNotifier(reg);
		assertTrue(n.getNotificatorType().equals(Utils.TYPE));
	}
	
	@Test
	public void testNotifyBuildStartedNormalExecution() {
		// Parameters
		String projectId = "project1";
		String buildConfigId = "buildconfig1";
		String buildConfigFullName = "Project 1 :: Build Config 1";
		boolean isPersonal = false;
		String username = "user1";
		// Notificator registry
		NotificatorRegistry reg = mock(NotificatorRegistry.class);
		// User sets
		SUser user = mock(SUser.class);
		when(user.getUsername()).thenReturn(username);
		Set<SUser> set = new HashSet<SUser>();
		set.add(user);	
		@SuppressWarnings("unchecked")
		UserSet<SUser> userSet = mock(UserSet.class);
		when(userSet.getUsers()).thenReturn(set);
		// Triggered by
		TriggeredBy triggeredBy = mock(TriggeredBy.class);
		when(triggeredBy.getUser()).thenReturn(user);
		// The build
		SRunningBuild build = mock(SRunningBuild.class);
		when(build.getProjectId()).thenReturn(projectId);
		when(build.getBuildTypeId()).thenReturn(buildConfigId);
		when(build.getFullName()).thenReturn(buildConfigFullName);
		when(build.isPersonal()).thenReturn(isPersonal);
		when(build.getTriggeredBy()).thenReturn(triggeredBy);
		when(build.getCommitters(any(SelectPrevBuildPolicy.class))).thenReturn(userSet);
		// Override the logger and GO
		Utils.LOGGER = mock(Logger.class);
		LightsNotifier n = new LightsNotifier(reg);
		n.notifyBuildStarted(build, set);
		n.notifyBuildFailed(build, set);
		n.notifyBuildFailedToStart(build, set);
		n.notifyBuildFailing(build, set);
		n.notifyBuildProbablyHanging(build, set);
		n.notifyBuildSuccessful(build, set);
	}
	
	@Test
	public void testNotifyBuildStartedPrivateBuild() {
		// Parameters
		String projectId = "project1";
		String buildConfigId = "buildconfig1";
		String buildConfigFullName = "Project 1 :: Build Config 1";
		boolean isPersonal = true;
		String username = "user1";
		// Notificator registry
		NotificatorRegistry reg = mock(NotificatorRegistry.class);
		// User sets
		SUser user = mock(SUser.class);
		when(user.getUsername()).thenReturn(username);
		Set<SUser> set = new HashSet<SUser>();
		set.add(user);	
		@SuppressWarnings("unchecked")
		UserSet<SUser> userSet = mock(UserSet.class);
		when(userSet.getUsers()).thenReturn(set);
		// Triggered by
		TriggeredBy triggeredBy = mock(TriggeredBy.class);
		when(triggeredBy.getUser()).thenReturn(user);
		// The build
		SRunningBuild build = mock(SRunningBuild.class);
		when(build.getProjectId()).thenReturn(projectId);
		when(build.getBuildTypeId()).thenReturn(buildConfigId);
		when(build.getFullName()).thenReturn(buildConfigFullName);
		when(build.isPersonal()).thenReturn(isPersonal);
		when(build.getTriggeredBy()).thenReturn(triggeredBy);
		when(build.getCommitters(any(SelectPrevBuildPolicy.class))).thenReturn(userSet);
		// Override the logger and GO
		Utils.LOGGER = mock(Logger.class);
		LightsNotifier n = new LightsNotifier(reg);
		n.notifyBuildStarted(build, set);
	}
	
	@Test
	public void testNotifyBuildStartedNotTriggeredByUser() {
		// Parameters
		String projectId = "project1";
		String buildConfigId = "buildconfig1";
		String buildConfigFullName = "Project 1 :: Build Config 1";
		boolean isPersonal = true;
		String username = "user1";
		// Notificator registry
		NotificatorRegistry reg = mock(NotificatorRegistry.class);
		// User sets
		SUser user = mock(SUser.class);
		when(user.getUsername()).thenReturn(username);
		Set<SUser> set = new HashSet<SUser>();
		set.add(user);	
		@SuppressWarnings("unchecked")
		UserSet<SUser> userSet = mock(UserSet.class);
		when(userSet.getUsers()).thenReturn(set);
		// The build
		SRunningBuild build = mock(SRunningBuild.class);
		when(build.getProjectId()).thenReturn(projectId);
		when(build.getBuildTypeId()).thenReturn(buildConfigId);
		when(build.getFullName()).thenReturn(buildConfigFullName);
		when(build.isPersonal()).thenReturn(isPersonal);
		when(build.getTriggeredBy()).thenReturn(null);
		when(build.getCommitters(any(SelectPrevBuildPolicy.class))).thenReturn(userSet);
		// Override the logger and GO
		Utils.LOGGER = mock(Logger.class);
		LightsNotifier n = new LightsNotifier(reg);
		n.notifyBuildStarted(build, set);
	}
	
	@Test
	public void testNotifyBuildStartedNoCommitters() {
		// Parameters
		String projectId = "project1";
		String buildConfigId = "buildconfig1";
		String buildConfigFullName = "Project 1 :: Build Config 1";
		boolean isPersonal = false;
		String username = "user1";
		// Notificator registry
		NotificatorRegistry reg = mock(NotificatorRegistry.class);
		// User sets
		SUser user = mock(SUser.class);
		when(user.getUsername()).thenReturn(username);
		Set<SUser> set = new HashSet<SUser>();
		@SuppressWarnings("unchecked")
		UserSet<SUser> userSet = mock(UserSet.class);
		when(userSet.getUsers()).thenReturn(set);
		// Triggered by
		TriggeredBy triggeredBy = mock(TriggeredBy.class);
		when(triggeredBy.getUser()).thenReturn(user);
		// The build
		SRunningBuild build = mock(SRunningBuild.class);
		when(build.getProjectId()).thenReturn(projectId);
		when(build.getBuildTypeId()).thenReturn(buildConfigId);
		when(build.getFullName()).thenReturn(buildConfigFullName);
		when(build.isPersonal()).thenReturn(isPersonal);
		when(build.getTriggeredBy()).thenReturn(triggeredBy);
		when(build.getCommitters(any(SelectPrevBuildPolicy.class))).thenReturn(userSet);
		// Override the logger and GO
		Utils.LOGGER = mock(Logger.class);
		LightsNotifier n = new LightsNotifier(reg);
		n.notifyBuildStarted(build, set);
	}	

	@Test
	public void testNotifyResponsibleChanged() {
		// Parameters
		String projectId = "project1";
		String buildConfigId = "buildconfig1";
		String buildConfigFullName = "Project 1 :: Build Config 1";
		String username = "user1";
		// User sets
		SUser user = mock(SUser.class);
		when(user.getUsername()).thenReturn(username);
		Set<SUser> set = new HashSet<SUser>();
		set.add(user);	
		@SuppressWarnings("unchecked")
		UserSet<SUser> userSet = mock(UserSet.class);
		when(userSet.getUsers()).thenReturn(set);
		// Responsibility info	
		ResponsibilityInfo info = mock(ResponsibilityInfo.class);
		when(info.getResponsibleUser()).thenReturn(user);
		when(info.getState()).thenReturn(ResponsibilityInfo.State.TAKEN);
		when(info.getReporterUser()).thenReturn(user);
		// Build configuration
		// You need jdom.jar to mock this
		SBuildType buildType = mock(SBuildType.class);
		when(buildType.getProjectId()).thenReturn(projectId);
		when(buildType.getBuildTypeId()).thenReturn(buildConfigId);
		when(buildType.getFullName()).thenReturn(buildConfigFullName);
		when(buildType.getResponsibilityInfo()).thenReturn(info);
		// Go
		NotificatorRegistry reg = mock(NotificatorRegistry.class);
		LightsNotifier n = new LightsNotifier(reg);
		n.notifyResponsibleChanged(buildType, set);
		n.notifyResponsibleAssigned(buildType, set);
	}
	
	@Test
	public void testNotifyLabelingFailed() {
		Build build = mock(Build.class);
		VcsRoot vcsRoot = mock(VcsRoot.class);
		Set<SUser> set = new HashSet<SUser>();
		Throwable throwable = new Throwable();
		// Go
		NotificatorRegistry reg = mock(NotificatorRegistry.class);
		LightsNotifier n = new LightsNotifier(reg);
		n.notifyLabelingFailed(build, vcsRoot, throwable, set);
	}

	@Test
	public void testNotifyResponsibleChangedForTest() {
		TestNameResponsibilityEntry oldValue = mock(TestNameResponsibilityEntry.class);
		TestNameResponsibilityEntry newValue = mock(TestNameResponsibilityEntry.class);
		SProject project = mock(SProject.class);
		Set<SUser> users = new HashSet<SUser>();
		// Go
		NotificatorRegistry reg = mock(NotificatorRegistry.class);
		LightsNotifier n = new LightsNotifier(reg);
		n.notifyResponsibleAssigned(oldValue, newValue, project, users);
		n.notifyResponsibleChanged(oldValue, newValue, project, users);
	}
	
	@Test
	public void testNotifyResponsibleChangedForTestCollection() {
		TestNameResponsibilityEntry entry = mock(TestNameResponsibilityEntry.class);
		SProject project = mock(SProject.class);
		Set<SUser> users = new HashSet<SUser>();
		TestName testName = mock(TestName.class);
		@SuppressWarnings("unchecked")
		Collection<TestName> testNameCollection = mock(Collection.class);
		testNameCollection.add(testName);
		// Go
		NotificatorRegistry reg = mock(NotificatorRegistry.class);
		LightsNotifier n = new LightsNotifier(reg);
		n.notifyResponsibleAssigned(testNameCollection, entry, project, users);
		n.notifyResponsibleChanged(testNameCollection, entry, project, users);
	}
	
	@Test
	public void testNotifyTestsMuted() {
		@SuppressWarnings("unchecked")
		Collection<STest> tests = mock(Collection.class);
		MuteInfo muteInfo = mock(MuteInfo.class);
		Set<SUser> users = new HashSet<SUser>();
		// Go
		NotificatorRegistry reg = mock(NotificatorRegistry.class);
		LightsNotifier n = new LightsNotifier(reg);
		n.notifyTestsMuted(tests, muteInfo, users);
		n.notifyTestsUnmuted(tests, muteInfo, users);
	}
	
}
