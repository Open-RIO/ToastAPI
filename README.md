# ToastAPI
An expandable, Open Source and Cross-Platform Robot API for FRC built on WPILib  

Master | Production | Development  
[![Build status](https://travis-ci.org/Open-RIO/ToastAPI.svg?branch=master)](https://travis-ci.org/Open-RIO/ToastAPI/)
[![Build status](https://travis-ci.org/Open-RIO/ToastAPI.svg?branch=production)](https://travis-ci.org/Open-RIO/ToastAPI/)
[![Build status](https://travis-ci.org/Open-RIO/ToastAPI.svg?branch=development)](https://travis-ci.org/Open-RIO/ToastAPI/)  
## What is Toast?
Toast is an API designed for the RoboRIO and teams competing in the FIRST Robotics Competition. Toast is built on top of WPILib and provides useful tools for increased stability, ease of use and usability. Toast also features a full Robot Simulation tool, meaning Robot Code can be tested at school, home, or even on the plane to the next match.

Toast is modular, and is designed with a core. This core is loaded no matter what and contains essential tools such as Logging, Crash Handling and of course, Module Loading. Modules are code that use Toast and WPILIb as a base API and can be loaded/unloaded at will. These are stored in .jar files much like any other program and are loaded at Runtime. This allows for an extremely modular workflow in Toast.  

Teams create their own Module to control their robot, but might choose to load other modules as well, such as a WebUI, Autonomous Recorder or even Vision Tracking. Modules can be optional, or depend on each other. This allows for the FIRST community to share their code and creations on a whole new level. A brief visual representation of how Toast loads and organized modules is given below:
![The 'pipline' of how Toast Modules work](https://raw.githubusercontent.com/Open-RIO/ToastAPI/master/doc/Pipeline.png)

## Simulation
Simulation is part of the Toast Core, and is designed for people working in Development Environments to easily test their code instantly, instead of waiting for the code to deploy to the robot and waiting again for it to restart. Toast will dynamically patch WPILib classes at Runtime if running in a Developement Environment and allow for all the Inputs, Outputs and functions of the RoboRIO to be simulated. An example of the Simulation GUI is given below:  

![An early prototype of the Simulation GUI for Toast](https://raw.githubusercontent.com/Open-RIO/ToastAPI/master/doc/SimulationGUI.png)

Ports such as DIO and Analog IN gain a Number Spinner, that allows the inputs to be changed. These spinners are enabled when they are registered through WPILib (DigitalInput/AnalogInput classes). Other things, such as PWM output, can be read as their raw values. This allows for the robots IO to be completely simulated. Additionally, the Simulation GUI will have support for XBox controllers and external Joysticks. In the future, simulated controllers will be supported through an optional module.  

## Other Tools
Toast has support for many other tools out of the box as well. For example, Toast can load Groovy files and execute them. This means that teams can program
their entire robot in the Groovy Programming Language. Groovy doesn't need to be compiled, which has the added benefit of not having to rebuild and
redeploy your code each time you change something, instead, you can use editors like Sublime Text or Atom to remotely edit the script file and have
your code ready within a few seconds.  

Groovy is also used for Toast's Configuration Files. These .groovy config files can be used alongside normal java programming, and can load in variables
defined in the groovy file. Methods can also be invoked in these config files if you so choose  
![A demo of the Groovy Config Files](http://puu.sh/gpZC5/bd99a3242a.png)

## Debugging Tools
The Toast Core has inbuilt debugging tools included by default. This includes a FileLogger, that will split the System.out and System.err streams between the Console and a File. This allows for Logs to be recorded. Additionally, when the Robot is detected to have crashed, Toast will shutdown safely and save the Crash Log to a file, as well as identifying possible culprit modules that caused the crash. This allows for debugging to be done quick and easily.  An example crash log has been posted below:
```
**** CRASH LOG ****
Your robot has crashed. Following is a crash log and more details.
This log has been saved to: D:\Programming\FRC\OpenRIO\ToastWebUI\toast\crash\crash-2015-02-19_03-34-17.txt
	Suspected Culprits for this Crash are: Toast

java.lang.ArithmeticException: / by zero
	at jaci.openrio.toast.extension.webui.ModuleWebUI.start(ModuleWebUI.java:28)
	at jaci.openrio.toast.core.loader.RobotLoader.start(RobotLoader.java:134)
	at jaci.openrio.toast.core.Toast.startCompetition(Toast.java:91)
	at edu.wpi.first.wpilibj.RobotBase.main(RobotBase.java:189)
	at jaci.openrio.toast.core.ToastBootstrap.main(ToastBootstrap.java:58)

Crash Information:
	Toast:
		Loaded Modules:
			WebUI@0.0.1

		Environment Status:
			Simulation


*******************
```

Additionally, Toast uses the GradleRIO build system, allowing for robot code to be deployed through command line, and any IDE/Operating System can be used with Toast. A debugging server can also be hosted on the RoboRIO using 'gradlew modeDebug' that will allow IDEs to connect to the debugging server and hotswap code live, as well as add breakpoints and monitor code in-depth.

## Deploying Toast
To deploy the latest (stable) version of Toast to the RoboRIO, follow these steps:
- Download the latest [release](https://github.com/Open-RIO/ToastAPI/releases) named Toast-Deployment-Utility
- Unzip the file to its own folder
- Launch 'Toast-Deployment.jar'
- Click 'Download'. When the download is complete, connect to the same network as your robot and click 'Deploy'
- Done!  

NOTE: If the computer you are using does not have a GUI, use the following steps:
- When unzipped, open a command prompt and type:
     - './deployToast' for Mac/Linux  
     - 'deployToast.bat' for Windows  
     - NOTE: Mac/Linux users may be required to run 'chmod 777' on the deployToast file and gradle directory if you are given permissions errors

- Follow the instructions presented

If you wish to build Toast from SRC and deploy it yourself, it's very simple.
- Fork this Repo
- Mirror this Repo on your local machine
- Run `gradlew wpi`, followed by `gradlew eclipse` or `gradlew idea`, depending on your development environment  
-   NOTE: use `./gradlew` if you are on Linux or Mac OS X
- Change the team number in build.gradle to match your own
- Connect to the same WiFi network as your RoboRIO and run `gradlew deploy`
- Congratulations! Now your RoboRIO is equipped with the Toast Core.
- Want to remove toast? We'll miss you, but just deploy your own robot program and it will override Toast

## Creating Modules
Creating Modules for Toast is really simple. Download Toast from the [releases page](https://github.com/Open-RIO/ToastAPI/releases) and unzip it.  
Alternatively, you can copy releases/ if you already have the Toast SRC on your computer.  
To setup your development environment and deploy to your Robot, do the following:
- Run `gradlew eclipse` if using Eclipse, or `gradlew idea` if using IntelliJ IDEA
-   NOTE: use `./gradlew` if you are on Linux or Mac OS X
- Point your eclipse or IntelliJ workspace to this directory and use src/main/java as your sources directory
- Make your Main robot class extend ToastModule instead of RobotBase

#### Deploying
Before deploying, there is some setup to do...
- Edit your build.gradle file so that archivesBaseName is set to your Module name, and gradlerio.team is equal to your team number
- Save your build.gradle file and run `gradlew deploy`
- Congrats! Your robot now has your module on it!

#### Simulation
To run the robot simulation, do the following:
- Change your Run Configuration to include the arguments ```-sim your.main.robot.class```
- 	NOTE: Your robot class should include the package!
- Run your Run Configuration and bam! You're done!

## Reporting Bugs
Find something wrong with Toast? Think it needs more butter, or just isn't Toasted enough? Great! Head over to our [issues page](https://github.com/Open-RIO/ToastAPI/issues) and see if your issue has already been submitted. If it hasn't, go ahead and hit 'new issue' and give us a detailed description of what's wrong. If possible, we'd like you to include as much of the following information possible so we can track down that bug and squash it as quickly as possible.  
- A brief description of the issue (e.g. During teleop the robot crashes for no apparent reason)
- How severe the crash is (e.g. It freezes for a few seconds, it crashes, help my roborio is on fire)
- A crash log if available (crash logs can be found in toast/crash/)
- If needed, a regular console log would be helpful (found in toast/log/)
- A list of modules, as well as what version of Toast you're on (module list isn't required if the robot has crashed, this is included in the crash log)
- Your thoughts on the bug (Think you know what caused it? Spill the beans!)

Got a feature request? Submit a new issue, but apply the 'Feature Request' label to it. We'll be sure to look at your suggestions!

Thanks for submitting your bugs and helping us improve Toast!

## Contributions
We're Open Source, which means we're open to contributions. Got something you want to add, or know how to fix an issue? Go ahead! Fork our repo and change it to your heart's desire. Just be sure to stick to the following terms:
- Stick to the license. This means ours, as well as that of WPILib
- Document it. If you're adding something new, add some JavaDoc comments to your class. Something descriptive and brief
- Credit yourself! Add @author to your JavaDoc comments so people can thank you
- Submit it to us via Pull Request. This makes it easy for us to merge your changes

Thanks for helping improving Toast!
