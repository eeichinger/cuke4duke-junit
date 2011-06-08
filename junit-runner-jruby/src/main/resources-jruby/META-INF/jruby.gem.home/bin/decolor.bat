@ECHO OFF
IF NOT "%~f0" == "~f0" GOTO :WinNT
@"jruby-complete-1.6.1.jar" "c:/tools/maven-2.2.1/localrepository/.jruby/bin/decolor" %1 %2 %3 %4 %5 %6 %7 %8 %9
GOTO :EOF
:WinNT
@"jruby-complete-1.6.1.jar" "%~dpn0" %*
