@ECHO OFF
IF NOT "%~f0" == "~f0" GOTO :WinNT
@"jruby-complete-1.5.6.jar" "C:/tools/maven-2.2.1/localrepository/.jruby/bin/cdiff" %1 %2 %3 %4 %5 %6 %7 %8 %9
GOTO :EOF
:WinNT
@"jruby-complete-1.5.6.jar" "%~dpn0" %*
