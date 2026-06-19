@REM Maven Wrapper Script for Windows
@REM Downloads Apache Maven on first run and caches it in %USERPROFILE%\.m2\wrapper\dists\

@echo off
setlocal enabledelayedexpansion

set SCRIPT_DIR=%~dp0
set WRAPPER_PROPERTIES=%SCRIPT_DIR%.mvn\wrapper\maven-wrapper.properties

if not exist "%WRAPPER_PROPERTIES%" (
  echo ERROR: %WRAPPER_PROPERTIES% not found. >&2
  exit /b 1
)

for /f "tokens=1,* delims==" %%a in ('findstr /b "distributionUrl=" "%WRAPPER_PROPERTIES%"') do (
  set DISTRIBUTION_URL=%%b
)

if "%DISTRIBUTION_URL%"=="" (
  echo ERROR: distributionUrl not set in %WRAPPER_PROPERTIES% >&2
  exit /b 1
)

for %%F in ("%DISTRIBUTION_URL%") do set DIST_FILENAME=%%~nxF
set MAVEN_VERSION=%DIST_FILENAME:-bin.zip=%
set DIST_CACHE_DIR=%USERPROFILE%\.m2\wrapper\dists\%MAVEN_VERSION%
set MAVEN_HOME=%DIST_CACHE_DIR%\%MAVEN_VERSION%

if not exist "%MAVEN_HOME%" (
  echo Downloading %DIST_FILENAME% ...
  if not exist "%DIST_CACHE_DIR%" mkdir "%DIST_CACHE_DIR%"
  set TMP_FILE=%DIST_CACHE_DIR%\%DIST_FILENAME%.tmp
  powershell -Command "Invoke-WebRequest -Uri '%DISTRIBUTION_URL%' -OutFile '!TMP_FILE!'"
  echo Extracting %DIST_FILENAME% ...
  powershell -Command "Expand-Archive -Path '!TMP_FILE!' -DestinationPath '%DIST_CACHE_DIR%' -Force"
  del "!TMP_FILE!"
)

set MVN_CMD=%MAVEN_HOME%\bin\mvn.cmd
if not exist "%MVN_CMD%" (
  echo ERROR: mvn not found at %MVN_CMD% >&2
  exit /b 1
)

"%MVN_CMD%" %*
