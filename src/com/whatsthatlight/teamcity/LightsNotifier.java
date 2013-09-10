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

// Jetbrains
import jetbrains.buildServer.Build;
import jetbrains.buildServer.notification.Notificator;
import jetbrains.buildServer.notification.NotificatorRegistry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.STest;
import jetbrains.buildServer.serverSide.mute.MuteInfo;
import jetbrains.buildServer.tests.TestName;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.vcs.VcsRoot;
// Java
import java.util.Collection;
import java.util.Set;

public class LightsNotifier implements Notificator {

	// Constructor
	public LightsNotifier(NotificatorRegistry reg) {
		// Register the notifier in the TeamCity registry
		reg.register(this);
		String version = this.getClass().getPackage().getImplementationVersion();
		String logMessage = String.format("%1$s (%2$s) version %3$s registered", Utils.NAME, getNotificatorType(), version);
		Utils.logInfo(Utils.LOGGER, logMessage);
	}

	// Notifier display name
	public String getDisplayName() {
		return Utils.NAME;
	}

	// Unique type of the notifier
	public String getNotificatorType() {
		return Utils.TYPE;
	}

	// Called when new build started
	public void notifyBuildStarted(SRunningBuild build, Set<SUser> users) {
		handleBuildNotification(NotificationType.BUILD_BUILDING, build, users);
	}

	// Called when build finished successfully
	public void notifyBuildSuccessful(SRunningBuild build, Set<SUser> users) {
		handleBuildNotification(NotificationType.BUILD_SUCCESSFUL, build, users);
	}

	// Called when build failed
	public void notifyBuildFailed(SRunningBuild build, Set<SUser> users) {
		handleBuildNotification(NotificationType.BUILD_FAILED, build, users);
	}

	// Called when build failed to start (VCS issue, etc.)
	public void notifyBuildFailedToStart(SRunningBuild build, Set<SUser> users) {
		handleBuildNotification(NotificationType.BUILD_FAILED_TO_START, build,
				users);
	}

	// Called when labelling failed for the build - IGNORE
	public void notifyLabelingFailed(Build build, VcsRoot root,
			Throwable exception, Set<SUser> users) {
		Utils.logInfo(
				Utils.LOGGER,
				"Ignoring notifyLabelingFailed(Build build, VcsRoot root, Throwable exception, Set<SUser> users)");
	}

	// Called when the first failed message occurred
	public void notifyBuildFailing(SRunningBuild build, Set<SUser> users) {
		handleBuildNotification(NotificationType.BUILD_FAILING, build, users);
	}

	// Called when build is not sending messages to server for some time
	public void notifyBuildProbablyHanging(SRunningBuild build, Set<SUser> users) {
		handleBuildNotification(NotificationType.BUILD_HANGING, build, users);
	}

	// Called when responsibility for configuration changed - IGNORE
	public void notifyResponsibleChanged(SBuildType buildType, Set<SUser> users) {
		handleBuildResponsibilityAssignedNotification(
				NotificationType.BUILD_RESPONSIBILITY_ASSIGNED, buildType);
	}

	// Called when responsibility for configuration was assigned - IGNORE
	public void notifyResponsibleAssigned(SBuildType buildType, Set<SUser> users) {
		// Don't know how to trigger this event. Can't find any info besides the
		// JavaDoc on it.
		Utils.logInfo(Utils.LOGGER,
				"Ignoring notifyResponsibleAssigned(SBuildType buildType, Set<SUser> users)");
	}

	// Called when responsibility for a test changed
	public void notifyResponsibleChanged(TestNameResponsibilityEntry oldValue,
			TestNameResponsibilityEntry newValue, SProject project,
			Set<SUser> users) {
		// This event takes AGES to fire... It doesn't seem useful for anything
		// the way it's defined. One needs the build configuration.
		Utils.logInfo(Utils.LOGGER,
				"Ignoring notifyResponsibleAssigned(SBuildType buildType, Set<SUser> users)");
	}

	// Called when responsibility for a test was assigned - IGNORE
	public void notifyResponsibleAssigned(TestNameResponsibilityEntry oldValue,
			TestNameResponsibilityEntry newValue, SProject project,
			Set<SUser> users) {
		// Don't know how to trigger this event. Can't find any info besides the
		// JavaDoc on it.
		Utils.logInfo(
				Utils.LOGGER,
				"Ignoring notifyResponsibleAssigned(TestNameResponsibilityEntry oldValue, TestNameResponsibilityEntry newValue, SProject project, Set<SUser> users)");
	}

	// Called when responsibility for a set of tests changed - IGNORE
	public void notifyResponsibleChanged(Collection<TestName> testNames,
			ResponsibilityEntry entry, SProject project, Set<SUser> users) {
		// Don't know how to trigger this event. Can't find any info besides the
		// JavaDoc on it.
		Utils.logInfo(
				Utils.LOGGER,
				"Ignoring notifyResponsibleChanged(Collection<TestName> testNames, ResponsibilityEntry entry, SProject project, Set<SUser> users)");
	}

	// Called when responsibility for a set of tests was assigned - IGNORE
	public void notifyResponsibleAssigned(Collection<TestName> testNames,
			ResponsibilityEntry entry, SProject project, Set<SUser> users) {
		// Don't know how to trigger this event. Can't find any info besides the
		// JavaDoc on it.
		Utils.logInfo(
				Utils.LOGGER,
				"Ignoring notifyResponsibleAssigned(Collection<TestName> testNames, ResponsibilityEntry entry, SProject project, Set<SUser> users)");
	}

	// Called when a set of tests were muted - IGNORE
	public void notifyTestsMuted(Collection<STest> tests, MuteInfo muteInfo,
			Set<SUser> users) {
		Utils.logInfo(
				Utils.LOGGER,
				"Ignoring notifyTestsMuted(java.util.Collection<STest> tests, MuteInfo muteInfo, java.util.Set<SUser> users)");
	}

	// Called when a set of tests were unmuted - IGNORE
	public void notifyTestsUnmuted(Collection<STest> tests, MuteInfo muteInfo,
			Set<SUser> users) {
		// Don't know how to trigger this event. Can't find any info besides the
		// JavaDoc on it.
		Utils.logInfo(
				Utils.LOGGER,
				"Ignoring notifyTestsMuted(java.util.Collection<STest> tests, MuteInfo muteInfo, java.util.Set<SUser> users)");
	}

	// Build notification handler
	private void handleBuildNotification(Integer notificationType,
			SRunningBuild build, Set<SUser> users) {
		try {
			// Extract fields required for processing
			String projectId = build.getProjectId();
			String buildConfigId = build.getBuildTypeId();
			String buildConfigFullName = build.getFullName();
			String notificationTypeName = NotificationType
					.getName(notificationType);
			String triggeredByUser = Utils.tryGetTriggeredByUser(build);
			String committers = Utils.getUsersThatCommittedToBuild(build);
			String recipients = Utils.mergeCsvs(triggeredByUser, committers);
			// Now we change the user string (if null) to "nobody" so that we 
			// can print it to the log. We don't want "nobody" sent on the
			// wire, so we do it AFTER creating the recipient list. 
			triggeredByUser = (triggeredByUser == null) ? "nobody"
					: triggeredByUser;
			// Skip personal builds
			if (build.isPersonal()) {
				String logMessage = String
						.format("Ignoring personal build notification %1$s for %2$s triggered by %3$s",
								notificationTypeName, buildConfigFullName,
								triggeredByUser);
				Utils.logInfo(Utils.LOGGER, logMessage);
				return;
			}
			String logMessage = String
					.format("Handling %1$s event for %2$s triggered by %3$s, with committers %4$s",
							notificationTypeName, buildConfigFullName,
							triggeredByUser, committers);
			if (committers.isEmpty()) {
				logMessage = String
						.format("Handling %1$s event for %2$s triggered by %3$s, with no committers",
								notificationTypeName, buildConfigFullName,
								triggeredByUser);
			}
			Utils.logInfo(Utils.LOGGER, logMessage);
			String command = Utils.createBuildNotificationCommand(
					notificationType, projectId, buildConfigId, recipients);
			Utils.notifyHost(command);
		} catch (Exception e) {
			Utils.logError(
					Utils.LOGGER,
					String.format("Could not handle event: %1$s",
							e.getMessage()));
			Utils.logStackTrace(Utils.LOGGER, e);
		}
	}

	private void handleBuildResponsibilityAssignedNotification(
			Integer notificationTypeId, SBuildType buildType) {
		// TODO: Cancel previous person that was responsible
		// TODO: Cancel if there's a successful build
		try {
			// Extract fields required for processing
			String projectId = buildType.getProjectId();
			String buildConfigId = buildType.getBuildTypeId();
			String buildConfigFullName = buildType.getFullName();
			String notificationTypeName = NotificationType
					.getName(notificationTypeId);
			String newUsername = buildType.getResponsibilityInfo()
					.getResponsibleUser().getUsername();
			ResponsibilityEntry.State newState = buildType
					.getResponsibilityInfo().getState();
			String reporter = buildType.getResponsibilityInfo()
					.getReporterUser().getUsername();
			String logMessage = String
					.format("Handling %1$s event for %2$s: Responsibility set to %3$s for user %4$s by %5$s",
							notificationTypeName, buildConfigFullName,
							newState.toString(), newUsername, reporter);
			Utils.logInfo(Utils.LOGGER, logMessage);
			String command = Utils
					.createResponsibilityAssignedNotificationCommand(
							notificationTypeId, projectId, buildConfigId,
							newUsername, newState.toString());
			Utils.notifyHost(command);
		} catch (Exception e) {
			Utils.logError(
					Utils.LOGGER,
					String.format("Could not handle event: %1$s",
							e.getMessage()));
			Utils.logStackTrace(Utils.LOGGER, e);
		}
	}

}
