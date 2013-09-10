TeamCity-Notifier-Plugin
========================

A TeamCity plugin to send notifications to a notification server which can broadcast events to registered clients.

# Help
* [TeamCity Custom Notifier](http://www.jetbrains.net/confluence/display/TCD4/Custom+Notifier)
* [TeamCity Open API](http://javadoc.jetbrains.net/teamcity/openapi/current/)

# Installation
* Compile `lights-notifier.jar` (ant clean release)
* Copy JAR to `<TEAMCITY_HOME>/webapps/ROOT/WEB-INF/lib`
* Copy `build-server-plugin-lights-notifier.xml` to `<TEAMCITY_HOME>/webapps/ROOT/WEB-INF`
* Check permissions of both the JAR and XML above to be the same than what the other files in those directories are
* Add the following two sections to `<TEAMCITY_HOME>/conf/teamcity-server-log4j.xml`

```xml
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
```

* Restart TeamCity service
* Set up or use a TeamCity account (typically an admin account) and ensure that that account will receive all server notifications
