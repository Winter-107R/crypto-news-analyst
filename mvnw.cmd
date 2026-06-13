@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup batch script
@REM ----------------------------------------------------------------------------
@IF "%__MVNW_ARG0_NAME__%"=="" (SET "MVN_CMD=mvn.cmd") ELSE (SET "MVN_CMD=%__MVNW_ARG0_NAME__%")
@SET MAVEN_PROJECTBASEDIR=%~dp0
@SET MAVEN_WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar"
@SET MAVEN_WRAPPER_PROPERTIES="%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.properties"
@SET DOWNLOAD_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"

@FOR /F "usebackq tokens=1,2 delims==" %%A IN (%MAVEN_WRAPPER_PROPERTIES%) DO (
    @IF "%%A"=="distributionUrl" SET DISTRIBUTION_URL=%%B
    @IF "%%A"=="wrapperUrl" SET DOWNLOAD_URL=%%B
)

@IF NOT "%MVNW_VERBOSE%"=="" (
  @ECHO distributionUrl=%DISTRIBUTION_URL%
  @ECHO wrapperUrl=%DOWNLOAD_URL%
)

@SET JAVA_HOME_PARENT=%JAVA_HOME%
@IF "%JAVA_HOME_PARENT%"=="" @SET JAVA_HOME_PARENT=%~dp0jdk

@IF EXIST %MAVEN_WRAPPER_JAR% (
    @SET MVNW_VERBOSE=false
) ELSE (
    @ECHO Downloading Maven Wrapper jar from %DOWNLOAD_URL%
    @IF NOT "%MVNW_VERBOSE%"=="" @ECHO Downloading to: %MAVEN_WRAPPER_JAR%
    @CALL "%JAVA_HOME%\bin\java.exe" -classpath "%MAVEN_PROJECTBASEDIR%.mvn\wrapper" ^
        org.apache.maven.wrapper.MavenWrapperDownloader ^
        "%DOWNLOAD_URL%" "%MAVEN_WRAPPER_JAR%"
    @IF ERRORLEVEL 1 (
        @ECHO Failed to download Maven Wrapper jar. Trying curl...
        @curl -o "%MAVEN_WRAPPER_JAR%" "%DOWNLOAD_URL%" 2>NUL
    )
)

@"%JAVA_HOME%\bin\java.exe" ^
  %JVM_CONFIG_MAVEN_PROPS% ^
  %MAVEN_OPTS% ^
  %MAVEN_DEBUG_OPTS% ^
  -classpath %MAVEN_WRAPPER_JAR% ^
  "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" ^
  org.apache.maven.wrapper.MavenWrapperMain %*
