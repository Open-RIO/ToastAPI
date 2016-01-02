@echo off
REM This is a little file that sets up my (Jaci's) personal development environment for Toast, building from WPI's git repo. Change the values below to suit your system.
REM This lets me start developing for the 2016 season early, and also get things like NetworkTables 3.0 working.

REM If you're going to use this file, copy it somewhere that isn't tracked by Git so it doesn't commit if you make a pull request. This is meant as a template.

set WPI_BUILD_DIR=D:\Programming\FRC\WPILib\all_wpi_pre2016\allwpilib\wpilibj\build\libs
set WPI_BUILD_ARTIFACT=:wpilibj
set NT_BUILD_DIR=D:\Programming\FRC\WPILib\all_wpi_pre2016\ntcore\native\build\libs
set NT_BUILD_ARTIFACT=:ntcore-windows

gradlew %*