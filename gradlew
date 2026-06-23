#!/bin/sh
# Gradle wrapper script for Linux/Mac

APP_HOME="$(cd "$(dirname "$0")" && pwd)"
GRADLE_WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
GRADLE_WRAPPER_PROPERTIES="$APP_HOME/gradle/wrapper/gradle-wrapper.properties"

exec java -jar "$GRADLE_WRAPPER_JAR" "$@"
