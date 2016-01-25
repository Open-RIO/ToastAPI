#![](doc/resources/logo_tiny_text.png)
An expandable, Open Source and Cross-Platform Robot API for FRC built on WPILib.

### Now ready for the 2016 FIRST Robotics Competition!


| Branch | Windows | Linux / Mac |
|--------|---------|-------------|
| Master | [![Build status](https://ci.appveyor.com/api/projects/status/l44bg3bl8o75osgt/branch/master?svg=true)](https://ci.appveyor.com/project/JacisNonsense/toastapi/branch/master) | [![Build Status](https://travis-ci.org/Open-RIO/ToastAPI.svg?branch=master)](https://travis-ci.org/Open-RIO/ToastAPI) |
| Development | [![Build status](https://ci.appveyor.com/api/projects/status/l44bg3bl8o75osgt/branch/development?svg=true)](https://ci.appveyor.com/project/JacisNonsense/toastapi/branch/development) | [![Build Status](https://travis-ci.org/Open-RIO/ToastAPI.svg?branch=development)](https://travis-ci.org/Open-RIO/ToastAPI) |

[![Come chat, or just hang out](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/Open-RIO/ToastAPI?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)  

[Current Whitepaper](http://www.chiefdelphi.com/media/papers/3174)  
[Current Metrics](https://dev.imjac.in/toast/metrics/)  


# Installing Toast
Toast installation and Module Development Instructions can be found in the [Toast Wiki](https://github.com/Open-RIO/ToastAPI/wiki/Creating-A-Toast-Module----Start-to-Finish)  
  
If you're releasing your module source files, whether it be on Github or anywhere else, it is recommended to read [this wiki article](https://github.com/Open-RIO/ToastAPI/wiki/Open-Sourcing-your-Toast-Module)

# What is Toast?
Toast is a modular robotics framework for the *FIRST* Robotics Competition, built atop WPILib.

Toast provides base code for your Robotics Program to build on top of. The Toast JAR (packaged with WPILib) is kept on the RoboRIO,
with your code (and other modules) stored as other JAR files for Toast to load. This allows for code to be modular, and for teams
to collaborate and share their code like never before. For example, one team may decide to release a module for Vision Targeting. For
other teams, installing this module onto their RoboRIO is as simple as copying the file to your RoboRIO. In fact, we'll do it for you.

Even better, Toast has a full simulation environment. This allows motor controllers, pneumatics, and just about anything on the Robot to 
be simulated without any external tools. The code you simulate is the exact same code that is sent to your Robot, so you can be confident that
it will work.

Below is a simple graph representing how Toast works.
![](doc/resources/Pipeline.png)

# Simulation
Below is an example of the Toast Simulation GUI
![](doc/resources/SimulationGUI.png)

The Toast Simulation GUI redirects calls to regular WPILib code to a simulation GUI for you to view. This GUI is launched straight from your IDE and
requires no extra code. Simulation is available on Linux, Mac OS X and Windows platforms, both 32 and 64 bit. Other platforms will need to build the NetworkTables-Core
library manually.

Simulation also has support for the 2016 FRC Driver Station. Instructions for the Driver Station can be found [here](https://github.com/Open-RIO/ToastAPI/wiki/Simulating-Joysticks-and-the-Driver-Station)

# USB
SSH'ing into the RoboRIO to copy modules isn't always the most pleasant experience. To remedy this, Toast has full support onboard for USB Mass Storage
devices. Modules can be put on a USB device that is connected to your RoboRIO's USB Ports. Any modules will be loaded from this device, and can even override
the modules on the RoboRIO if you want to. Furthermore, log files, crash reports, and configuration files are all stored on the USB Device if present, allowing
for quick and easy debugging of your Robot Program.

# Logging and Crash Reports
Toast will helpfully log all console output to file, and even provides a logging framework for you to use to format your logs
neatly. An example "just-started-up" log is seen below.
```
.________    ______                 __
((       )  /_  __/___  ____ ______/ /_
||  o o  |   / / / __ \/ __ `/ ___/ __/
||   3   |  / / / /_/ / /_/ (__  ) /_
\\_______/ /_/  \____/\__,_/____/\__/

[05/07/15-06:46:03] [Toast] [Bootstrap] [INFO] Toast Version: 2.0.0
[05/07/15-06:46:03] [Toast] [Core-Initialization] [INFO] Toast Started with Run Arguments: [-sim, --search]
[05/07/15-06:46:03] [Toast] [Pre-Initialization] [INFO] Slicing Loaf...
[05/07/15-06:46:05] [Toast] [Initialization] [INFO] Nuking Toast...
[05/07/15-06:46:05] [Toast] [Pre-Start] [INFO] Buttering Bread...
[05/07/15-06:46:05] [Toast] [Start] [INFO] Fabricating Sandwich...
[05/07/15-06:46:05] [Toast] [Start] [INFO] Verdict: Needs more salt
[05/07/15-06:46:05] [Toast] [Main] [INFO] Total Initiation Time: 2.98 seconds
```
This log output can also be viewed in the official 2016 FRC Driver Station!

Additionally, Crash Logs are also formatted whenever the Robot encounters an unexpected exception. An example crash log
can be seen below. All Crash Logs are saved to file with a date/time stamp.

```
**** CRASH LOG ****
Your robot has crashed. Following is a crash log and more details.
This log has been saved to: D:\Programming\FRC\OpenRIO\Toast\run\toast\crash\crash-2015-07-05_06-48-37.txt
 ________     __  ____       ____  __
((       )   / / / / /_     / __ \/ /_
||  x x  |  / / / / __ \   / / / / __ \
||   ^   | / /_/ / / / /  / /_/ / / / /
\\_______/ \____/_/ /_/   \____/_/ /_/

java.lang.Exception: Invoked Debug Crash
    at jaci.openrio.toast.core.command.cmd.CommandInvokeCrash.invokeCommand(CommandInvokeCrash.java:35)
    at jaci.openrio.toast.core.command.CommandBus.parseMessage(CommandBus.java:78)
    at jaci.openrio.toast.core.command.CommandBus$1.run(CommandBus.java:167)

Crash Information:
    Toast:
        Toast Version: 2.0.0
        Loaded Modules:

    Environment:
            Toast: 2.0.0
             Type: Simulation
              FMS: false
               OS: Windows 8.1 6.3 (amd64)
             Java: 1.8.0_25 (Oracle Corporation)
        Java Path: C:\Program Files\Java\jdk1.8.0_25\jre
          JScript: Supported (Nashorn)


*******************
```

# Configurations
Instead of redeploying code each time you tweak a value (such as PID), Toast provides a way to make these changes simpler. Using the `ModuleConfig` class, you
can create a Configuration File for your Toast Module to use. Configuration Files are stored in .json format, and can be changed very easily over SSH. Toast's Configuration File
can be seen below.
```json
{
	"delegate":{
		"logger":{
			"password":"",
			"algorithm":"SHA256"
		},
		"command":{
			"password":"",
			"algorithm":"SHA256"
		}
	},
	"robot":{
		"name":"{ NAME NOT SET }",
		"team":-1,
		"desc":"{ DESCRIPTION NOT SET }"
	},
	"security":{
		"policy":"STRICT"
	},
	"optimization":{
		"gc":{
			"time":30,
			"enabled":false
		}
	},
	"sim":{
		"ds":{
			"enabled":true
		},
		"bonjour":{
			"enabled":false,
			"target":"9999"
		}
	},
	"threading":{
		"pool_size":2
	},
	"javascript":{
		"autoload":["main.js"]
	}
}

```

# Commands
Toast introduces a CommandBus. The CommandBus allows for commands to be sent to the RoboRIO via GradleRIO, STDIN (for Simulation), the Network, or through your own implementation. These
commands, similar to any shell, will be interpreted by Toast and passed to the relevant code. This allows you to create 'commands' and register on the CommandBus to control your robot with a text-based
prompt. For example, a team may use the CommandBus to define the layout of the Outer Works in the 2016 Stronghold Tournament *(hint, hint)*.

# JavaScript
Toast has inbuilt support for the Nashorn JavaScript Engine. Scripts can be saved to a `.js` file and Toast will run them at startup, or code
can be interpreted LIVE, while the Robot/Simulation is running, to run code on the fly via the `js` command.

# Reporting Bugs
Find something wrong with Toast? Think it needs more butter, or just isn't Toasted enough? Great! Head over to our [issues page](https://github.com/Open-RIO/ToastAPI/issues) and see if your issue has already been submitted. If it hasn't, go ahead and hit 'new issue' and give us a detailed description of what's wrong. If possible, we'd like you to include as much of the following information possible so we can track down that bug and squash it as quickly as possible.  
- A brief description of the issue (e.g. During teleop the robot crashes for no apparent reason)
- How severe the crash is (e.g. It freezes for a few seconds, it crashes, help my roborio is on fire)
- A crash log if available (crash logs can be found in toast/crash/)
- If needed, a regular console log would be helpful (found in toast/log/)
- A list of modules, as well as what version of Toast you're on (module list isn't required if the robot has crashed, this is included in the crash log)
- Your thoughts on the bug (Think you know what caused it? Spill the beans!)

Got a feature request? Submit a new issue, but apply the 'Feature Request' label to it. We'll be sure to look at your suggestions!

Thanks for submitting your bugs and helping us improve Toast!

# Contributions
We're Open Source, which means we're open to contributions. Got something you want to add, or know how to fix an issue? Go ahead! Fork our repo and change it to your heart's desire. Just be sure to stick to the following terms:
- Stick to the license. This means ours, as well as that of WPILib
- Document it. If you're adding something new, add some JavaDoc comments to your class. Something descriptive and brief
- Credit yourself! Add @author to your JavaDoc comments so people can thank you
- Submit it to us via Pull Request. This makes it easy for us to merge your changes

Don't forget, we also have a [Contribution Guide](https://github.com/Open-RIO/ToastAPI/wiki/Contributing-to-Toast) to make sure you setup your Development Environment correctly.

Thanks for helping improving Toast!  

# Why did I call it 'Toast'?  
Toast is modular. So is regular Toast. Want butter? Go ahead, add it! Jam more your style? Whatever, jam it up, baby! Want to make a sandwich? Make the best sandwich the world has ever seen.  
