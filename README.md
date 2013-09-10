TeamCity-Notifier-Plugin
========================

A TeamCity plugin to send notifications to a notification server which can broadcast events to registered clients.

Help:
 o http://www.jetbrains.net/confluence/display/TCD4/Custom+Notifier
 o http://javadoc.jetbrains.net/teamcity/openapi/current/
Installation:
 o Compile lights-notifier.jar (ant clean release)
 o Copy JAR to C:\TeamCity\webapps\ROOT\WEB-INF\lib
 o Copy build-server-plugin-lights-notifier.xml to C:\TeamCity\webapps\ROOT\WEB-INF
 o Check permissions of both the JAR and XML above to be the same
   than what the other files in those directories are
 o Add the following two sections to <TEAMCITY_HOME>/conf/teamcity-server-log4j.xml
======================================================================
<appender name="ROLL.LIGHTS.NOTIFIER" class="jetbrains.buildServer.util.TCRollingFileAppender">
 <param name="file" value="${teamcity_logs}lights-notifier.log"/>
 <param name="maxBackupIndex" value="3"/>
 <!--REPLACE PREVIOUS LINE WITH UNCOMMENTED LINE TO STORE MORE LOGS-->
 <!-- <param name="maxBackupIndex" value="20"/> -->

 <layout class="org.apache.log4j.PatternLayout">
   <param name="ConversionPattern" value="%d - %-5p - %m %n"/>
 </layout>
</appender>

<category name="com.whatsthatlight.teamcity">
 <!-- Set this to DEBUG to enable debug logging -->
 <priority value="INFO"/>
 <appender-ref ref="ROLL.LIGHTS.NOTIFIER"/>
</category>
======================================================================
 o Restart TeamCity service
 o Set up or use a TeamCity account (typically an admin account) and  
   ensure that that account will receive all server notifications
