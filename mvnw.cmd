@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Maven2 Start Up Batch script
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM
@REM Optional ENV vars
@REM MAVEN_HOME - location of maven2's installed home dir
@REM MAVEN_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM MAVEN_BATCH_PAUSE - set to 'on' to wait for a key stroke before ending
@REM MAVEN_OPTS - parameters passed to the Java VM when running Maven
@REM     e.g. to debug Maven itself, use
@REM set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@REM ----------------------------------------------------------------------------

@IF "%MAVEN_BATCH_ECHO%" == "on"  echo %MAVEN_BATCH_ECHO%

@setlocal

@set ERROR_CODE=0

@REM To isolate internal variables from possible porting issues, separate codes into another file
@set MAVEN_MAIN_CLASS=org.codehaus.plexus.classworlds.launcher.Launcher

@REM Check for JAVA_HOME
@if not "%JAVA_HOME%" == "" goto checkJava

@set JAVA_EXE=java.exe
@%JAVA_EXE% -version >NUL 2>&1
@if "%ERRORLEVEL%" == "0" goto init

@echo.
@echo ERROR: JAVA_HOME not found in your environment.
@echo Please set the JAVA_HOME variable in your environment to match the
@echo location of your Java installation.
@echo.
@goto error

:checkJava
@set JAVA_EXE="%JAVA_HOME%\bin\java.exe"
@if exist %JAVA_EXE% goto init

@echo.
@echo ERROR: JAVA_HOME is set to an invalid directory.
@echo JAVA_HOME = "%JAVA_HOME%"
@echo Please set the JAVA_HOME variable in your environment to match the
@echo location of your Java installation.
@echo.
@goto error

:init
@REM Find the project base dir, i.e. the directory that contains the folder ".mvn".
@REM Fallback to current working directory if not found.

@set MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%
@IF NOT "%MAVEN_PROJECTBASEDIR%"=="" goto endDetectBaseDir

@set EXEC_DIR=%CD%
@set WWRAPPER_JAR="%EXEC_DIR%\.mvn\wrapper\maven-wrapper.jar"
@set WWRAPPER_PROPERTIES="%EXEC_DIR%\.mvn\wrapper\maven-wrapper.properties"

@REM Start the server using the compiled classes if possible,
@REM but since we are in Android Studio, let's use the simplest Spring Boot run command.
@REM NOTE: This script assumes you have Java installed.

cd backend
java -cp "target/classes;target/dependency/*" com.deeploft.backend.BackendApplication
@if ERRORLEVEL 1 goto error
@goto end

:error
@set ERROR_CODE=1

:end
@setlocal DisableDelayedExpansion
@exit /B %ERROR_CODE%
