@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  fitting-web startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and FITTING_WEB_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\fitting-web-2.0.1.jar;%APP_HOME%\lib\calculation-client-1.0.0.jar;%APP_HOME%\lib\wicket-bootstrap-extensions-0.10.16.jar;%APP_HOME%\lib\wicket-guice-7.9.0.jar;%APP_HOME%\lib\wicket-bootstrap-themes-0.10.6.jar;%APP_HOME%\lib\wicket-ioc-7.9.0.jar;%APP_HOME%\lib\wicket-bootstrap-core-0.10.16.jar;%APP_HOME%\lib\wicket-extensions-7.9.0.jar;%APP_HOME%\lib\wicket-webjars-0.5.6.jar;%APP_HOME%\lib\wicket-core-7.9.0.jar;%APP_HOME%\lib\wicket-request-7.9.0.jar;%APP_HOME%\lib\wicket-util-7.9.0.jar;%APP_HOME%\lib\commons-fileupload-1.3.2.jar;%APP_HOME%\lib\commons-io-2.5.jar;%APP_HOME%\lib\commons-lang-2.6.jar;%APP_HOME%\lib\slf4j-log4j12-1.7.25.jar;%APP_HOME%\lib\log4j-1.2.17.jar;%APP_HOME%\lib\guice-4.0.jar;%APP_HOME%\lib\opencsv-2.4.jar;%APP_HOME%\lib\jetty-webapp-9.3.9.v20160517.jar;%APP_HOME%\lib\jetty-servlet-9.3.9.v20160517.jar;%APP_HOME%\lib\jetty-security-9.3.9.v20160517.jar;%APP_HOME%\lib\jetty-server-9.3.9.v20160517.jar;%APP_HOME%\lib\javax.servlet-api-3.1.0.jar;%APP_HOME%\lib\vavr-gson-0.9.1.jar;%APP_HOME%\lib\vavr-0.9.1.jar;%APP_HOME%\lib\akka-actor_2.12-2.5.12.jar;%APP_HOME%\lib\gson-fire-1.8.0.jar;%APP_HOME%\lib\gson-2.8.1.jar;%APP_HOME%\lib\swagger-annotations-1.5.15.jar;%APP_HOME%\lib\logging-interceptor-2.7.5.jar;%APP_HOME%\lib\okhttp-2.7.5.jar;%APP_HOME%\lib\threetenbp-1.3.5.jar;%APP_HOME%\lib\slf4j-api-1.7.25.jar;%APP_HOME%\lib\javax.inject-1.jar;%APP_HOME%\lib\aopalliance-1.0.jar;%APP_HOME%\lib\closure-compiler-v20130603.jar;%APP_HOME%\lib\jquery-selectors-0.2.5.jar;%APP_HOME%\lib\guava-19.0.jar;%APP_HOME%\lib\jetty-http-9.3.9.v20160517.jar;%APP_HOME%\lib\jetty-io-9.3.9.v20160517.jar;%APP_HOME%\lib\jetty-xml-9.3.9.v20160517.jar;%APP_HOME%\lib\vavr-match-0.9.1.jar;%APP_HOME%\lib\commons-collections4-4.1.jar;%APP_HOME%\lib\Eonasdan-bootstrap-datetimepicker-4.17.37-1.jar;%APP_HOME%\lib\x-editable-bootstrap-1.5.1.jar;%APP_HOME%\lib\bootstrap-3.3.7-1.jar;%APP_HOME%\lib\modernizr-2.8.3.jar;%APP_HOME%\lib\joda-time-2.3.jar;%APP_HOME%\lib\commons-lang3-3.4.jar;%APP_HOME%\lib\maven-parent-config-0.3.5.jar;%APP_HOME%\lib\momentjs-2.14.1.jar;%APP_HOME%\lib\font-awesome-4.7.0.jar;%APP_HOME%\lib\jquerypp-1.0.1.jar;%APP_HOME%\lib\jquery-ui-1.11.4.jar;%APP_HOME%\lib\typeaheadjs-0.10.4.jar;%APP_HOME%\lib\spin-js-2.1.0.jar;%APP_HOME%\lib\animate.css-3.3.0.jar;%APP_HOME%\lib\summernote-0.8.1.jar;%APP_HOME%\lib\scala-java8-compat_2.12-0.8.0.jar;%APP_HOME%\lib\scala-library-2.12.5.jar;%APP_HOME%\lib\config-1.3.2.jar;%APP_HOME%\lib\okio-1.6.0.jar;%APP_HOME%\lib\jetty-util-9.3.9.v20160517.jar;%APP_HOME%\lib\cglib-3.1.jar;%APP_HOME%\lib\asm-util-5.0.3.jar;%APP_HOME%\lib\args4j-2.0.16.jar;%APP_HOME%\lib\protobuf-java-2.4.1.jar;%APP_HOME%\lib\json-20090211.jar;%APP_HOME%\lib\jsr305-1.3.9.jar;%APP_HOME%\lib\jquery-1.11.1.jar;%APP_HOME%\lib\asm-tree-5.0.3.jar;%APP_HOME%\lib\asm-5.0.3.jar;%APP_HOME%\lib\jackson-databind-2.8.8.jar;%APP_HOME%\lib\jackson-core-2.8.8.jar;%APP_HOME%\lib\jackson-annotations-2.8.0.jar

@rem Execute fitting-web
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %FITTING_WEB_OPTS%  -classpath "%CLASSPATH%" ch.unibas.fitting.Main %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable FITTING_WEB_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%FITTING_WEB_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
