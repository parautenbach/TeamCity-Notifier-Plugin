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

// Java
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.lang.reflect.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
// Jetbrains
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.vcs.SelectPrevBuildPolicy;
import com.intellij.openapi.diagnostic.Logger;

public class Utils {

	// Command terminator
	private static final String CMD_TERM = "!";
	// Command terminator
	private static final String CMD_SEP = ";";
	// Key-value separator
	private static final String CMD_KV_SEP = "=";
	// Host
	public static final String HOST = "localhost";
	// Host port
	public static final int PORT = 9191;
	// Name of the notifier as will be displayed in TeamCity
	// http://en.wikipedia.org/wiki/Blinkenlights
	public static final String NAME = "Das blinkenlichten";
	// The type of this notifier (anything unique, but not
	// longer than 20 characters, which is TC 6.5's limit)
	public static final String TYPE = "Build Lights";
	// The TeamCity logger to use
	public static Logger LOGGER = Logger.getInstance("com.whatsthatlight.teamcity");
	
	// Notify
	public synchronized static void notifyHost(String command) {
		// TODO: If running on CI, don't try to connect and send; use an environment variable
		logInfo(LOGGER, String.format("Notify host %1$s on port %2$s: %3$s",
				HOST, PORT, command));
		try {
			Socket s = new Socket(HOST, PORT);
			BufferedOutputStream bos = new BufferedOutputStream(
					s.getOutputStream());
			OutputStreamWriter osw = new OutputStreamWriter(bos, "US-ASCII");
			osw.write(command);
			osw.flush();
			osw.close();
			s.close();
		} catch (Exception e) {
			logWarn(LOGGER, String.format(
					"Unable to connect to host %1$s on port %2$s: %3$s",
					HOST, PORT, e.getMessage()));
		}
	}

	public static void logInfo(Logger logger, String message) {
		logger.info(message);
	}
	
	public static void logWarn(Logger logger, String message) {
		logger.warn(message);
	}
	
	public static void logError(Logger logger, String message) {
		logger.error(message);
	}
	
	public static void logStackTrace(Logger logger, Exception ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		Utils.logError(logger, sw.toString());
		pw.close();
		try {
			sw.close();
		} catch (IOException e) {
			// From http://docs.oracle.com/javase/1.4.2/docs/api/java/io/StringWriter.html#close():
			// Closing a StringWriter has no effect. The methods in this 
			// class can be called after the stream has been closed without 
			// generating an IOException.
		}
		logger.error(NAME + " - " + pw.toString());
	}
	
	public static String createBuildNotificationCommand(int notificationTypeId, String projectId, String buildConfigId, String recipients) {
		LinkedList<SimpleEntry<String, String>> list = new LinkedList<SimpleEntry<String, String>>();
		list.add(new SimpleEntry<String, String>("notificationtypeid", Integer.toString(notificationTypeId)));
		list.add(new SimpleEntry<String, String>("projectid", projectId));
		list.add(new SimpleEntry<String, String>("buildconfigid", buildConfigId));
		list.add(new SimpleEntry<String, String>("recipients", recipients));
		return createCommand(list);
	}
	
	public static String createResponsibilityAssignedNotificationCommand(int notificationTypeId, String projectId, String buildConfigId, String username, String state) {
		LinkedList<SimpleEntry<String, String>> list = new LinkedList<SimpleEntry<String, String>>();
		list.add(new SimpleEntry<String, String>("notificationtypeid", Integer.toString(notificationTypeId)));
		list.add(new SimpleEntry<String, String>("projectid", projectId));
		list.add(new SimpleEntry<String, String>("buildconfigid", buildConfigId));
		list.add(new SimpleEntry<String, String>("username", username));
		list.add(new SimpleEntry<String, String>("state", state));
		return createCommand(list);
	}
	
	public static String createServerUpCommand() {
		LinkedList<SimpleEntry<String, String>> list = new LinkedList<SimpleEntry<String, String>>();
		list.add(new SimpleEntry<String, String>("requesttypeid", "4"));
		list.add(new SimpleEntry<String, String>("status", "1"));
		return createCommand(list);
	}
	
	public static String createCommand(LinkedList<SimpleEntry<String, String>> list) {
		StringBuffer command = new StringBuffer();
		Iterator<SimpleEntry<String, String>> iter = list.iterator();
		while (iter.hasNext()) {
			SimpleEntry<String, String> entry = iter.next();
			command.append(entry.getKey());
			command.append(CMD_KV_SEP);
			command.append(entry.getValue());
			if (iter.hasNext()) {
				command.append(CMD_SEP);
			}
		}
		command.append(CMD_TERM);
		return command.toString();
	}

	public static String getUsersThatCommittedToBuild(SRunningBuild build)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		// BUG (TODO): If there never was a successful build, this won't work, 
		// e.g. you've created a new project and the first run fails. Also, this
		// will be an issue for all consecutive builds. You need at least one
		// green build. 
		// build.getCommitters(SelectPrevBuildPolicy.SINCE_FIRST_BUILD)
		return convertUserSetToCsv(build.getCommitters(
				SelectPrevBuildPolicy.SINCE_LAST_SUCCESSFULLY_FINISHED_BUILD)
				.getUsers());
	}

	public static String convertUserSetToCsv(Set<SUser> set)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		return convertSetToCsv(set, SUser.class.getMethod("getUsername"), ",");
	}

	public static String convertSetToCsv(Set<?> set, Method method,
			String delimiter) throws IllegalAccessException,
			InvocationTargetException {
		StringBuffer builder = new StringBuffer();
		Iterator<?> iter = set.iterator();
		while (iter.hasNext()) {
			Object e = iter.next();
			if (method != null) {
				builder.append(method.invoke(e));
			} else {
				builder.append(e);
			}
			if (!iter.hasNext()) {
				break;
			}
			builder.append(delimiter);
		}
		return builder.toString();
	}

	public static String mergeCsvs(String csv1, String csv2)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		if (csv1 == null && csv2 == null) {
			return "";
		} else if ((csv1 == null && csv2 != null) || (csv1 != null && csv1.isEmpty() && csv2 != null)) {
			return csv2;
		} else if ((csv2 == null && csv1 != null) || (csv2 != null && csv2.isEmpty() && csv1 != null)) {
			return csv1;
		} else {
			String[] split1 = csv1.split(",");
			String[] split2 = csv2.split(",");
			HashSet<String> set = new HashSet<String>();
			for (String s1 : split1) {
				set.add(s1);
			}
			for (String s2 : split2) {
				set.add(s2);
			}
			return convertSetToCsv(set, null, ",");
		}
	}

	public static String tryGetTriggeredByUser(SRunningBuild build) {
		if (build.getTriggeredBy() != null && build.getTriggeredBy().getUser() != null) {
			return build.getTriggeredBy().getUser().getUsername();
		} else {
			return null;
		}
	}
	
}
