Toast Lifecycle Documentation
-----
Welcome to the documentation for the Toast module loading and execution Lifecycle. This document is intended to demonstrate the load process in an easy-to-understand manner and explain in depth why it was chosen to be the way it is. This may look like a lot of phases, but in reality it only takes a few seconds to setup and the phases can be extremely useful in debugging.

# Table of Contents
- [Introduction](#introduction)
  - [Sections](#sections)
- [Phases](#phases)
  - [Bootstrap](#bootstrap)
  - [Core Pre-Init](#core-pre-initialization)
  - [Core Init](#core-initialization)
  - [Pre-Init](#pre-initialization)
  - [Init](#initialization)
  - [Prestart](#pre-start)
  - [Start](#start)
  - [Complete](#complete)
- [Interfacing](#load-phase-interfacing)

# Introduction
The Toast Loading lifecycle is split into **Load Phases.** Load Phases are points in the program that dictate what is currently happening. These exist to make debugging simple, as well as organize the code and make it easy to read the 'Load Status' of the robot if it were to crash or be unusually slow during initialization. The Load Phases are depicted by the following diagram.

![The Toast Loading Lifecycle](https://raw.githubusercontent.com/Open-RIO/ToastAPI/master/doc/resources/lifecycle.png)

### Sections
  - The **blue** section of the diagram (labelled 'Core') is everything that is done before WPILib is initialized. This is where Toast does it's setup, as well as loads any 'Core Modules' that are required. Loggers, Crash Handlers, Preferences among other things are all loaded in this section.
  - The **red** section (labelled 'Expansion') is everything that is loaded during/after WPILib. This is where modules are loaded and services started. This is where most of your code will be interacting.

  - The **dark** section (labelled 'Modules') is the subset of Load Phases in which Modules can interact. This includes Core Modules as well as regular modules and scripts. Cross-Sectioning the 'Modules' and 'Expansion' sections will yield the section most Toast Modules are loaded.

# Phases

## Bootstrap
The 'Bootstrap' phase is used to initialize the Bare Essentials of Toast. This includes parsing the command line arguments, creating the home file and starting the Logger and Crash Handler to ensure any and all crashes are recorded. Toast's Versioning system is also initialized here. Keep in mind the ClassPatcher is not started here.  

## Core Pre-Initialization
The 'Core Pre-Initialization' phase *(Core PreInit for short)* is used to preload Core Modules. Core Modules are modules for Toast that are loaded before WPILib and as such gain much more control over the Robot's lifecycle. The Core PreInit phase is used to 'preload' these modules, i.e. discover their files, load them, and instantiate the 'preinit' method in their main class. This 'preinit' phase is used for initial setup of the Module, such as creating file paths, assigning constants and other things of the like. If your Core Module functions as an API, this should be where all your Hooks are instantiated so they can be accessed by other modules in the following 'Init' phase.  

## Core Initialization
The 'Core Initialization' phase *(Core Init for short)* is used to load the main bulk of functions inside of a Core Module. As described by the Core Pre-Initialization phase, Core Modules are loaded immediately after Toast's Bare Essentials and before WPILib. The Initialization phase is intended to be used after the PreInit phase in the hopes that 'API' or 'Library' modules have already instantiated their hooks and required setup in PreInit so they can be reliably accessed in Init. Init is the 'everything is setup and ready to go' phase.  

## Pre-Initialization
The 'Pre-Initialization' phase *(PreInit for short)* is the phase used to load Toast's other utilities that should be instantiated before WPILib but after Core Modules. This includes services and utilities such as the ClassPatcher, Preferences, ThreadPool and USBMassStorage. This is intended to make sure that Toast is loaded properly before WPILib. The ClassPatcher will start to load patches if the Robot is detected to be in Simulation and will also start displaying the GUI at this time.  

## Initialization
The 'Initialization' phase *(Init for short)* is where WPILib is loaded. At this point, the ClassPatcher has applied its patches and the GUI (if required) has been started. WPILib will now continue it's RobotBase initialization and instantiate Toast's main class when complete. This phase is largely uncontrolled by Toast, but any crashes or issues will be logged and dealt with safely.  

## Pre-Start
The 'Prestart' phase is started after WPILib has completed it's loading. This is where modules are loaded and put into their 'prestart' phase. This phase is called before WPILib sends the control packet that the robot is 'Ready To Go', so mostly setup is done here. Similar to Core Modules, the prestart phase is intended to be used for setting up things like the File System, Hooks and Listeners (StateTracker, Heartbeat) that are required before the robot starts. Additionally, if your module is an API or Library, your API instantiations should be done here so that any modules referencing them in Init are safe. The 'prestart' method in the ToastModule class is called during this time, and as such, it respects the MethodExecutor's '@Priority' annotation. At the end of this phase, the robot is classified as 'ready'  

## Start
The 'Start' phase is called once WPILib has completed Robot-Setup and after the Prestart phase. The robot has already been classified and 'ready' and this phase is intended to contain the bulk of your 'start' code. At this phase, API Modules will have loaded and instantiated their hooks, filesystems will be setup as well as any other initial 'essential' setup, meaning they are safe for you to access. During this phase you should start to setup your Motor Controllers, Digital IO, Driver Station and other interfaces relating to your robot's front-end functions. This phase is also where Toast starts it's SocketManager, System Monitors and other post-setup activities. After this phase is complete, Toast will start the StateTracker and the Robot will be completely functional.  

## Complete
The 'Complete' phase is used to signify that all setup of the robot is done and iterative code can begin. At this point the StateTracker will begin ticking and will not stop until the shutdown hooks are called, or power is lost to the Robot.

# Load Phase Interfacing
Load Phases can be interacted and interfaced with. The LoadPhase class in the 'lib' package contains all the necessary hooks for your Module to listen for changes in Load Phases. This exists so Core Modules can instantiate different code at different Load Phases, as well as track what state we are in for things like Loading Indicators or Statistics/Analytics.
